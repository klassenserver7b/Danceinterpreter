package de.danceinterpreter.loader;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
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

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import de.danceinterpreter.songprocessing.SongData;

public class PlaylistLoader {

	private final Logger log;

	public PlaylistLoader() {
		this.log = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * 
	 * @return
	 */
	public File loadPlaylistFile() {
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

		String dir = prefs.get("Last-Path", "");

		if (dir == null || dir.equalsIgnoreCase("") || (new File(dir)) == null) {

			dir = System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH");

		}

		JFileChooser f = new JFileChooser();
		f.setFileSelectionMode(JFileChooser.FILES_ONLY);
		f.setAcceptAllFileFilterUsed(false);
		f.setFileFilter(new FileNameExtensionFilter("Playlist Files (*.m3u, *.m3u8, *.xspf)", "m3u", "m3u8", "xspf"));
		f.setCurrentDirectory(new File(dir));
		f.setDialogTitle("Select Directory");
		f.setApproveButtonText("Select");
		f.setMultiSelectionEnabled(false);
		f.showOpenDialog(null);

		if (f.getSelectedFile() == null) {
			return null;
		}

		prefs.put("Last-Path", f.getSelectedFile().getAbsolutePath());
		return f.getSelectedFile();
	}

	public LinkedHashMap<File, SongData> loadSongs(File f) {

		if (f == null || f.isDirectory() || !f.canRead()) {
			return null;
		}

		String[] fileparts = f.getName().split("\\.");

		switch (fileparts[fileparts.length - 1]) {

		case "m3u", "m3u8" -> {
			log.info("loading m3u -> " + f.getPath());
			return loadM3U(f);
		}

		case "xspf" -> {
			log.info("loading xspf -> " + f.getPath());
			return loadXSPF(f);
		}

		default -> {
			return null;
		}

		}
	}

	private LinkedHashMap<File, SongData> loadM3U(File f) {

		LinkedHashMap<File, SongData> songs = new LinkedHashMap<>();
		List<String> lines = null;

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

			lines = reader.lines().toList();
			reader.close();

		} catch (IOException e) {
			log.error(e.getMessage(), e);
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

			String filepath = URLDecoder.decode(s, StandardCharsets.UTF_8);

			File song = new File(f.getParentFile().getAbsolutePath() + "/" + filepath);

			SongData data = getDataFromFile(song);

			songs.put(song, data);

		}

		return songs;

	}

	private LinkedHashMap<File, SongData> loadXSPF(File file) {
		LinkedHashMap<File, SongData> songs = new LinkedHashMap<>();

		Document doc = getXMLDoc(file);

		List<File> files = getFilesfromXML(doc);

		if (files == null || files.isEmpty()) {
			return null;
		}

		for (File f : files) {

			SongData data = getDataFromFile(f);

			songs.put(f, data);

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
				encodedpath = encodedpath.replaceAll("file:///", "");

				String path = URLDecoder.decode(encodedpath, StandardCharsets.UTF_8);

				ret.add(new File(path));

			}
		}

		return ret;

	}

	private Document getXMLDoc(File f) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();

		try {

			String xmlstr = Files.readString(Path.of(f.toURI()));

			if (xmlstr == null) {
				return null;
			}

			DocumentBuilder docbuild = factory.newDocumentBuilder();
			Document doc = docbuild.parse(new ByteArrayInputStream(xmlstr.getBytes(StandardCharsets.UTF_8)));

			return doc;

		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	private SongData getDataFromFile(File f) {

		if (f == null || f.isDirectory() || !f.canRead()) {
			return null;
		}

		SongData ret = null;

		Mp3File mp3file;
		try {
			mp3file = new Mp3File(f);

			if (mp3file.hasId3v2Tag()) {

				ID3v2 tags = mp3file.getId3v2Tag();
				String title = tags.getTitle();
				String author = tags.getArtist();

				byte[] imageData = tags.getAlbumImage();

				BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);

				// converting the bytes to an image
				if (imageData != null) {
					try {
						img = ImageIO.read(new ByteArrayInputStream(imageData));
					} catch (IOException e) {
						this.log.error("Couldn't parse ID3 Cover");
						log.error(e.getMessage(), e);
					}
				} else {
					this.log.info("Song doesn't have a Cover");
				}

				Long length = mp3file.getLength();

				String dance = tags.getComment();

				ret = new SongData(title, author, dance, length, img);

			} else {

				ret = new SongData("Unknown", "Unknown", "null", 0L,
						new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB));
			}

		} catch (UnsupportedTagException | InvalidDataException | IOException e1) {
			log.error(e1.getMessage(), e1);
		}

		return ret;

	}

}
