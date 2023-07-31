/**
 * 
 */
package de.danceinterpreter.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.danceinterpreter.graphics.songwindows.SongWindowBdImgTA;
import de.danceinterpreter.songprocessing.SongData;

/**
 * @author klassenserver7b
 *
 */
public class SongWindowServer {

	private final List<FormattedSongWindow> registeredWindows;
	private int selectedWindow;
	private boolean allowImages;

	/**
	 * 
	 */
	protected SongWindowServer() {
		registeredWindows = new ArrayList<>();
		selectedWindow = 0;
		allowImages = true;
	}

	/**
	 * 
	 * @return
	 */
	public static SongWindowServer createDefault() {
		SongWindowServer server = new SongWindowServer();
		server.registerSongWindows(new SongWindowBdImgTA(true));
		server.registerSongWindows(new SongWindowBdImgTA(false));

		return server;
	}

	/**
	 * 
	 * @param data
	 */
	public void provideData(SongData data) {
		reselectWindow(data);

		registeredWindows.get(selectedWindow).updateData(data);
	}

	/**
	 * 
	 */
	public void refresh() {
		registeredWindows.get(selectedWindow).refresh();
	}

	/**
	 * 
	 * @param allowImages
	 */
	public void setAllowImages(boolean allowImages) {
		this.allowImages = allowImages;
	}

	/**
	 * 
	 * @param data
	 */
	protected void reselectWindow(SongData data) {
		SongWindowSpecs dataspecs = data.toSongWindowSpecs();

		if (!allowImages) {
			dataspecs = new SongWindowSpecs(false, dataspecs.containsArtist(), dataspecs.containsTitle(),
					dataspecs.containsDance());
		}

		for (int i = 0; i < registeredWindows.size(); i++) {
			if (registeredWindows.get(i).getWindowSpecs().equals(dataspecs)) {
				selectedWindow = i;
				return;
			}
		}

	}

	/**
	 * 
	 * @param window
	 */
	public void registerSongWindow(FormattedSongWindow window) {
		if (registeredWindows.contains(window)) {
			return;
		}

		registeredWindows.add(window);

	}

	/**
	 * 
	 * @param windows
	 */
	public void registerSongWindows(Collection<? extends FormattedSongWindow> windows) {
		for (FormattedSongWindow window : windows) {
			registerSongWindow(window);
		}
	}

	/**
	 * 
	 * @param windows
	 */
	public void registerSongWindows(FormattedSongWindow... windows) {
		registerSongWindows(Arrays.asList(windows));
	}

	/**
	 * 
	 * @return
	 */
	public FormattedSongWindow getWindow() {
		return registeredWindows.get(selectedWindow);
	}
}
