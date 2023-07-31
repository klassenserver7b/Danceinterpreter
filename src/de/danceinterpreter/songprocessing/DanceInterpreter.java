package de.danceinterpreter.songprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.danceinterpreter.AppModes;
import de.danceinterpreter.Main;
import de.danceinterpreter.loader.PlaylistLoader;
import de.danceinterpreter.threads.SongCheckThread;

/**


 *
 
 
 */
public class DanceInterpreter {

	private TreeMap<String, JsonObject> dancelist = new TreeMap<>();
	private SongCheckThread songcheckT;

	private LinkedHashMap<File, SongData> songs;

	private final List<File> data;

	private final Logger log;

	public DanceInterpreter() {
		this.data = new ArrayList<>();
		this.log = LoggerFactory.getLogger("Danceinterpreter");
	}

	/**
	 * 
	 
	
	
	*/
	public boolean startSongCheck(AppModes appmode) {

		if (!initialize()) {
			return false;
		}

		if (appmode == AppModes.LocalMP3) {
			if (!loadWorkingDirectory()) {
				return false;
			}
		}

		if (appmode == AppModes.Playlist) {

			File playlist;
			if ((playlist = new PlaylistLoader().loadPlaylistFile()) == null) {
				log.debug("Invalid Playlist File - couldn't load playlist-file");
				return false;
			}
			if ((songs = new PlaylistLoader().loadSongs(playlist)) == null) {
				log.debug("Invalid Playlist File - couldn't load songs!");
				return false;
			}
			log.debug("Playlist sucessfully loaded!");
		}

		songcheckT = new SongCheckThread(appmode);
		return true;
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	private boolean loadWorkingDirectory() {

		String workingDirectorystr = getWorkingDirectory();

		if (workingDirectorystr != null) {
			Path workingDirectory = Path.of(workingDirectorystr, "");
			this.log.info("Working in: " + workingDirectory);
			File f = workingDirectory.toFile();
			File[] files = f.listFiles();

			if (files != null) {
				findAllFilesInFolder(f);
			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param f
	 * 
	 * 
	 * 
	 */
	private void findAllFilesInFolder(File f) {

		if (!f.isDirectory()) {
			return;
		}
		for (File file : f.listFiles()) {
			if (!file.isDirectory()) {
				if (file.getName().endsWith(".mp3")) {
					data.add(file);
				}
			} else {
				findAllFilesInFolder(file);
			}
		}

	}

	/**
	 * 
	 
	
	
	*/
	public void shutdown() {

		// if (this.songcheckT != null) {
		// this.songcheckT.interrupt();
		//
		// }
		//
		// if (this.window != null) {
		// JFrame mainframe = this.getWindow().getMainFrame();
		// if (mainframe != null) {
		// mainframe.dispose();
		// mainframe.setVisible(false);
		// mainframe = null;
		// }
		// JPanel mainpanel = this.getWindow().getMainPanel();
		// if (mainpanel != null) {
		// mainpanel.removeAll();
		// mainpanel.setEnabled(false);
		// mainpanel = null;
		// }
		// }
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	private String getWorkingDirectory() {

		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

		String dir = prefs.get("Last-Path", "");

		if (dir == null || dir.equalsIgnoreCase("") || (new File(dir)) == null) {

			dir = System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH");

		}

		JFileChooser f = new JFileChooser();
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		f.setCurrentDirectory(new File(dir));
		f.setDialogTitle("Select Directory");
		f.setApproveButtonText("Select");
		f.setMultiSelectionEnabled(false);
		f.showOpenDialog(null);

		if (f.getSelectedFile() != null) {
			prefs.put("Last-Path", f.getSelectedFile().getAbsolutePath());
			return f.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

	/**
	 * 
	 * @param spotifyuri
	 * @return
	 */
	public String getDance(String spotifyuri) {

		JsonObject danceobj = dancelist.get(spotifyuri);

		if (danceobj != null) {

			String dance;
			JsonElement elem = danceobj.get("dance");
			if (elem != null && !elem.isJsonNull() && !(dance = elem.getAsString()).equalsIgnoreCase("")) {
				return dance;
			} else {
				return "unknown";
			}
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param title
	 * @param author
	 * @return
	 * 
	 * 
	 * 
	 */
	public String getDance(String title, String author) {

		JsonObject danceobj = dancelist.get(author + " - " + title);

		if (danceobj != null) {

			String dance;
			JsonElement elem = danceobj.get("dance");
			if (elem != null && !elem.isJsonNull() && !(dance = elem.getAsString()).isBlank()) {
				return dance;
			} else {
				return "unknown";
			}
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	private boolean initialize() {

		File file = new File("./resources/dancelist.json");

		if (file.exists()) {

			try {

				String jsonstring = Files.readString(Path.of(file.getPath()));

				JsonElement json = JsonParser.parseString(jsonstring);

				if (json != null) {
					JsonArray arr = json.getAsJsonObject().get("Songs").getAsJsonArray();

					switch (Main.Instance.getAppMode()) {
					case Spotify -> {

						for (JsonElement e : arr) {

							JsonObject obj = e.getAsJsonObject();
							dancelist.put(obj.get("SpotifyURL").getAsString(), obj);

						}
						break;

					}
					default -> {

						for (JsonElement e : arr) {

							JsonObject obj = e.getAsJsonObject();
							String title = obj.get("title").getAsString();
							String artist = obj.get("artist").getAsString();

							dancelist.put(artist + " - " + title, e.getAsJsonObject());

						}
					}
					}
				}

				return true;

			} catch (IOException e1) {
				log.error(e1.getMessage(), e1);
				return false;

			}

		} else {

			try {

				file.getParentFile().mkdir();

				file.createNewFile();

				return true;
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}

		}

		return false;

	}

	/**
	 * 
	 * @param songdata
	 * @param SpotifyUri
	 * 
	 * 
	 * 
	 */
	public void addSongtoJSON(SongData songdata, String SpotifyUri) {

		JsonObject obj = new JsonObject();
		obj.addProperty("title", songdata.getTitle());
		obj.addProperty("artist", songdata.getAuthor());
		obj.addProperty("dance", songdata.getDance());
		obj.addProperty("SpotifyURL", SpotifyUri);

		JsonArray arr = new JsonArray();
		JsonObject finaldata = new JsonObject();

		dancelist.values().forEach(val -> {

			arr.add(val);

		});
		arr.add(obj);

		finaldata.add("Songs", arr);

		try {
			BufferedWriter stream = Files.newBufferedWriter(Path.of("resources/dancelist.json"),
					Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);
			stream.write(finaldata.toString());
			stream.flush();
			stream.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	public TreeMap<String, JsonObject> getDancelist() {
		return this.dancelist;
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	public List<File> getFiles() {
		return this.data;
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	public LinkedHashMap<File, SongData> getPlaylistSongs() {
		return this.songs;
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 * 
	 */
	public List<File> getSongs() {
		return data;
	}

	public SongCheckThread getSongcheckT() {
		return songcheckT;
	}
}
