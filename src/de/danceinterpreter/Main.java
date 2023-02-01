package de.danceinterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.connections.SpotifyInteractions;
import de.danceinterpreter.graphics.ConfigWindow;
import de.danceinterpreter.songprocessing.DanceInterpreter;
import se.michaelthelin.spotify.SpotifyApi;

/**
 * 
 * 
 * package de.danceinterpreter;
 * 
 * import java.io.BufferedReader; import java.io.IOException; import
 * java.io.InputStreamReader; import java.util.ArrayList;
 * 
 * import javax.swing.JOptionPane; import javax.swing.UIManager;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory;
 * 
 * import de.danceinterpreter.connections.SpotifyInteractions; import
 * de.danceinterpreter.graphics.ConfigWindow; import
 * de.danceinterpreter.songprocessing.DanceInterpreter; import
 * se.michaelthelin.spotify.SpotifyApi;
 * 
 * /**
 **/

public class Main {
	public static Main Instance;
	public static boolean exit;
	public static boolean errordetected;

	private SpotifyInteractions spotify;
	private DanceInterpreter danceinterpreter;
	private ConfigWindow cfgwindow;
	private AppModes appMode;
	private Thread shutdownT;
	private final Logger log = LoggerFactory.getLogger("Main");

	public Main() {
		Instance = this;

		initalizeUILayout();

		this.appMode = askForAppMode();

		if (this.appMode == null) {
			onShutdown(null);
			return;
		}

		cfgwindow = new ConfigWindow();

		if (!load()) {
			onShutdown(appMode);
			return;
		}

		startShutdownT(this.appMode);

	}

	/**
	 * @param args
	 * 
	 * 
	 * 
	 */
	public static void main(String[] args) {
		System.setProperty("java.net.useSystemProxies", "true");
		Instance = new Main();

	}

	/**
	 *
	 
	
	*/
	private boolean load() {
		this.danceinterpreter = new DanceInterpreter();

		if (appMode == AppModes.Spotify) {
			this.spotify = new SpotifyInteractions();
		}

		if (!errordetected) {
			return danceinterpreter.startSongCheck(appMode);
		}

		return false;
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	public AppModes askForAppMode() {

		ArrayList<String> optionsToChoose = new ArrayList<>();

		for (AppModes m : AppModes.values()) {
			optionsToChoose.add(m.toString());
		}

		String localappMode = (String) JOptionPane.showInputDialog(null, "Which AppMode do you want to use?",
				"Choose Mode", JOptionPane.QUESTION_MESSAGE, null, optionsToChoose.toArray(), optionsToChoose.get(0));

		if (localappMode == null) {
			return null;
		}

		AppModes appMode = AppModes.valueOf(localappMode);

		return appMode;

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
	private void startShutdownT(AppModes appMode) {
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
	public void onShutdown(AppModes appMode) {

		this.log.info("Shutdown started");

		if (danceinterpreter != null) {
			danceinterpreter.shutdown();
		}

		if (cfgwindow != null) {
			cfgwindow.close();
		}

		this.log.debug("Danceinterpreter deactivated");

		if (appMode == AppModes.Spotify) {
			spotify.fetchthread.interrupt();
			this.log.debug("Spotify deactivated");
		}

		this.log.info("Shutdown complete");

		if (this.shutdownT != null) {
			this.shutdownT.interrupt();
		}

	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	public AppModes getAppMode() {
		return this.appMode;
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	public SpotifyApi getSpotifyAPI() {

		return this.spotify.getSpotifyApi();

	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	public DanceInterpreter getDanceInterpreter() {

		return this.danceinterpreter;

	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	public ConfigWindow getConfigWindow() {
		return this.cfgwindow;
	}

}
