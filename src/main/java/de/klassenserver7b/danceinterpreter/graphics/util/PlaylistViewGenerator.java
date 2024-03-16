/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics.util;

import java.awt.Font;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.graphics.listener.PlaylistViewClickListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.PlaylistViewClickListener.ClickListenerType;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
 * 
 */
public class PlaylistViewGenerator {

	private static final String CELLFORMAT = "<html><body>Title: %s<br>Artist: %s<br>Dance: %s</body></html>";
	private final Logger log;
	private JsonArray staticSongs;
	private JsonArray staticLabels;
	private boolean playlistViewEnabled;

	/**
	 * 
	 */
	public PlaylistViewGenerator() {
		this.log = LoggerFactory.getLogger(getClass());
		this.staticSongs = new JsonArray();
		this.staticLabels = new JsonArray();

		try {
			loadJsonDefaults();
		} catch (JsonSyntaxException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void loadJsonDefaults() throws JsonSyntaxException {

		InputStream deffile = getClass().getResourceAsStream("/defaultmenu.json");

		JsonObject defaults = JsonParser.parseReader(new InputStreamReader(deffile)).getAsJsonObject();

		staticSongs = defaults.get("songs").getAsJsonArray();
		staticLabels = defaults.get("labels").getAsJsonArray();
	}

	public boolean save() {
		try {

			File f = openSaveDialogue();

			if (f == null) {
				return false;
			}

			JsonObject saveobj = new JsonObject();
			saveobj.add("songs", staticSongs);
			saveobj.add("labels", staticLabels);

			try (Writer writer = Files.newBufferedWriter(f.toPath(), StandardCharsets.UTF_8,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				gson.toJson(saveobj, writer);
			}

		} catch (IOException e) {
			this.log.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	public boolean load() {
		File f = openOpenDialogue();

		try {

			JsonReader jsonReader = new JsonReader(new FileReader(f));
			JsonElement jsonElem = JsonParser.parseReader(jsonReader);

			if (!jsonElem.isJsonObject()) {
				sendFailedImport();
				return false;
			}

			JsonObject jsonObj = jsonElem.getAsJsonObject();

			if (!validateJsonObject(jsonObj)) {
				sendFailedImport();
				return false;
			}

			JsonElement songsElem = jsonObj.get("songs");
			JsonElement labelsElem = jsonObj.get("labels");

			this.staticSongs = songsElem.getAsJsonArray();
			this.staticLabels = labelsElem.getAsJsonArray();

		} catch (FileNotFoundException e) {
			this.log.error(e.getMessage(), e);
			return false;
		}

		Main.Instance.getConfigWindow().updateWindow();
		return true;
	}

	protected void sendFailedImport() {

		TrayIcon trayIcon = Main.Instance.getTrayIcon();

		if (trayIcon == null) {
			return;
		}

		trayIcon.displayMessage("Import failed", "couldn't import Json", MessageType.ERROR);
	}

	protected boolean validateJsonObject(JsonObject mainObj) {

		JsonElement songsElem = mainObj.get("songs");
		JsonElement labelsElem = mainObj.get("labels");

		if (!songsElem.isJsonArray() || !labelsElem.isJsonArray()) {
			return false;
		}

		if (!validateSongs(songsElem.getAsJsonArray())) {
			return false;
		}

		if (!validateLabels(labelsElem.getAsJsonArray())) {
			return false;
		}

		return true;
	}

	protected boolean validateSongs(JsonArray songsArr) {

		for (JsonElement songElem : songsArr) {

			if (!songElem.isJsonObject()) {
				return false;
			}

			JsonObject songObj = songElem.getAsJsonObject();
			JsonElement checkElem;

			if ((checkElem = songObj.get("title")) == null || !checkElem.isJsonPrimitive()) {
				return false;
			}
			if ((checkElem = songObj.get("artist")) == null || !checkElem.isJsonPrimitive()) {
				return false;
			}
			if ((checkElem = songObj.get("dance")) == null || !checkElem.isJsonPrimitive()) {
				return false;
			}
		}

		return true;
	}

	protected boolean validateLabels(JsonArray labelsArr) {

		for (JsonElement labelElem : labelsArr) {

			if (!labelElem.isJsonObject()) {
				return false;
			}

			JsonObject labelObj = labelElem.getAsJsonObject();
			JsonElement checkElem;

			if ((checkElem = labelObj.get("text")) == null || !checkElem.isJsonPrimitive()) {
				return false;
			}
			if ((checkElem = labelObj.get("format")) == null || !checkElem.isJsonPrimitive()) {
				return false;
			}
		}

		return true;
	}

	public List<JLabel> loadPlaylistView() {

		if (!playlistViewEnabled) {
			return null;
		}

		List<JLabel> labels = new LinkedList<>();
		labels.addAll(loadSongs());
		labels.addAll(loadStaticSongs());
		labels.addAll(loadStaticLabels());

		return labels;

	}

	protected List<JLabel> loadSongs() {

		List<JLabel> songLabels = new LinkedList<>();

		LinkedList<SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

		for (int i = 0; i < songs.size(); i++) {

			JLabel songp = generateGenericLabel();

			songp.addMouseListener(new PlaylistViewClickListener(ClickListenerType.PLAYLIST, i, this));
			songLabels.add(songp);

			SongData songData = songs.get(i);

			songp.setText(String.format(CELLFORMAT, songData.getTitle(), songData.getArtist(), songData.getDance()));

		}

		return songLabels;

	}

	protected List<JLabel> loadStaticSongs() {
		List<JLabel> staticSongsLables = new LinkedList<>();

		for (int i = 0; i < staticSongs.size(); i++) {

			JLabel songp = generateGenericLabel();
			songp.addMouseListener(new PlaylistViewClickListener(ClickListenerType.STATIC_SONG, i, this));

			songp.setFont(new Font("Arial", Font.ITALIC, 12));
			staticSongsLables.add(songp);

			JsonObject song = staticSongs.get(i).getAsJsonObject();

			songp.setText(String.format(CELLFORMAT, song.get("title").getAsString(), song.get("artist").getAsString(),
					song.get("dance").getAsString()));

		}

		return staticSongsLables;
	}

	protected List<JLabel> loadStaticLabels() {
		List<JLabel> labels = new LinkedList<>();

		for (int i = 0; i < staticLabels.size(); i++) {

			JLabel songp = generateGenericLabel();

			songp.addMouseListener(new PlaylistViewClickListener(ClickListenerType.STATIC_LABEL, i, this));

			labels.add(songp);

			JsonObject label = staticLabels.get(i).getAsJsonObject();

			int style = switch (label.get("format").getAsString()) {

			case "b" -> Font.BOLD;
			case "i" -> Font.ITALIC;
			case "bi", "ib" -> Font.ITALIC | Font.BOLD;
			default -> Font.PLAIN;

			};

			songp.setFont(new Font("Arial", style, 20));

			String text = label.get("text").getAsString();

			if (text.equalsIgnoreCase("")) {
				text = "BLANK";
			}

			songp.setText(text);

		}

		return labels;
	}

	protected JLabel generateGenericLabel() {

		JLabel songp = new JLabel();
		songp.setSize(200, 200);
		songp.setBorder(BorderFactory.createLineBorder(Main.Instance.getTextColor(), 3, true));
		songp.setForeground(Main.Instance.getTextColor());
		songp.setBackground(Main.Instance.getBackgroundColor());

		return songp;
	}

	protected File openSaveDialogue() throws IOException {

		File selectedFile;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("Save to:");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setFileFilter(new FileNameExtensionFilter("json files (.json)", "json"));
		int opt = fileChooser.showSaveDialog(null);

		switch (opt) {

		case JFileChooser.APPROVE_OPTION -> {
			selectedFile = fileChooser.getSelectedFile();

			if (!selectedFile.getName().endsWith(".json")) {
				selectedFile = new File(selectedFile.getAbsolutePath() + ".json");
			}

			if (!selectedFile.exists()) {
				selectedFile.createNewFile();
			}

		}
		case JFileChooser.CANCEL_OPTION -> {
			return null;
		}

		default -> {
			this.log.error("Illegal JFileChooser Status - can't save");
			return null;
		}

		}

		return selectedFile;

	}

	/**
	 * @return
	 * @throws IllegalStateException
	 */
	protected File openOpenDialogue() {

		File selectedFile;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("Select file to open:");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setFileFilter(new FileNameExtensionFilter("json files (.json)", "json"));
		int opt = fileChooser.showOpenDialog(null);

		switch (opt) {

		case JFileChooser.APPROVE_OPTION -> {

			selectedFile = fileChooser.getSelectedFile();

			if (!selectedFile.isFile()) {
				return null;
			}

			if (!selectedFile.exists()) {
				return null;
			}

		}
		case JFileChooser.CANCEL_OPTION -> {
			return null;
		}

		default -> {
			this.log.error("Illegal JFileChooser Status - can't save");
			return null;
		}

		}

		return selectedFile;

	}

	public void addSong(String title, String artist, String dance) {
		JsonObject song = new JsonObject();

		song.addProperty("title", title);
		song.addProperty("artist", artist);
		song.addProperty("dance", dance);
		staticSongs.add(song);

		Main.Instance.getConfigWindow().updateWindow();
	}

	public void addLabel(String text, String format) {
		JsonObject song = new JsonObject();

		song.addProperty("text", text);
		song.addProperty("format", format);
		staticLabels.add(song);

		Main.Instance.getConfigWindow().updateWindow();
	}

	public SongData getStaticSong(int index) {
		JsonElement elem = staticSongs.get(index);

		if (elem == null) {
			return null;
		}
		JsonObject obj = elem.getAsJsonObject();

		return new SongData(obj.get("title").getAsString(), obj.get("artist").getAsString(),
				obj.get("dance").getAsString());
	}

	public SongData getStaticLabel(int index) {
		JsonElement elem = staticLabels.get(index);

		if (elem == null) {
			return null;
		}
		JsonObject obj = elem.getAsJsonObject();

		return new SongData("", "", obj.get("text").getAsString());
	}

	/**
	 * @return the playlistViewEnabled
	 */
	public boolean isPlaylistViewEnabled() {
		return this.playlistViewEnabled;
	}

	/**
	 * @param playlistViewEnabled the playlistViewEnabled to set
	 */
	public void setPlaylistViewEnabled(boolean playlistViewEnabled) {
		this.playlistViewEnabled = playlistViewEnabled;
	}

}
