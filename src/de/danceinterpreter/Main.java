/**
 * 
 */
package de.danceinterpreter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.michaelthelin.spotify.SpotifyApi;

/**
 * @author felix
 *
 */
public class Main {
	public static Main main;
	public static SpotifyInteractions spotify;
	public static DanceInterpreter danceinterpreter;
	private Thread shutdownT;
	public static final Logger log = LoggerFactory.getLogger("Main");
	public static boolean errordetected;

	public Main() {

		Properties prop = new Properties();
		FileInputStream in;
		try {

			in = new FileInputStream("resources/config.properties");
			prop.load(in);
			in.close();
			spotify = new SpotifyInteractions(prop);

		} catch (IOException e) {
			log.error("No valid config File found! generating a new one");
			File f = new File("resources/config.properties");
			if (!f.exists()) {
				createConfigFile(f);
			}
		}
		
		danceinterpreter = new DanceInterpreter();

		if (!errordetected) {
			startShutdownT();
			danceinterpreter.startSongCheck();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("java.net.useSystemProxies", "true");
		main = new Main();

	}

	private void createConfigFile(File f) {
		try {
			f.createNewFile();

			BufferedWriter stream = Files.newBufferedWriter(Path.of("resources/config.properties"),
					Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);

			Properties devprops = new Properties();
			devprops.setProperty("client_id", "");
			devprops.setProperty("client_secret", "");
			devprops.setProperty("redirect_uri", "");

			Properties authprops = new Properties();
			authprops.setProperty("authorization_token", "");

			Properties refreshprops = new Properties();
			refreshprops.setProperty("refresh_token", "");

			devprops.store(stream, "Spotify-Api\r\n\nThis data is providet by the AppCreator");
			authprops.store(stream, "\nGet this after authorizing the Application");
			refreshprops.store(stream, "\nDO NOT CHANGE THIS");

			stream.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void startShutdownT() {
		this.shutdownT = new Thread(() -> {
			String line;

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			try {
				while ((line = reader.readLine()) != null) {
					if (line.equalsIgnoreCase("exit")) {
						spotify.exit = true;

						onShutdown();
						reader.close();
						break;
					}
					System.out.println("Use Exit to Shutdown");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		this.shutdownT.setName("Shutdown");
		this.shutdownT.start();
	}

	public void onShutdown() {

		spotify.fetchthread.interrupt();
		danceinterpreter.songcheckT.interrupt();

		this.shutdownT.interrupt();

	}

	/**
	 * 
	 * @return
	 */
	public static SpotifyApi getSpotifyAPI() {

		return Main.spotify.spotifyApi;

	}

	/**
	 * 
	 * @return
	 */
	public static DanceInterpreter getDaceInterpreter() {

		return Main.danceinterpreter;

	}

}
