
package de.klassenserver7b.danceinterpreter.songprocessing.dataprovider;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
**/

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

		LinkedList<SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

		if (current == null && !songs.isEmpty()) {
			current = 0;
			log.info("Loaded first Song");

			return getDataFromPos(current);
		}

		return null;

	}

	@Override
	public void provideAsync() {

		LinkedList<SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

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

		log.debug("current: " + current);
		log.debug("songs.size: " + songs.size());

		Main.Instance.getSongWindowServer().provideData(getDataFromPos(current));

	}

	protected SongData getDataFromPos(int listIndex) {

		LinkedList<SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

		SongData ret = songs.get(listIndex);
		SongData next;

		if (listIndex < songs.size() - 1 && (next = songs.get(listIndex + 1)) != null) {
			ret.setNext(next);
		}

		return ret;

	}

	public boolean setPosition(int pos) {

		int maxsize = Main.Instance.getDanceInterpreter().getPlaylistSongs().size();

		if (pos < 1 || pos > maxsize) {
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
