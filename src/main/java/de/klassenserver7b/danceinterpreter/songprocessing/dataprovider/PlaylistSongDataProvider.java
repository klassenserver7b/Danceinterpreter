
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
	private Direction direction = Direction.FORWARD;

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

		if (this.current == null && !songs.isEmpty()) {
			this.current = 0;
			this.log.info("Loaded first Song");

			return getDataFromPos(this.current);
		}

		return null;

	}

	@Override
	public void provideAsync() {

		LinkedList<SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

		switch (this.direction) {

		case FORWARD -> {

			if (this.current < songs.size() - 1) {
				this.current++;
			}
			this.log.debug("direction");
		}

		case BACK -> {

			if (this.current > 0) {
				this.current--;
			}
			this.log.debug("back");
		}

		case SAME -> {
			this.log.debug("same");
		}

		default -> throw new IllegalArgumentException("Unexpected value: " + this.direction);

		}

		this.log.debug("current: " + this.current);
		this.log.debug("songs.size: " + songs.size());

		Main.Instance.getSongWindowServer().provideData(getDataFromPos(this.current));

	}

	protected SongData getDataFromPos(int listIndex) {

		LinkedList<SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

		SongData ret = songs.get(listIndex);
		SongData next;

		if (listIndex < songs.size() - 1 && (next = songs.get(listIndex + 1)) != null) {
			if (!(next.getTitle().isBlank() && next.getArtist().isBlank())) {
				ret.setNext(next);
			}
		}

		return ret;

	}

	public boolean setPosition(int pos) {

		int maxsize = Main.Instance.getDanceInterpreter().getPlaylistSongs().size();

		if (pos < 1 || pos > maxsize) {
			return false;
		}

		this.current = pos - 1;
		this.direction = Direction.SAME;

		return true;

	}

	/**
	 * 
	 * @param direction 0 ->
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public enum Direction {
		FORWARD(1), BACK(-1), SAME(0), UNKNOWN(-1);

		private final int id;

		private Direction(int id) {
			this.id = id;
		}

		public Direction byId(int id) {
			for (Direction t : values()) {
				if (t.getId() == id) {
					return t;
				}
			}
			return UNKNOWN;
		}

		public int getId() {
			return id;
		}
	}

}
