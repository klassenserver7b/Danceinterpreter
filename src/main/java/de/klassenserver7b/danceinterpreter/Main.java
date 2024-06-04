package de.klassenserver7b.danceinterpreter;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.formdev.flatlaf.FlatDarculaLaf;

import de.klassenserver7b.danceinterpreter.connections.SpotifyInteractions;
import de.klassenserver7b.danceinterpreter.graphics.ConfigWindow;
import de.klassenserver7b.danceinterpreter.graphics.SongWindowServer;
import de.klassenserver7b.danceinterpreter.songprocessing.DanceInterpreter;
import se.michaelthelin.spotify.SpotifyApi;

/**
 * 
 * @author K7
 *
 */

public class Main {
	public static Main Instance;
	public static boolean exit;
	public static boolean errordetected;

	private SpotifyInteractions spotify;
	private DanceInterpreter danceInterpreter;
	private ConfigWindow cfgWindow;
	private SongWindowServer songWindowServer;
	private AppModes appMode;
	private Thread shutdownT;
	private final Logger log;
	private TrayIcon trayIcon;
	// private final Color backgroundColor = Color.decode("#1E1F22");
	// private final Color textColor = Color.decode("#EAE5D6");

	/**
	 * 
	 */
	public Main() {
		Instance = this;

		log = LoggerFactory.getLogger("Main");
		this.trayIcon = null;

		if (!initalizeUILayout()) {
			this.log.warn("LayoutInitialization failed");
		}

		this.appMode = askForAppMode();

		if (this.appMode == null) {
			onShutdown(null);
			return;
		}

		this.cfgWindow = new ConfigWindow();
		initSystemTray();

		if (!load()) {
			onShutdown(this.appMode);
			return;
		}

		startShutdownT(this.appMode);

	}

	/**
	 * @param test test 
	 */
	protected Main(boolean test) {
		log = LoggerFactory.getLogger(getClass());
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
	 * @return
	 */
	private boolean load() {
		this.danceInterpreter = new DanceInterpreter();
		this.songWindowServer = SongWindowServer.createDefault();

		if (this.appMode == AppModes.Spotify) {
			this.spotify = new SpotifyInteractions();
		}

		if (!errordetected) {
			return this.danceInterpreter.startSongCheck(this.appMode);
		}

		return false;
	}

	/**
	 * @return
	 */
	protected AppModes askForAppMode() {

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
	 * @return
	 */
	protected boolean initalizeUILayout() {
		return FlatDarculaLaf.setup();
	}

	protected void initSystemTray() {
		SystemTray tray = SystemTray.getSystemTray();

		Image image;
		try {

			image = Toolkit.getDefaultToolkit().createImage(getClass().getResourceAsStream("/icon.jpg").readAllBytes());

			TrayIcon trayIcon = new TrayIcon(image, "Danceinterpreter");

			trayIcon.setImageAutoSize(true);
			trayIcon.setToolTip("Danceinterpreter icon");

			tray.add(trayIcon);
			this.trayIcon = trayIcon;

		} catch (AWTException | IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param appMode
	 */
	protected void startShutdownT(AppModes appMode) {
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
				this.log.error(e.getMessage(), e);
			}
		});
		this.shutdownT.setName("Shutdown");
		this.shutdownT.start();
	}

	/**
	 * 
	 * @param appMode
	 */
	public void onShutdown(AppModes appMode) {

		this.log.info("Shutdown started");

		if (this.cfgWindow != null) {
			this.cfgWindow.close();
		}

		this.log.debug("Danceinterpreter deactivated");
		if (appMode == AppModes.Spotify) {
			this.spotify.fetchthread.interrupt();
			this.log.debug("Spotify deactivated");
		}

		this.log.info("Shutdown complete");

		if (this.shutdownT != null) {
			this.shutdownT.interrupt();
		}

	}

	public boolean isWinOS() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains("win");
	}

	public String getHomeDir() {
		if (isWinOS()) {
			return System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH");
		}
		return System.getenv("HOME");
	}

	/**
	 * 
	 * @return
	 */
	public AppModes getAppMode() {
		return this.appMode;
	}

	/**
	 * 
	 * @return
	 */
	public SpotifyApi getSpotifyAPI() {

		return this.spotify.getSpotifyApi();

	}

	/**
	 * 
	 * @return
	 */
	public DanceInterpreter getDanceInterpreter() {

		return this.danceInterpreter;

	}

	/**
	 * 
	 * @return
	 */
	public ConfigWindow getConfigWindow() {
		return this.cfgWindow;
	}

	/**
	 * 
	 * @return
	 */
	public SongWindowServer getSongWindowServer() {
		return this.songWindowServer;
	}

	/**
	 * @return the trayIcon
	 */
	public TrayIcon getTrayIcon() {
		return this.trayIcon;
	}

}
