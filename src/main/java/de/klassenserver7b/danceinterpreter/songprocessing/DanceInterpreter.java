package de.klassenserver7b.danceinterpreter.songprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
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

import de.klassenserver7b.danceinterpreter.AppModes;
import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.loader.PlaylistLoader;
import de.klassenserver7b.danceinterpreter.threads.SongCheckThread;

/**
 *
 */
public class DanceInterpreter {

    private final TreeMap<String, JsonObject> dancelist = new TreeMap<>();
    private SongCheckThread songcheckT;

    private LinkedList<SongData> playlistSongs;

    private final List<File> localMp3Files;

    private final Logger log;

    public DanceInterpreter() {
        this.localMp3Files = new ArrayList<>();
        this.log = LoggerFactory.getLogger("Danceinterpreter");
    }

    /**
     * Starts the song check thread in specified mode
     *
     * @return Whether file loading / starting was successful
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
            if ((playlistSongs = new PlaylistLoader().loadSongs(playlist)) == null) {
                log.debug("Invalid Playlist File - couldn't load songs!");
                return false;
            }
            log.debug("Playlist sucessfully loaded!");
            Main.Instance.getSongWindowServer().provideData(appmode.getDataProvider().provideSongData());
            return true;
        }

        songcheckT = new SongCheckThread(appmode);
        return true;
    }

    /**
     * Lets the user select a working directory and loads all mp3 files in it
     *
     * @return Whether loading was successful
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
     * Recursively searches for all mp3 files in a given folder and adds them to the list of local mp3 files
     *
     * @param f Folder
     */
    @SuppressWarnings("DataFlowIssue")
    private void findAllFilesInFolder(File f) {

        if (f == null || !f.exists() || !f.isDirectory()) {
            return;
        }

        for (File file : f.listFiles()) {
            if (!file.isDirectory()) {
                if (file.getName().endsWith(".mp3")) {
                    localMp3Files.add(file);
                }
            } else {
                findAllFilesInFolder(file);
            }
        }

    }

    /**
     * Opens file chooser to select working directory
     *
     * @return Path of the selected directory or null if no directory was selected
     */
    private String getWorkingDirectory() {

        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

        String dir = prefs.get("Last-Path", "");

        if (dir.isBlank() || !new File(dir).exists()) {
            dir = Main.Instance.getHomeDir();
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(dir));
        fileChooser.setDialogTitle("Select Directory");
        fileChooser.setApproveButtonText("Select");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.showOpenDialog(null);

        if (fileChooser.getSelectedFile() != null) {
            prefs.put("Last-Path", fileChooser.getSelectedFile().getAbsolutePath());
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    /**
     * @param spotifyuri Song's URI on Spotify
     * @return Name of dance or "unknown" if song was not found
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
     * @param title  Title of the song to search for
     * @param author Author of the song to search for
     * @return Name of dance or "unknown" if song was not found
     */
    @SuppressWarnings("unused")
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
     * @return Whether initialization was successful
     */
    private boolean initialize() {

        File file = new File("./resources/dancelist.json");

        if (file.exists()) {

            try {

                String jsonstring = Files.readString(Path.of(file.getPath()));

                JsonElement json = JsonParser.parseString(jsonstring);

                if (json != null) {
                    JsonArray arr = json.getAsJsonObject().get("Songs").getAsJsonArray();

                    //noinspection SwitchStatementWithTooFewBranches
                    switch (Main.Instance.getAppMode()) {
                        case Spotify -> {

                            for (JsonElement e : arr) {

                                JsonObject obj = e.getAsJsonObject();
                                dancelist.put(obj.get("SpotifyURL").getAsString(), obj);

                            }

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

                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdir();
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();

                return true;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

        }

        return false;

    }

    /**
     * Creates an entry in dancelist.json for a song
     *
     * @param songdata   SongData of the song
     * @param SpotifyUri Song's URI on Spotify
     */
    public void addSongtoJSON(SongData songdata, String SpotifyUri) {

        JsonObject obj = new JsonObject();
        obj.addProperty("title", songdata.getTitle());
        obj.addProperty("artist", songdata.getAuthor());
        obj.addProperty("dance", songdata.getDance());
        obj.addProperty("SpotifyURL", SpotifyUri);

        JsonArray arr = new JsonArray();
        JsonObject finaldata = new JsonObject();

        dancelist.values().forEach(arr::add);
        arr.add(obj);

        finaldata.add("Songs", arr);

        try {
            BufferedWriter stream = Files.newBufferedWriter(Path.of("resources/dancelist.json"),
                    StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            stream.write(finaldata.toString());
            stream.flush();
            stream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * @return The dancelist
     */
    @SuppressWarnings("unused")
    public TreeMap<String, JsonObject> getDancelist() {
        return this.dancelist;
    }

    /**
     * @return List of loaded local mp3 files or an empty list if AppMode is not LocalMP3
     */
    public List<File> getFiles() {
        return this.localMp3Files;
    }

    /**
     * @return List of songs in the playlist or null if AppMode is not Playlist
     */
    public LinkedList<SongData> getPlaylistSongs() {
        return this.playlistSongs;
    }

    @SuppressWarnings("unused")
    public SongCheckThread getSongcheckT() {
        return songcheckT;
    }
}
