/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.klassenserver7b.danceinterpreter.graphics.songwindows.SongWindowBdImgTA;
import de.klassenserver7b.danceinterpreter.graphics.songwindows.SongWindowBdImgTAN;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
 * @author klassenserver7b
 *
 */
public class SongWindowServer {

	private final List<FormattedSongWindow> registeredWindows;
	private int selectedWindow;
	private SongWindowSpecs settingsOverride;
	private SongData currentData;

	/**
	 * 
	 */
	protected SongWindowServer() {
		registeredWindows = new ArrayList<>();
		selectedWindow = 0;
		settingsOverride = new SongWindowSpecs();
		currentData = null;
	}

	/**
	 * 
	 * @return
	 */
	public static SongWindowServer createDefault() {
		SongWindowServer server = new SongWindowServer();
		server.registerSongWindows(new SongWindowBdImgTA(true));
		server.registerSongWindows(new SongWindowBdImgTA(false));
		server.registerSongWindows(new SongWindowBdImgTAN(true));
		server.registerSongWindows(new SongWindowBdImgTAN(false));

		return server;
	}

	/**
	 * 
	 * @param data
	 */
	public void provideData(SongData data) {
		reselectWindow(data);
		currentData = data;
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
	 * @param data
	 */
	protected void reselectWindow(SongData data) {
		SongWindowSpecs dataspecs = applyOverride(data.toSongWindowSpecs());

		if (registeredWindows.get(selectedWindow).getWindowSpecs().equals(dataspecs)) {
			return;
		}

		for (int i = 0; i < registeredWindows.size(); i++) {
			if (registeredWindows.get(i).getWindowSpecs().equals(dataspecs)) {
				registeredWindows.get(selectedWindow).close();
				selectedWindow = i;

				return;
			}
		}

	}

	protected SongWindowSpecs applyOverride(SongWindowSpecs base) {

		boolean containsImage = (!settingsOverride.containsImage() ? false : base.containsImage());

		boolean containsArtist = (!settingsOverride.containsArtist() ? false : base.containsArtist());

		boolean containsTitle = (!settingsOverride.containsTitle() ? false : base.containsTitle());

		boolean containsDance = (!settingsOverride.containsDance() ? false : base.containsDance());

		boolean hasNext = (!settingsOverride.containsNext() ? false : base.containsNext());

		return new SongWindowSpecs(containsImage, containsArtist, containsTitle, containsDance, hasNext);

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

	public SongWindowSpecs getSettingsOverride() {
		return settingsOverride;
	}

	public void setSettingsOverride(SongWindowSpecs settingsOverride) {
		this.settingsOverride = settingsOverride;
		provideData(currentData);
	}

}
