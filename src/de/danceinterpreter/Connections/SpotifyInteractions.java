/**
 * 
 */
package de.danceinterpreter.Connections;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Properties;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.Main;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

/**
 * @author felix
 *
 */
public class SpotifyInteractions {
	private Logger spotifylog = LoggerFactory.getLogger("spotifylog");
	private static String code = "";
	public SpotifyApi spotifyApi;
	public Thread fetchthread;
	public long expires;
	private boolean usingrefreshtoken;

	public SpotifyInteractions(Properties prop) {

		if (initialize(prop)) {
			Main.errordetected=true;
			return;
		}
		if (!usingrefreshtoken) {
			if (authorize(prop)) {
				Main.errordetected=true;
				return;
			}
		} else {
			refreshToken();
		}
		startfetchcycle();

	}

	/**
	 * 
	 * @param prop
	 * @return
	 */
	public boolean initialize(Properties prop) {

		String clisec = String.valueOf(prop.get("client_secret"));
		String cliid = String.valueOf(prop.get("client_id"));
		String reduri = String.valueOf(prop.get("redirect_uri"));
		String usr_auth_code = String.valueOf(prop.get("authorization_token"));
		String ref_token = String.valueOf(prop.get("refresh_token"));

		if (clisec.isBlank() || cliid.isBlank() || reduri.isBlank()) {
			spotifylog.error("blank required fields");
			spotifylog.info("Invalid configfile - ask the developer for the required data");
			return true;

		}

		this.spotifyApi = new SpotifyApi.Builder().setClientId(cliid).setClientSecret(clisec)
				.setRedirectUri(URI.create(reduri)).build();

		if (!ref_token.isEmpty()) {
			spotifyApi.setRefreshToken(ref_token);
			usingrefreshtoken = true;
			return false;
		}

		if (usr_auth_code==null ||usr_auth_code.isBlank()) {

			spotifylog.error("no usr_auth_code");
			final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
					.scope("app-remote-control,streaming,user-read-playback-position,user-modify-playback-state,user-read-playback-state,user-read-currently-playing").build();

			spotifylog.info(
					"Please insert your authcode into the configfile\nThis can be optained in the redirect url after accepting:\n"
							+ authorizationCodeUriRequest.execute());

			return true;
		}

		code = usr_auth_code;

		return false;
	}

	/**
	 * 
	 */
	public void startfetchcycle() {

		this.fetchthread = new Thread(() -> {

			while (Main.exit) {
				if (!(this.expires >= new Date().getTime() - 5000)) {
					refreshToken();
					spotifylog.debug("authcode_refresh");
				}
			}
			if(Main.exit) {
				return;
			}

		});
		this.fetchthread.setName("token_fetch_cycle");
		this.fetchthread.start();

	}

	public void updateConfig(Properties prop) throws IOException {
		
		BufferedWriter stream = Files.newBufferedWriter(Path.of("resources/config.properties"), Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);
		
		Properties devprops = new Properties();
		devprops.setProperty("client_id", prop.getProperty("client_id"));
		devprops.setProperty("client_secret", prop.getProperty("client_secret"));
		devprops.setProperty("redirect_uri", prop.getProperty("redirect_uri"));
		
		Properties authprops = new Properties();
		authprops.setProperty("authorization_token", "");
		
		Properties refreshprops = new Properties();
		refreshprops.setProperty("refresh_token", prop.getProperty("refresh_token"));
		
		devprops.store(stream, "Spotify-Api\r\n\nThis data is providet by the AppCreator");
		authprops.store(stream, "\nGet this after authorizing the Application");
		refreshprops.store(stream, "\nDO NOT CHANGE THIS");
		
		
		stream.close();

	}

	/**
	 * 
	 */
	public void refreshToken() {

		final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
				.build();

		try {

			final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

			spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
			this.expires = new Date().getTime() + (authorizationCodeCredentials.getExpiresIn() * 1000);
			spotifylog.debug("new DATA -> expires:" + this.expires + ", token:" + spotifyApi.getAccessToken());

		} catch (IOException | SpotifyWebApiException | ParseException e) {
			spotifylog.error("Error: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean authorize(Properties prop) {

		spotifylog.debug("authorization started");
		final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

		try {

			AuthorizationCodeCredentials creds = authorizationCodeRequest.execute();

			spotifyApi.setAccessToken(creds.getAccessToken());
			spotifyApi.setRefreshToken(creds.getRefreshToken());
			this.expires = new Date().getTime() + (creds.getExpiresIn() * 1000);

			prop.setProperty("refresh_token", creds.getRefreshToken());
			updateConfig(prop);

			spotifylog.debug("AUTHORIZED -> expires:" + this.expires + ", token:" + spotifyApi.getAccessToken());

		} catch (ParseException | SpotifyWebApiException | IOException e) {
			spotifylog.error(e.getMessage(), e);
			spotifylog.error("invalid usr_auth_code");
			final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
					.scope("app-remote-control,streaming,user-read-playback-position,user-modify-playback-state,user-read-playback-state,user-read-currently-playing").build();

			spotifylog.info(
					"Please insert your VALID authcode into the configfile\nThis can be optained in the redirect url after accepting:\n"
							+ authorizationCodeUriRequest.execute());
			return true;
		}

		return false;

	}

}