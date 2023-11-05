
package de.klassenserver7b.danceinterpreter.songprocessing.dataprovider;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.nio.file.ExtendedOpenOption;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.loader.FileLoader;
import de.klassenserver7b.danceinterpreter.songprocessing.DanceInterpreter;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
 * @author K7
 */
public class LocalSongDataProvider implements SongDataProvider {

    private final Logger log;
    private int hash;
    private int datahash;

    /**
     *
     */
    public LocalSongDataProvider() {
        log = LoggerFactory.getLogger(this.getClass());
        hash = 0;
        datahash = 0;
    }

    /**
     *
     */
    @Override
    public SongData provideSongData() {
        boolean force = false;
        return provideParameterizedData(getLocalSong(force), force);
    }

    protected SongData provideParameterizedData(File f, boolean provideforced) {

		SongData ret = null;
		DanceInterpreter danceI = Main.Instance.getDanceInterpreter();

        ret = FileLoader.getDataFromFile(f);

        if (ret == null || (datahash == ret.hashCode() && !provideforced)) {
            return null;
        }

        if (ret.getDance() == null) {
            danceI.addSongtoJSON(ret, "LOCAL");
        }

        datahash = ret.hashCode();
        return ret;

    }

    /**
     * Gets currently playing song by checking for locked files
     * Asks user to select if multiple files are locked
     * <br> <strong>HUAN</strong>
     * <b>WARNING: </b> Works only on Windows based OS
     *
     * @param provideforced Whether to not forcefully refresh the song if the new locked files list's hash matches the old one
     * @return File of the currently playing song or null if no song is playing / the hashes match
     */
    private File getLocalSong(boolean provideforced) {
        List<File> blocked = getBlockedFiles();

        if (blocked.isEmpty()) {
            return null;
        }
        if (!provideforced && blocked.hashCode() == hash) {
            return null;
        }

        this.hash = blocked.hashCode();
        if (blocked.size() == 1) {
            return blocked.get(0);
        }

        ConcurrentHashMap<String, File> fileoptions = new ConcurrentHashMap<>();

        for (File f : blocked) {
            fileoptions.put(f.getName(), f);
        }

        String filename = (String) JOptionPane.showInputDialog(null, "Which is the current Song?",
                "Please select current song!", JOptionPane.QUESTION_MESSAGE, null,
                fileoptions.keySet().toArray(new String[0]), null);

        if (filename == null) {
            return null;
        }
        return fileoptions.get(filename);
    }

    /**
     * Iterates over DanceInterpreter.getFiles(), checks whether a file is currently locked / used by another process, then returns a list of all locked files
     *
     * @return List of locked files
     */
    private List<File> getBlockedFiles() {
        List<File> blocked = new ArrayList<>();

        DanceInterpreter danceI = Main.Instance.getDanceInterpreter();
        for (File file : danceI.getFiles()) {

            FileChannel chan = null;
            FileLock lock;

            try {

				chan = FileChannel.open(file.toPath(), ExtendedOpenOption.NOSHARE_READ);
				lock = chan.tryLock();

			} catch (NonWritableChannelException e) {

				if (chan != null) {
					try {
						chan.close();
					} catch (IOException e1) {
						log.error(e1.getMessage(), e1);
					}
					chan = null;
				}

				if (lock != null) {
					try {
						lock.close();
					} catch (IOException e1) {
						log.error(e1.getMessage(), e1);
					}
					lock = null;
				}

			} catch (IOException ex) {
				this.log.debug("Locked - f: " + file.getName());
				blocked.add(file);

				if (chan != null) {
					try {
						chan.close();
					} catch (IOException e1) {
						log.error(e1.getMessage(), e1);
					}
					chan = null;
				}
			}

        }

        return blocked;
    }

    /***
     *
     */
    @Override
    public void provideAsync() {
        boolean force = true;
        SongData data = provideParameterizedData(getLocalSong(force), force);

        if (data != null) {
            log.info(data.getTitle() + ", " + data.getAuthor() + ", " + data.getDance() + ", " + data.getDuration());

            Main.Instance.getSongWindowServer().provideData(data);

        }
    }

}
