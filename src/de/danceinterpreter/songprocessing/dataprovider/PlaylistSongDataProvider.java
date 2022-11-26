/**
 * 
 */
package de.danceinterpreter.songprocessing.dataprovider;

import java.io.File;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.Main;
import de.danceinterpreter.songprocessing.SongDataProvider;
import de.danceinterpreter.songprocessing.DanceInterpreter;
import de.danceinterpreter.songprocessing.SongData;

/**
 * @author Felix
 *
 */
public class PlaylistSongDataProvider implements SongDataProvider {

	private final Logger log;
	private Integer current;
	private boolean forward = true;

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

		if (current == null) {
			current = 0;
			log.info("Loaded first Song");
			return songs.entrySet().parallelStream().toList().get(current).getValue();
		}

		return null;

	}

	@Override
	public void provideAsynchronous() {

		LinkedHashMap<File, SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

		if (forward) {

			if (current < songs.size() - 1) {

				log.debug("forward");
				current++;
				SongData data = songs.entrySet().parallelStream().toList().get(current).getValue();

				DanceInterpreter di = Main.Instance.getDanceInterpreter();

				di.updateSongWindow(data.getTitle(), data.getAuthor(), data.getDance(), data.getImage());
			}

		} else {

			if (current > 0) {

				log.debug("back");
				current--;
				SongData data = songs.entrySet().parallelStream().toList().get(current).getValue();

				DanceInterpreter di = Main.Instance.getDanceInterpreter();

				di.updateSongWindow(data.getTitle(), data.getAuthor(), data.getDance(), data.getImage());

			}

		}

	}

	public void setDirection(boolean forward) {
		this.forward = forward;
	}

}
