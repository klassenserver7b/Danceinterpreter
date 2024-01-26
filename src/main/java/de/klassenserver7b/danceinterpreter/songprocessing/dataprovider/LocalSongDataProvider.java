
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
		this.log = LoggerFactory.getLogger(this.getClass());
		this.hash = 0;
		this.datahash = 0;
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

		SongData ret;
		DanceInterpreter danceI = Main.Instance.getDanceInterpreter();

		ret = FileLoader.getDataFromFile(f);

		if (ret == null || (this.datahash == ret.hashCode() && !provideforced)) {
			return null;
		}

		if (ret.getDance() == null) {
			danceI.addSongtoJSON(ret, "LOCAL");
		}

		this.datahash = ret.hashCode();
		return ret;

	}

	/**
	 * Gets currently playing song by checking for locked files Asks user to select
	 * if multiple files are locked <br>
	 * <strong>HUAN</strong> <b>WARNING: </b> Works only on Windows based OS
	 *
	 * @param provideforced Whether to not forcefully refresh the song if the new
	 *                      locked files list's hash matches the old one
	 * @return File of the currently playing song or null if no song is playing /
	 *         the hashes match
	 */
	private File getLocalSong(boolean provideforced) {
		List<File> blocked = getBlockedFiles();

		if (blocked.isEmpty()) {
			return null;
		}
		if (!provideforced && blocked.hashCode() == this.hash) {
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
	 * Iterates over DanceInterpreter.getFiles(), checks whether a file is currently
	 * locked / used by another process, then returns a list of all locked files
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

				/*
				 * Windows doesn't support locks on NOSHARE_READ
				 */
				System.err.println(file.getAbsolutePath());
				chan = FileChannel.open(file.toPath(), ExtendedOpenOption.NOSHARE_READ);
				lock = chan.tryLock();
				lock.close();
			}
			/*
			 * If file is not locked by another application chan.tryLock() will throw a
			 * NonWritableChannelException because locking is not supported on NOSHARE_READ
			 */
			catch (NonWritableChannelException e) {
				//
			}

			/*
			 * If the file is locked FileChannel.open() throws an IOException because it
			 * can't open the file unshared
			 */
			catch (IOException ex) {
				this.log.debug("Locked - f: " + file.getName());
				blocked.add(file);
			} finally {
				if (chan != null) {
					try {
						chan.close();
					} catch (IOException e1) {
						this.log.error(e1.getMessage(), e1);
					}
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
			this.log.info(
					data.getTitle() + ", " + data.getArtist() + ", " + data.getDance() + ", " + data.getDuration());

			Main.Instance.getSongWindowServer().provideData(data);

		}
	}

}
