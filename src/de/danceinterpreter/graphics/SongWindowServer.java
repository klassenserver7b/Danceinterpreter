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
	private int selectedWindow;

	/**
	 * 
	 */
	protected SongWindowServer() {
		registeredWindows = new ArrayList<>();
		selectedWindow = 0;
	}

	public void provideData(SongData data) {
		reselectWindow(data);
		
		registeredWindows.get(selectedWindow).updateData(data);
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
		return registeredWindows.get(selectedWindow);
	}

	public void refresh() {
		registeredWindows.get(selectedWindow).refresh();
	}

	public static SongWindowServer createDefault() {
		SongWindowServer server = new SongWindowServer();
		server.registerSongWindows(new SongWindowBdImgTA());

		return server;
	}

	protected void reselectWindow(SongData data) {
		SongWindowSpecs dataspecs = data.toSongWindowSpecs();

		for (int i = 0; i < registeredWindows.size(); i++) {
			if (registeredWindows.get(i).getWindowSpecs().equals(dataspecs)) {
				selectedWindow = i;
				return;
			}
		}

	}
}
