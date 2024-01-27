package de.klassenserver7b.danceinterpreter.loader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

public class FileLoader {

	private static final Logger log = LoggerFactory.getLogger(FileLoader.class);

	public static SongData getDataFromFile(File f) {

		if (f == null || f.isDirectory() || !f.canRead()) {
			log.warn("Error accessing file " + (f != null ? f.getAbsolutePath() : "null") +" - exists?: " + (f == null ? "error" : f.exists()) + ", can read?:" + (f == null ? "error" : f.canRead()));
			return null;
		}

		log.debug("Data Loading started for: " + f.getAbsolutePath());

		SongData ret = null;

		Mp3File mp3file;
		try {
			mp3file = new Mp3File(f);

			if (mp3file.hasId3v2Tag()) {

				ID3v2 tags = mp3file.getId3v2Tag();
				String title = tags.getTitle();
				String artist = tags.getArtist();

				byte[] imageData = tags.getAlbumImage();

				BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);

				// converting the bytes to an image
				if (imageData != null) {
					try {
						img = ImageIO.read(new ByteArrayInputStream(imageData));
					} catch (IOException e) {
						log.error("Couldn't parse ID3 Cover");
						log.error(e.getMessage(), e);
					}
				} else {
					log.info("Song doesn't have a Cover");
				}

				Long length = mp3file.getLengthInSeconds();

				String dance = tags.getGenreDescription();

				if (dance == null || dance.isBlank()) {
					if ((dance = tags.getComment()) == null || dance.isBlank()) {
						dance = tags.getItunesComment();
					}
				}

				ret = new SongData(title, artist, dance, length, img);

			} else {

				ret = new SongData("Unknown", "Unknown", "null", 0L,
						new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB));
			}

		} catch (UnsupportedTagException | InvalidDataException | IOException e1) {
			log.error(e1.getMessage(), e1);
		}

		return ret;

	}
}
