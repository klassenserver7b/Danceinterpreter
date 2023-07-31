/**
 * 
 */
package de.danceinterpreter.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.danceinterpreter.songprocessing.SongData;

/**
 * @author felix
 *
 */
public class SongWindowServer {

	private final List<FormattedSongWindow> registeredWindows;

	/**
	 * 
	 */
	public SongWindowServer() {
		registeredWindows = new ArrayList<>();
	}

	public void provideData(SongData data) {
		// TODO
	}

	public synchronized void forceData(SongData data) {
		// TODO
	}

	public void registerSongWindow(FormattedSongWindow window) {
		if (registeredWindows.contains(window)) {
			return;
		}

		registeredWindows.add(window);

	}

	public void registerSongWindows(Collection<? extends FormattedSongWindow> windows) {
		for (FormattedSongWindow window : windows) {
			registerSongWindow(window);
		}
	}

	public void registerSongWindows(FormattedSongWindow... windows) {
		registerSongWindows(Arrays.asList(windows));
	}

	public FormattedSongWindow getWindow() {
		// TODO Provide selected
		return registeredWindows.get(0);
	}

	public void refresh() {
		// TODO -> forward to selected window (rescale, etc.)
	}

	public static SongWindowServer createDefault() {
		SongWindowServer server = new SongWindowServer();
		server.registerSongWindows(new SongWindowBdImgTA());

		return server;
	}
}
