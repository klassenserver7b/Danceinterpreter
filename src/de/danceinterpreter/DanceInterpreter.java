package de.danceinterpreter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.hc.core5.http.ParseException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

/**
 * 
 * @author felix
 *
 */
public class DanceInterpreter {

	public JsonObject dancelist;
	public Thread songcheckT;
	public int datahash = 0;
	public SongWindow window;

	/**
	 * 
	 */
	public void startSongCheck() {

		if (initialize()) {
			return;
		}

		songcheckT = new Thread(() -> {

			if (!this.songcheckT.isInterrupted()) {
				long time = System.currentTimeMillis();

				while (!Main.spotify.exit) {
					if (System.currentTimeMillis() >= time + 5000) {
						time = System.currentTimeMillis();

						Songdata data = provideSongData();

						if (data != null) {
							System.out.println(data.getTitle() + ", " + data.getAuthor() + ", " + data.getDance() + ", "
									+ data.getDuration());
							if (this.window == null) {
								this.window = new SongWindow(data.getTitle(), data.getAuthor(), data.getDance(),
										data.getImageURL());
							} else {
								this.window.UpdateWindow(data.getTitle(), data.getAuthor(), data.getDance(),
										data.getImageURL());
							}
						}
					}
				}
			}

		});
		songcheckT.setName("SongCheck");
		songcheckT.start();

	}

	/**
	 * 
	 * @return
	 */

	private Songdata provideSongData() {
		Track cutrack = getCurrentSong();

		if (cutrack != null) {

			StringBuilder authbuild = new StringBuilder();
			String imgurl = cutrack.getAlbum().getImages()[0].getUrl();

			ArtistSimplified[] artists = cutrack.getArtists();

			for (int i = 0; i < artists.length; i++) {
				authbuild.append(artists[i].getName());
				if (i != artists.length - 1) {
					authbuild.append(", ");
				}
			}

			String authors = authbuild.toString().trim();
			String dance = getDance(cutrack.getUri());

			Songdata ret = new Songdata(cutrack.getName(), authors, dance, (long) (cutrack.getDurationMs() / 1000),
					imgurl);

			if (datahash != ret.hashCode()) {
				datahash = ret.hashCode();
				return ret;
			}
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	private Track getCurrentSong() {

		SpotifyApi spotifyapi = Main.getSpotifyAPI();

		GetUsersCurrentlyPlayingTrackRequest currenttrackrequest = spotifyapi.getUsersCurrentlyPlayingTrack().build();
		try {
			CurrentlyPlaying curplay = currenttrackrequest.execute();

			if (curplay != null) {
				IPlaylistItem item = curplay.getItem();

				if (item == null) {
					return null;
				}

				String spotifyid = item.getId();

				GetTrackRequest trackreq = spotifyapi.getTrack(spotifyid).build();

				return trackreq.execute();
			} else {
				return null;
			}

		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 
	 * @param searchquery
	 * @return
	 */
	private String getDance(String spotifyuri) {

		JsonElement dancelem = dancelist.get(spotifyuri);

		if (dancelem != null) {
			return dancelem.getAsString().replaceAll("\"", "");
		} else {
			return null;
		}
	}

	private boolean initialize() {

		File file = new File("resources/dancelist.json");

		if (file.exists()) {

			try {

				String jsonstring = Files.readString(Path.of(file.getPath()));

				JsonElement json = JsonParser.parseString(jsonstring);
				dancelist = json.getAsJsonObject();

				return false;

			} catch (IOException e1) {

				e1.printStackTrace();

				return true;

			}

		}

		return true;

	}
}
