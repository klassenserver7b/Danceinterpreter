/**
 * 
 */
package de.danceinterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.Connections.SpotifyInteractions;
import de.danceinterpreter.Songprocessing.DanceInterpreter;
import se.michaelthelin.spotify.SpotifyApi;

/**
 * @author felix
 *
 */
public class Main {
	public static Main Instance;
	public static boolean exit;
	public SpotifyInteractions spotify;
	public DanceInterpreter danceinterpreter;
	public String appMode;
	private Thread shutdownT;
	private final Logger log = LoggerFactory.getLogger("Main");
	public static boolean errordetected;

	public Main() {
		Instance = this;


		initalizeUILayout();
		this.appMode = getAppMode();

		if (this.appMode == null) {
			return;
		}

		switch (this.appMode) {
		case "local .mp3 files": {
			if (!loadLocal()) {
				return;
			}
			break;
		}
		case "Spotify": {
			if (!loadSpotify()) {
				return;
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + this.appMode);
		}

		startShutdownT(this.appMode);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("java.net.useSystemProxies", "true");
		Instance = new Main();

	}

	/**
	 *
	 */
	private boolean loadLocal() {
		danceinterpreter = new DanceInterpreter();

		if (!errordetected) {
			return danceinterpreter.startLocalSongCheck();
		}
		return false;
	}

	/**
	 * 
	 * @param prop
	 */
	private boolean loadSpotify() {

		spotify = new SpotifyInteractions();
		danceinterpreter = new DanceInterpreter();

		if (!errordetected) {
			return danceinterpreter.startSpotifySongCheck();
		}
		return false;

	}

	/**
	 * 
	 * @return
	 */
	public String getAppMode() {

		String[] optionsToChoose = { "Spotify", "local .mp3 files" };

		String localappMode = (String) JOptionPane.showInputDialog(null, "Which AppMode do you want to use?",
				"Choose Mode", JOptionPane.QUESTION_MESSAGE, null, optionsToChoose, optionsToChoose[1]);

		return localappMode;

	}

	/**
	 * 
	 */
	private void initalizeUILayout() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	private void startShutdownT(String appMode) {
		this.shutdownT = new Thread(() -> {
			String line;

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			try {
				while ((line = reader.readLine()) != null) {
					if (line.equalsIgnoreCase("exit")) {
						Main.exit = true;

						onShutdown(appMode);
						reader.close();
						break;
					}
					System.out.println("Use Exit to Shutdown");
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		});
		this.shutdownT.setName("Shutdown");
		this.shutdownT.start();
	}

	/**
	 * 
	 */
	public void onShutdown(String appMode) {

		this.log.info("Shutdown started");

		danceinterpreter.shutdown();
		this.log.debug("Danceinterpreter deactivated");

		if (appMode.equalsIgnoreCase("Spotify")) {
			spotify.fetchthread.interrupt();
			this.log.debug("Spotify deactivated");
		}
		this.log.info("Shutdown complete");
		this.shutdownT.interrupt();

	}

	/**
	 * 
	 * @return
	 */
	public SpotifyApi getSpotifyAPI() {

		return this.spotify.spotifyApi;

	}

	/**
	 * 
	 * @return
	 */
	public DanceInterpreter getDanceInterpreter() {

		return this.danceinterpreter;

	}

}
