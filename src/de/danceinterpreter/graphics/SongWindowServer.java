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

	}

	public void forceData(SongData data) {

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
}
