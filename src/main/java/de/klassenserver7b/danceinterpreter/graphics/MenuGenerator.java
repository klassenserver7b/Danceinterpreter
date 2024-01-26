/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.AppModes;
import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.connections.SpotifyInteractions;
import de.klassenserver7b.danceinterpreter.graphics.icons.ExitIcon;

/**
 * @author K7
 *
 */
public class MenuGenerator {

	private final Logger log;
	private final ConfigWindow cfgwindow;

	/**
	 * 
	 */
	public MenuGenerator(ConfigWindow window) {
		this.log = LoggerFactory.getLogger(getClass());
		this.cfgwindow = window;
	}

	/**
	 * 
	 * @return
	 */
	public JMenuBar getMenuBar() {
		JMenuBar bar = new JMenuBar();

		bar.add(getFileMenu());

		bar.add(getEditMenu());

		bar.add(getSongWindowMenu());

		bar.setVisible(true);
		return bar;
	}

	protected JMenu getFileMenu() {

		JMenu filem = new JMenu("File");
		filem.add(getExit());
		return filem;
	}

	protected JMenu getEditMenu() {

		JMenu editm = new JMenu("Edit");
		editm.add(getConfigAnimationCheck());

		if (Main.Instance.getAppMode() == AppModes.Playlist) {
			editm.addSeparator();
			editm.add(getPlaylistViewCheck());
			editm.add(getPlaylistViewExport());
			editm.add(getPlaylistViewImport());
		}

		if (Main.Instance.getAppMode() == AppModes.Spotify) {
			editm.addSeparator();
			editm.add(getSpotifyReset());

		}

		return editm;

	}

	protected JMenu getSongWindowMenu() {

		JMenu songwindowm = new JMenu("SongWindow");

		songwindowm.add(getPictureCheck());
		songwindowm.add(getNextCheck());
		songwindowm.add(getRefreshWindow());

		return songwindowm;
	}

	protected JMenuItem getSpotifyReset() {

		JMenuItem spi = new JMenuItem("Reset Spotify Config");
		spi.addActionListener(e -> {
			Preferences prefs = Preferences.userRoot().node(new File("").getParent() + "_" + new File("").getName()
					+ "_" + SpotifyInteractions.class.getName());
			try {
				prefs.clear();
			} catch (BackingStoreException e1) {
				this.log.error(e1.getMessage(), e1);
			}

		});

		return spi;

	}

	protected JMenuItem getExit() {
		JMenuItem exitI = new JMenuItem("Exit");
		exitI.setIcon(new ExitIcon());
		exitI.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		exitI.addActionListener(e -> System.exit(0));
		return exitI;
	}

	protected JMenuItem getRefreshWindow() {

		JMenuItem label = new JMenuItem();

		label.setText("Refresh SongWindow");
		label.addActionListener(e -> Main.Instance.getSongWindowServer().refresh());

		return label;
	}

	protected JCheckBoxMenuItem getPictureCheck() {

		JCheckBoxMenuItem pictureI = new JCheckBoxMenuItem();
		pictureI.setText("Show Thumbnails");
		pictureI.setSelected(true);
		pictureI.addActionListener(e -> {

			SongWindowSpecs current = Main.Instance.getSongWindowServer().getSettingsOverride();

			current.setContainsImage(pictureI.getState());

			Main.Instance.getSongWindowServer().setSettingsOverride(current);

		});

		return pictureI;
	}

	protected JCheckBoxMenuItem getNextCheck() {

		JCheckBoxMenuItem hasNextI = new JCheckBoxMenuItem();
		hasNextI.setText("Show Next Dance");
		hasNextI.setSelected(true);
		hasNextI.addActionListener(e -> {

			SongWindowSpecs current = Main.Instance.getSongWindowServer().getSettingsOverride();

			current.setContainsNext(hasNextI.getState());

			Main.Instance.getSongWindowServer().setSettingsOverride(current);

		});

		return hasNextI;
	}

	protected JCheckBoxMenuItem getConfigAnimationCheck() {

		JCheckBoxMenuItem cbI = new JCheckBoxMenuItem();
		cbI.setText("Show Gif in ConfigWindow");
		cbI.setSelected(false);
		cbI.addActionListener(e -> {

			cbI.setSelected(this.cfgwindow.setGifEnabled(cbI.getState()));
			this.cfgwindow.updateWindow();

		});

		return cbI;
	}

	protected JMenuItem getPlaylistViewExport() {

		JMenuItem cbI = new JMenuItem();
		cbI.setText("Export Playlistview");
		cbI.addActionListener(e -> {
			this.cfgwindow.getPlayviewgen().save();

		});

		return cbI;
	}

	protected JMenuItem getPlaylistViewImport() {

		JMenuItem cbI = new JMenuItem();
		cbI.setText("Import Playlistview");
		cbI.addActionListener(e -> {
			this.cfgwindow.getPlayviewgen().load();
		});

		return cbI;
	}

	protected JCheckBoxMenuItem getPlaylistViewCheck() {

		JCheckBoxMenuItem cbI = new JCheckBoxMenuItem();
		cbI.setText("Enable Playlistview");
		cbI.setSelected(false);
		cbI.addActionListener(e -> {

			cbI.setSelected(this.cfgwindow.setPlaylistview(cbI.getState()));

			this.cfgwindow.updateWindow();

		});

		return cbI;
	}

}
