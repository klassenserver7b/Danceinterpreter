
package de.klassenserver7b.danceinterpreter.songprocessing.dataprovider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.sun.nio.file.ExtendedOpenOption;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.songprocessing.DanceInterpreter;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
 * 
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

		Mp3File mp3file;
		if (f != null) {
			try {
				mp3file = new Mp3File(f);

				if (mp3file.hasId3v2Tag()) {

					ID3v2 tags = mp3file.getId3v2Tag();

					String title = tags.getTitle();
					String author = tags.getArtist();

					byte[] imageData = tags.getAlbumImage();

					BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);

					// converting the bytes to an image
					if (imageData != null) {
						try {
							img = ImageIO.read(new ByteArrayInputStream(imageData));
						} catch (IOException e) {
							this.log.error("Couldn't parse ID3 Cover");
							log.error(e.getMessage(), e);
						}
					} else {
						this.log.info("Song doesn't have a Cover");
					}

					Long length = mp3file.getLength();
					String dance = danceI.getDance(title, author);

					ret = new SongData(title, author, dance, length, img);

					if (dance == null) {
						danceI.addSongtoJSON(ret, "LOCAL");
					}

				} else {
					ret = new SongData("Unknown", "Unknown", "null", 0L,
							new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB));
				}

			} catch (UnsupportedTagException | InvalidDataException | IOException e1) {
				log.error(e1.getMessage(), e1);
			}
		}

		if (ret == null || (datahash == ret.hashCode() && !provideforced)) {
			return null;
		}
		
		datahash = ret.hashCode();
		return ret;

	}

	/**
	 * 
	 * @param provideforced
	 * @return
	 */
	private File getLocalSong(boolean provideforced) {
		List<File> blocked = getBlockedFiles();

		boolean checked = provideforced;

		if (provideforced) {
			checked = (blocked.hashCode() != hash);
		} else {
			checked = true;
		}

		if (!blocked.isEmpty() && checked) {

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

			if (filename != null) {
				return fileoptions.get(filename);
			}
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	private List<File> getBlockedFiles() {
		List<File> blocked = new ArrayList<>();

		DanceInterpreter danceI = Main.Instance.getDanceInterpreter();
		for (File file : danceI.getFiles()) {

			FileChannel chan = null;
			FileLock lock = null;

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
