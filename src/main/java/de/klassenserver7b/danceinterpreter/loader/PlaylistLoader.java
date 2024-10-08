package de.klassenserver7b.danceinterpreter.loader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

public class PlaylistLoader {

	private final Logger log;

	public PlaylistLoader() {
		this.log = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * Opens file chooser to select playlist file
	 *
	 * @return Path of the selected file or null if no file was selected
	 */
	public File loadPlaylistFile() {
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		String dir = prefs.get("Last-Path", "");

		if (dir.isBlank() || !new File(dir).exists()) {
			if (Main.Instance.isWinOS()) {
				dir = System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH");
			} else {
				dir = System.getenv("HOME");
			}
		}

		JFileChooser fileChooser = generateFileChooser(dir);

		if (fileChooser.getSelectedFile() == null) {
			return null;
		}

		prefs.put("Last-Path", fileChooser.getSelectedFile().getAbsolutePath());
		return fileChooser.getSelectedFile();
	}

	protected JFileChooser generateFileChooser(String defaultDir) {

		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(
				new FileNameExtensionFilter("Playlist Files (*.m3u, *.m3u8, *.xspf)", "m3u", "m3u8", "xspf"));
		fileChooser.setCurrentDirectory(new File(defaultDir));
		fileChooser.setDialogTitle("Select Directory");
		fileChooser.setApproveButtonText("Select");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.showOpenDialog(null);

		return fileChooser;
	}

	public LinkedList<SongData> loadSongs(File f) {

		if (f == null || f.isDirectory() || !f.canRead()) {
			return null;
		}

		String[] fileparts = f.getName().split("\\.");

		LinkedList<SongData> songs;

		switch (fileparts[fileparts.length - 1]) {

		case "m3u", "m3u8" -> {
			this.log.info("loading m3u -> " + f.getPath());
			songs = loadM3U(f);
		}

		case "xspf" -> {
			this.log.info("loading xspf -> " + f.getPath());
			songs = loadXSPF(f);
		}
		default -> songs = null;
		}

		if (songs == null) {
			songs = new LinkedList<>();
		}

		return songs;
	}

	private LinkedList<SongData> loadM3U(File f) {

		LinkedList<SongData> songs = new LinkedList<>();
		List<String> lines = null;

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

			lines = reader.lines().toList();
			reader.close();

		} catch (IOException e) {
			this.log.error(e.getMessage(), e);
		}

		if (lines == null) {
			return null;
		}

		if (!lines.get(0).equalsIgnoreCase("#EXTM3U")) {
			return null;
		}

		for (String s : lines) {

			if (s.startsWith("#")) {
				continue;
			}

			String filePath = new File(f.getParent()).toURI() + s;

			File songFile = getFileFromEncodedString(filePath);

			if (songFile != null && songFile.exists()) {
				SongData data = FileLoader.getDataFromFile(songFile);

				songs.add(data);
			}

		}

		return songs;

	}

	private LinkedList<SongData> loadXSPF(File file) {
		LinkedList<SongData> songs = new LinkedList<>();

		Document doc = getXMLDoc(file);

		List<File> files = getFilesfromXML(doc);

		if (files == null || files.isEmpty()) {
			return null;
		}

		for (File f : files) {

			SongData data = FileLoader.getDataFromFile(f);

			songs.add(data);

		}

		return songs;

	}

	private List<File> getFilesfromXML(Document doc) {

		if (doc == null || !doc.hasChildNodes()) {
			return null;
		}

		NodeList tracks = doc.getElementsByTagName("track");

		List<File> ret = new ArrayList<>();

		for (int i = 0; i < tracks.getLength(); i++) {
			Node track = tracks.item(i);

			if (track.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) track;

				String encodedpath = e.getElementsByTagName("location").item(0).getTextContent();
				File f = getFileFromEncodedString(encodedpath);

				if (f != null && f.exists()) {
					ret.add(f);
				}
			}
		}

		return ret;

	}

	protected File getFileFromEncodedString(String encodedpath) {
		try {

			URI uri = new URI(encodedpath);
			return Paths.get(uri).toFile();

		} catch (URISyntaxException e1) {
			this.log.error(e1.getMessage(), e1);
		}

		return null;
	}

	private Document getXMLDoc(File f) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();

		try {

			String xmlstr = Files.readString(Path.of(f.toURI()));

			if (xmlstr == null) {
				return null;
			}

			DocumentBuilder docbuild = factory.newDocumentBuilder();
			return docbuild.parse(new ByteArrayInputStream(xmlstr.getBytes(StandardCharsets.UTF_8)));

		} catch (ParserConfigurationException | SAXException | IOException e) {
			this.log.error(e.getMessage(), e);
		}

		return null;
	}

}
