/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.graphics.listener.ArrowSpaceKeyListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.CustomKeyListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.FullscreenListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.NumberListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.RefreshListener;
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
	private JFrame mainFrame;
	private final Logger log;

	/**
	 * 
	 */
	protected SongWindowServer() {
		registeredWindows = new ArrayList<>();
		selectedWindow = 0;
		settingsOverride = new SongWindowSpecs();
		currentData = null;
		log = LoggerFactory.getLogger(getClass());

		initFrame();
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

	protected void initFrame() {

		mainFrame = new JFrame();

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Rectangle screenBounds = devices[0].getDefaultConfiguration().getBounds();

		mainFrame.setTitle("DanceInterpreter");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainFrame.setBounds(screenBounds);
		mainFrame.setLayout(null);

		mainFrame.getContentPane().setBackground(Color.BLACK);

		CustomKeyListener keylis = new CustomKeyListener();
		keylis.registerKeyListeners(new FullscreenListener(mainFrame));
		keylis.registerKeyListeners(new ArrowSpaceKeyListener());
		keylis.registerKeyListeners(new NumberListener());
		keylis.registerKeyListeners(new RefreshListener());
		mainFrame.addKeyListener(keylis);

		mainFrame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				if (selectedWindow >= registeredWindows.size()) {
					return;
				}

				registeredWindows.get(selectedWindow).refresh();
			}
		});
	}

	/**
	 * 
	 * @param data
	 */
	public void provideData(SongData data) {

		int mins = (int) (data.getDuration() / 60);
		log.info(data.getTitle() + ", " + data.getAuthor() + ", " + data.getDance() + ", " + mins + "min "
				+ (data.getDuration() - mins * 60) + "s");

		reselectWindow(data);
		currentData = data;
		registeredWindows.get(selectedWindow).updateData(data);
		mainFrame.requestFocus();
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
				selectedWindow = i;

				mainFrame.getContentPane().removeAll();
				registeredWindows.get(selectedWindow).onInit(mainFrame);
				mainFrame.repaint();
				mainFrame.setVisible(true);

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
