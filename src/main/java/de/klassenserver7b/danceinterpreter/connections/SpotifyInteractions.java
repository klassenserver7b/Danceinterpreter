/**
 * 
 */

package de.klassenserver7b.danceinterpreter.connections;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Date;
import java.util.prefs.Preferences;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.Main;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

/**
**/

public class SpotifyInteractions {

	private static final String CLISEC = "982cae885d54429bb830a8a48f3a03c9";
	private static final String CLIID = "63bd45efbbac4e7a936ee5b9d28d78e3";
	// private static final String REDURI = "https://github.com/klassenserver7b";
	private static final String REDURI = "http://localhost:8187/submitcode";
	private Logger spotifylog;
	Preferences prefs;
	private final String rtkpath;
	private String rtk;
	private SpotifyApi spotifyApi;
	public Thread fetchthread;
	public long expires;

	public SpotifyInteractions() {

		spotifylog = LoggerFactory.getLogger("spotifylog");

		rtkpath = new File("").getParent() + "_" + new File("").getName() + "_" + "DIRTK";
		prefs = Preferences.userRoot()
				.node(new File("").getParent() + "_" + new File("").getName() + "_" + this.getClass().getName());

		if (!initialize()) {
			Main.errordetected = true;
			return;
		}

		refreshToken();

		startfetchcycle();

	}

	/**
	 * 
	 * @return
	 */
	public boolean initialize() {

		Integer proxyport = null;
		String proxyurl = null;

		try {
			HttpRoute route = new SystemDefaultRoutePlanner(ProxySelector.getDefault())
					.determineRoute(new HttpHost("https://api.spotify.com"), new BasicHttpContext());

			HttpHost proxy = route.getProxyHost();

			if (proxy != null) {
				proxyurl = proxy.getHostName();
				proxyport = proxy.getPort();
			}

		} catch (HttpException e) {
			spotifylog.error(e.getMessage(), e);
		}

		this.spotifyApi = new SpotifyApi.Builder().setClientId(CLIID).setProxyUrl(proxyurl).setProxyPort(proxyport)
				.setClientSecret(CLISEC).setRedirectUri(URI.create(REDURI)).build();

		rtk = prefs.get(rtkpath, "");

		if (rtk == null || rtk.isBlank()) {

			sendTokenRequest(this);

			return true;
		}

		if (rtk != null && !rtk.isBlank()) {
			spotifyApi.setRefreshToken(rtk);
			return true;
		}

		return false;
	}

	public void sendTokenRequest(SpotifyInteractions interaction) {

		spotifylog.error("no usr_auth_code");

		final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri().scope(
				"app-remote-control,streaming,user-read-playback-position,user-modify-playback-state,user-read-playback-state,user-read-currently-playing")
				.build();

		URI requestUri = authorizationCodeUriRequest.execute();

		try {

			new CodeHttpServer(interaction);

			Desktop.getDesktop().browse(requestUri);

		} catch (IOException e) {
			spotifylog.error(e.getMessage(), e);
		}

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
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (Main.exit) {
				return;
			}

		});
		this.fetchthread.setName("token_fetch_cycle");
		this.fetchthread.start();

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
			spotifylog.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param code
	 */
	public void authorize(String code) {

		spotifylog.debug("authorization started");
		final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

		try {

			AuthorizationCodeCredentials creds = authorizationCodeRequest.execute();

			spotifyApi.setAccessToken(creds.getAccessToken());
			spotifyApi.setRefreshToken(creds.getRefreshToken());
			this.expires = new Date().getTime() + (creds.getExpiresIn() * 1000);

			prefs.put(rtkpath, creds.getRefreshToken());

			spotifylog.debug("AUTHORIZED -> expires:" + this.expires + ", token:" + spotifyApi.getAccessToken());

			refreshToken();
			return;

		} catch (ParseException | SpotifyWebApiException | IOException e) {
			spotifylog.error(e.getMessage(), e);
			spotifylog.error("invalid usr_auth_code");
			final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri().scope(
					"app-remote-control,streaming,user-read-playback-position,user-modify-playback-state,user-read-playback-state,user-read-currently-playing")
					.build();

			spotifylog.info(
					"Please insert your VALID authcode into the configfile\nThis can be optained in the redirect url after accepting:\n"
							+ authorizationCodeUriRequest.execute());
			return;
		}

	}

	public SpotifyApi getSpotifyApi() {
		return spotifyApi;
	}

}