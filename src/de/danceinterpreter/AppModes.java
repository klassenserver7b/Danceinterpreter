package de.danceinterpreter;

import de.danceinterpreter.songprocessing.dataprovider.*;

/**
 * 
 * @author Felix
 *
 */
public enum AppModes {

	Spotify(0, new SpotifySongDataProvider()),

	LocalMP3(1, new LocalSongDataProvider()),

	Playlist(2, new PlaylistSongDataProvider());

	private final int id;
	private final SongDataProvider provider;

	private AppModes(int id, SongDataProvider provider) {
		this.id = id;
		this.provider = provider;
	}

	/**
	 * Used to retrieve the correspondending {@link SongDataProvider} for the
	 * AppMode. Can be used to retrieve the currently playing song
	 * 
	 * @return The static {@link SongDataProvider} of the {@link AppModes}
	 */
	public SongDataProvider getDataProvider() {
		return this.provider;
	}

	/**
	 * Used to retrieve the id of the AppMode. Can be used to spimplify the Mode and
	 * to retrieve the corresponding {@link AppModes} by using {@link #fromId(int)}
	 * 
	 * @return The static id of the {@link AppModes}
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Static accessor for retrieving a AppMode based on its id.
	 *
	 * @param id The id key of the requested AppMode.
	 *
	 * @return The {@link AppModes} that is referred to by the provided id. If the
	 *         id is unknown, {@link null} is returned.
	 */
	public static AppModes fromId(int id) {
		for (AppModes type : values()) {
			if (type.id == id)
				return type;
		}
		return null;
	}

}
