package de.danceinterpreter.Songprocessing;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.sun.nio.file.ExtendedOpenOption;

import de.danceinterpreter.Main;
import de.danceinterpreter.Graphics.SongWindow;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

/**
 * 
 * @author felix
 *
 */
public class DanceInterpreter {

	public JsonObject dancelist;
	public Thread songcheckT;
	public int datahash = 0;
	public SongWindow window;
	public final List<File> data = new ArrayList<>();
	private final Logger log = LoggerFactory.getLogger("Interpreter");
	private int hash = 0;

	/**
	 * 
	 */
	public void startLocalSongCheck() {

		if (!initialize()) {
			return;
		}
		if (!loadWorkingDirectory()) {
			return;
		}

		songcheckT = new Thread(() -> {

			if (!this.songcheckT.isInterrupted()) {
				long time = System.currentTimeMillis();

				while (!Main.exit) {
					if (System.currentTimeMillis() >= time + 10000) {
						time = System.currentTimeMillis();

						Songdata data = provideLocalSongData(true);

						if (data != null) {
							log.info("Songchange detected: " + data.getTitle() + ", " + data.getAuthor() + ", "
									+ data.getDance() + ", " + data.getDuration());
							if (this.window == null) {
								this.window = new SongWindow(data.getTitle(), data.getAuthor(), data.getDance(),
										data.getImageURL());
							} else {
								this.window.UpdateWindow(data.getTitle(), data.getAuthor(), data.getDance(),
										data.getImageURL());
							}
						}
					}
				}
			}

		});
		songcheckT.setName("SongCheck");
		songcheckT.start();
	}

	/**
	 * 
	 */
	public void startSpotifySongCheck() {

		if (!initialize()) {
			return;
		}

		songcheckT = new Thread(() -> {

			if (!this.songcheckT.isInterrupted()) {
				long time = System.currentTimeMillis();

				while (!Main.exit) {
					if (System.currentTimeMillis() >= time + 5000) {
						time = System.currentTimeMillis();

						Songdata data = provideSpotifySongData();

						if (data != null) {
							log.info(data.getTitle() + ", " + data.getAuthor() + ", " + data.getDance() + ", "
									+ data.getDuration());
							if (this.window == null) {
								this.window = new SongWindow(data.getTitle(), data.getAuthor(), data.getDance(),
										data.getImageURL());
							} else {
								this.window.UpdateWindow(data.getTitle(), data.getAuthor(), data.getDance(),
										data.getImageURL());
							}
						}
					}
				}
			}

		});
		songcheckT.setName("SongCheck");
		songcheckT.start();

	}

	/**
	 * 
	 */
	public void provideAsynchronous() {
		Songdata data = provideLocalSongData(false);

		if (data != null) {
			log.info(data.getTitle() + ", " + data.getAuthor() + ", " + data.getDance() + ", " + data.getDuration());
			if (this.window == null) {
				this.window = new SongWindow(data.getTitle(), data.getAuthor(), data.getDance(), data.getImageURL());
			} else {
				this.window.UpdateWindow(data.getTitle(), data.getAuthor(), data.getDance(), data.getImageURL());
			}
		}
	}

	/**
	 * 
	 * @return
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
	 */
	private void findAllFilesInFolder(File f) {

		for (File file : f.listFiles()) {
			if (!file.isDirectory() && file.getName().endsWith(".mp3")) {
				data.add(file);
			} else {
				findAllFilesInFolder(file);
			}
		}

	}

	/**
	 * 
	 */
	public void shutdown() {

		if (this.songcheckT != null) {
			this.songcheckT.interrupt();
		}

		if (this.window != null) {
			if (this.window.mainframe != null) {
				this.window.mainframe.dispose();
				this.window.mainframe.setVisible(false);
				this.window.mainframe = null;
			}
			if (this.window.mainpanel != null) {
				this.window.mainpanel.removeAll();
				this.window.mainpanel.setEnabled(false);
				this.window.mainpanel = null;
			}
		}
	}

	/**
	 * 
	 * @return
	 */

	private String getWorkingDirectory() {

		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		
		String dir = prefs.get("Last-Path", "");
		
		if(dir==null || dir.equalsIgnoreCase("") || (new File(dir)) ==null) {
			
			dir = System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH");
			
		}
		
		JFileChooser f = new JFileChooser();
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		f.setCurrentDirectory(new File(dir));
		f.setDialogTitle("Ordner auswählen");
		f.setApproveButtonText("Auswählen");
		f.setMultiSelectionEnabled(false);
		f.showSaveDialog(null);

		if (f.getSelectedFile() != null) {
			prefs.put("Last-Path", f.getSelectedFile().getAbsolutePath());
			return f.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

	/**
	 * 
	 * @param checkcurrent
	 * @return
	 */
	private Songdata provideLocalSongData(boolean checkcurrent) {

		File f = getLocalSong(checkcurrent);

		Songdata ret = null;

		Mp3File mp3file;
		if (f != null) {
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
							e.printStackTrace();
						}
					} else {
						this.log.info("Song doesn't have a Cover");
					}

					Integer length = tags.getLength();

					ret = new Songdata(title, author, getDance(title, author), length.longValue(), img);

				} else {
					ret = new Songdata("Unknown", "Unknown", "null", 0L,
							new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB));
				}

			} catch (UnsupportedTagException | InvalidDataException | IOException e1) {
				e1.printStackTrace();
			}
		}

		if (ret != null && datahash != ret.hashCode()) {
			datahash = ret.hashCode();
			return ret;
		}
		return null;
	}

	/**
	 * 
	 * @param checkcurrent
	 * @return
	 */
	private File getLocalSong(boolean checkcurrent) {

		List<File> blocked = new ArrayList<>();

		this.data.forEach(file -> {
			FileChannel chan = null;
			FileLock lock = null;

			try {

				chan = FileChannel.open(file.toPath(), ExtendedOpenOption.NOSHARE_READ);
				lock = chan.tryLock();

			} catch (NonWritableChannelException e) {

				if (chan != null) {
					try {
						chan.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					chan = null;
				}

				if (lock != null) {
					try {
						lock.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					lock = null;
				}

			} catch (IOException ex) {
				this.log.debug("Locked - f: " + file.getName());
				blocked.add(file);

				if (chan != null) {
					try {
						chan.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					chan = null;
				}
			}

		});

		boolean checked = checkcurrent;

		if (checkcurrent) {
			checked = (blocked.hashCode() != hash);
		} else {
			checked = true;
		}

		if (!blocked.isEmpty() && checked) {

			this.hash = blocked.hashCode();
			if (blocked.size() == 1) {
				return blocked.get(0);
			}

			ConcurrentHashMap<String, File> fileoptions = new ConcurrentHashMap<>();

			for (File f : blocked) {
				fileoptions.put(f.getName(), f);
			}

			String filename = (String) JOptionPane.showInputDialog(null, "Which is the current Song?", "Song select",
					JOptionPane.QUESTION_MESSAGE, null, fileoptions.keySet().toArray(new String[0]), null);

			if (filename != null) {
				return fileoptions.get(filename);
			}
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	private Songdata provideSpotifySongData() {
		Track cutrack = getCurrentSpotifySong();

		if (cutrack != null) {

			StringBuilder authbuild = new StringBuilder();
			String imgurl = cutrack.getAlbum().getImages()[0].getUrl();

			ArtistSimplified[] artists = cutrack.getArtists();

			for (int i = 0; i < artists.length; i++) {
				authbuild.append(artists[i].getName());
				if (i != artists.length - 1) {
					authbuild.append(", ");
				}
			}

			String authors = authbuild.toString().trim();
			String dance = getDance(cutrack.getUri());

			if (dance == null) {
				JsonObject obj = new JsonObject();
				obj.addProperty("songname", cutrack.getName());
				obj.add("dance", null);
				dancelist.add(cutrack.getUri(), obj);

				try {
					BufferedWriter stream = Files.newBufferedWriter(Path.of("resources/dancelist.json"),
							Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);
					stream.write(dancelist.toString());
					stream.flush();
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			Songdata ret;
			try {
				ret = new Songdata(cutrack.getName(), authors, dance, (long) (cutrack.getDurationMs() / 1000), imgurl);
				if (datahash != ret.hashCode()) {
					datahash = ret.hashCode();
					return ret;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	private Track getCurrentSpotifySong() {

		SpotifyApi spotifyapi = Main.Instance.getSpotifyAPI();

		GetUsersCurrentlyPlayingTrackRequest currenttrackrequest = spotifyapi.getUsersCurrentlyPlayingTrack().build();
		try {
			CurrentlyPlaying curplay = currenttrackrequest.execute();

			if (curplay != null) {
				IPlaylistItem item = curplay.getItem();

				if (item == null) {
					return null;
				}

				String spotifyid = item.getId();

				GetTrackRequest trackreq = spotifyapi.getTrack(spotifyid).build();

				return trackreq.execute();
			} else {
				return null;
			}

		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 
	 * @param searchquery
	 * @return
	 */
	private String getDance(String spotifyuri) {

		JsonElement dancelem = dancelist.get(spotifyuri);

		if (dancelem != null) {
			JsonElement stringelem = dancelem.getAsJsonObject().get("dance");

			if (stringelem != null && !stringelem.toString().equalsIgnoreCase("")) {
				return stringelem.toString().replaceAll("\"", "");
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
	 */
	private String getDance(String title, String author) {

		JsonElement dancelem = dancelist.get(author + " - " + title);

		if (dancelem != null) {
			JsonElement stringelem = dancelem.getAsJsonObject().get("dance");

			if (stringelem != null && !stringelem.toString().equalsIgnoreCase("")) {
				return stringelem.toString().replaceAll("\"", "");
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
	 */
	private boolean initialize() {

		File file = new File("resources/dancelist.json");

		if (file.exists()) {

			try {

				String jsonstring = Files.readString(Path.of(file.getPath()));

				JsonElement json = JsonParser.parseString(jsonstring);
				dancelist = json.getAsJsonObject();

				return true;

			} catch (IOException e1) {

				e1.printStackTrace();

				return false;

			}

		}

		return false;

	}

	/**
	 * 
	 * @return
	 */
	public SongWindow getWindow() {
		return this.window;
	}
}
