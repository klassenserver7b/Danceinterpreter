
package de.klassenserver7b.danceinterpreter.songprocessing.dataprovider;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

/**
**/

public class SpotifySongDataProvider implements SongDataProvider {

	private final Logger log;
	public int datahash;

	/**
	 *
	 
	
	*/
	public SpotifySongDataProvider() {
		this.log = LoggerFactory.getLogger(this.getClass());
		this.datahash = 0;
	}

	@Override
	public SongData provideSongData() {
		Track cutrack = getCurrentSpotifySong();

		if (cutrack != null) {

			SongData ret = getDatafromTrack(cutrack);
			if (ret != null && this.datahash != ret.hashCode()) {
				this.datahash = ret.hashCode();
				return ret;
			}

		}

		return null;
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	private Track getCurrentSpotifySong() {

		SpotifyApi spotifyapi = Main.Instance.getSpotifyAPI();

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
			}
			return null;

		} catch (ParseException | SpotifyWebApiException | IOException e1) {
			this.log.error(e1.getMessage(), e1);
			return null;
		}

	}

	private SongData getDatafromTrack(Track cutrack) {

		StringBuilder authbuild = new StringBuilder();
		Image[] images = cutrack.getAlbum().getImages();

		Image img = images[0];

		for (Image imgs : images) {
			if (imgs.getHeight() > img.getHeight()) {
				img = imgs;
			}
		}

		String imgurl = img.getUrl();

		ArtistSimplified[] artists = cutrack.getArtists();

		for (int i = 0; i < artists.length; i++) {
			authbuild.append(artists[i].getName());
			if (i != artists.length - 1) {
				authbuild.append(", ");
			}
		}

		String artistsStr = authbuild.toString().trim();
		String dance = Main.Instance.getDanceInterpreter().getDance(cutrack.getUri());

		if (dance == null) {

			try {
				Main.Instance.getDanceInterpreter().addSongtoJSON(new SongData(cutrack.getName(), artistsStr, dance,
						(long) (cutrack.getDurationMs() / 1000), imgurl), cutrack.getUri());
			} catch (IOException | URISyntaxException e1) {
				this.log.error(e1.getMessage(), e1);
			}

		}

		try {
			return new SongData(cutrack.getName(), artistsStr, dance, (long) (cutrack.getDurationMs() / 1000), imgurl);
		} catch (IOException | URISyntaxException e) {
			this.log.error(e.getMessage(), e);
			return null;
		}

	}

	@Override
	public void provideAsync() {
		Track t = getCurrentSpotifySong();

		if (t == null) {
			return;
		}

		SongData data = getDatafromTrack(t);

		if (data == null) {
			return;
		}

		this.log.info(data.getTitle() + ", " + data.getArtist() + ", " + data.getDance() + ", " + data.getDuration());

		Main.Instance.getSongWindowServer().provideData(data);
	}

}
