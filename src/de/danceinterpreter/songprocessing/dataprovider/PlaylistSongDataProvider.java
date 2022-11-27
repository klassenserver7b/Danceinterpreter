/**
 * 
 */
package de.danceinterpreter.songprocessing.dataprovider;

import java.io.File;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.Main;
import de.danceinterpreter.songprocessing.DanceInterpreter;
import de.danceinterpreter.songprocessing.SongData;

/**
 * @author Felix
 *
 */
public class PlaylistSongDataProvider implements SongDataProvider {

	private final Logger log;
	private Integer current;
	private int forward = 1;

	/**
	 * 
	 */
	public PlaylistSongDataProvider() {
		this.log = LoggerFactory.getLogger(this.getClass());
		this.current = null;
	}

	@Override
	public SongData provideSongData() {

		LinkedHashMap<File, SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

		if (current == null && !songs.isEmpty()) {
			current = 0;
			log.info("Loaded first Song");
			return songs.entrySet().parallelStream().toList().get(current).getValue();
		}

		return null;

	}

	@Override
	public void provideAsynchronous() {

		LinkedHashMap<File, SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

		switch (forward) {

		case 1 -> {

			if (current < songs.size() - 1) {
				current++;
			}
			log.debug("forward");
		}

		case -1 -> {

			if (current > 0) {
				current--;
			}
			log.debug("back");
		}
		case 0 -> {
			log.debug("same");
		}

		}

		if (current < songs.size() && current >= 0) {

			SongData data = songs.entrySet().parallelStream().toList().get(current).getValue();

			DanceInterpreter di = Main.Instance.getDanceInterpreter();

			di.updateSongWindow(data.getTitle(), data.getAuthor(), data.getDance(), data.getImage());
		}

	}

	public boolean setPosition(int pos) {

		int maxsize = Main.Instance.getDanceInterpreter().getPlaylistSongs().size();

		if (pos < 1 || pos >= maxsize) {
			return false;
		}

		current = pos - 1;
		forward = 0;

		return true;

	}

	public void setDirection(int forward) {
		this.forward = forward;
	}

}
