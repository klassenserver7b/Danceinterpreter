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

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.graphics.listener.ArrowSpaceKeyListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.CustomKeyListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.FullscreenListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.NumberListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.RefreshListener;
import de.klassenserver7b.danceinterpreter.graphics.songwindows.SongWindowBdImgTA;
import de.klassenserver7b.danceinterpreter.graphics.songwindows.SongWindowBdImgTAN;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
 * @author K7
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
		this.registeredWindows = new ArrayList<>();
		this.selectedWindow = 0;
		this.settingsOverride = new SongWindowSpecs();
		this.currentData = null;
		this.log = LoggerFactory.getLogger(getClass());

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

		this.mainFrame = new JFrame();

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Rectangle screenBounds = devices[0].getDefaultConfiguration().getBounds();

		this.mainFrame.setTitle("DanceInterpreter");
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.mainFrame.setBounds(screenBounds);
		this.mainFrame.setLayout(null);

		this.mainFrame.setAutoRequestFocus(true);

		this.mainFrame.getContentPane().setBackground(Color.BLACK);

		CustomKeyListener keylis = new CustomKeyListener();
		keylis.registerKeyListeners(new FullscreenListener(this.mainFrame));
		keylis.registerKeyListeners(new ArrowSpaceKeyListener());
		keylis.registerKeyListeners(new NumberListener());
		keylis.registerKeyListeners(new RefreshListener());
		this.mainFrame.addKeyListener(keylis);

		this.mainFrame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				if (SongWindowServer.this.selectedWindow >= SongWindowServer.this.registeredWindows.size()) {
					return;
				}

				SongWindowServer.this.registeredWindows.get(SongWindowServer.this.selectedWindow).refresh();
			}
		});

		Main.Instance.getConfigWindow().initKeyListeners(keylis);
	}

	/**
	 * 
	 * @param data
	 */
	public void provideData(SongData data) {

		int mins = (int) (data.getDuration() / 60);
		this.log.info(data.getTitle() + ", " + data.getArtist() + ", " + data.getDance() + ", " + mins + "min "
				+ (data.getDuration() - mins * 60) + "s");

		reselectWindow(data);
		this.currentData = data;
		this.registeredWindows.get(this.selectedWindow).updateData(data);
		this.mainFrame.requestFocus();
	}

	/**
	 * 
	 */
	public void refresh() {
		this.registeredWindows.get(this.selectedWindow).refresh();
	}

	/**
	 * 
	 * @param data
	 */
	protected void reselectWindow(SongData data) {
		SongWindowSpecs dataspecs = applyOverride(data.toSongWindowSpecs());

		if (this.registeredWindows.get(this.selectedWindow).getWindowSpecs().equals(dataspecs)) {
			return;
		}

		for (int i = 0; i < this.registeredWindows.size(); i++) {
			if (this.registeredWindows.get(i).getWindowSpecs().equals(dataspecs)) {
				this.selectedWindow = i;

				this.mainFrame.getContentPane().removeAll();
				this.registeredWindows.get(this.selectedWindow).onInit(this.mainFrame);
				this.mainFrame.repaint();
				this.mainFrame.setVisible(true);

				return;
			}
		}

	}

	protected SongWindowSpecs applyOverride(SongWindowSpecs base) {

		boolean containsImage = (!this.settingsOverride.containsImage() ? false : base.containsImage());

		boolean containsArtist = (!this.settingsOverride.containsArtist() ? false : base.containsArtist());

		boolean containsTitle = (!this.settingsOverride.containsTitle() ? false : base.containsTitle());

		boolean containsDance = (!this.settingsOverride.containsDance() ? false : base.containsDance());

		boolean hasNext = (!this.settingsOverride.containsNext() ? false : base.containsNext());

		return new SongWindowSpecs(containsImage, containsArtist, containsTitle, containsDance, hasNext);

	}

	/**
	 * 
	 * @param window
	 */
	public void registerSongWindow(FormattedSongWindow window) {
		if (this.registeredWindows.contains(window)) {
			return;
		}

		this.registeredWindows.add(window);

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
		return this.registeredWindows.get(this.selectedWindow);
	}

	public SongWindowSpecs getSettingsOverride() {
		return this.settingsOverride;
	}

	public void setSettingsOverride(SongWindowSpecs settingsOverride) {
		this.settingsOverride = settingsOverride;
		provideData(this.currentData);
	}

	/**
	 * @return the mainFrame
	 */
	public JFrame getMainFrame() {
		return this.mainFrame;
	}

}
