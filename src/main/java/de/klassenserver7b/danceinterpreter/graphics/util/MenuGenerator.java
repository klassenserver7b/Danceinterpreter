/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
import de.klassenserver7b.danceinterpreter.graphics.ConfigWindow;
import de.klassenserver7b.danceinterpreter.graphics.util.icons.ExitIcon;

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

		bar.setOpaque(true);

		bar.setForeground(Main.Instance.getTextColor());
		bar.setBackground(Main.Instance.getBackgroundColor());

		bar.add(getFileMenu());

		bar.add(getEditMenu());

		bar.add(getSongWindowMenu());

		bar.add(getHelpMenu());

		bar.setVisible(true);
		return bar;
	}

	protected JMenu getFileMenu() {

		JMenu filem = new JMenu("File");
		filem.setBackground(Main.Instance.getBackgroundColor());

		filem.add(getExit());

		return filem;
	}

	protected JMenu getEditMenu() {

		JMenu editm = new JMenu("Edit");
		editm.setBackground(Main.Instance.getBackgroundColor());

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
		songwindowm.setBackground(Main.Instance.getBackgroundColor());

		songwindowm.add(getPictureCheck());
		songwindowm.add(getNextCheck());
		songwindowm.add(getRefreshWindow());

		return songwindowm;
	}

	protected JMenu getHelpMenu() {

		JMenu helpm = new JMenu("Help");
		helpm.setBackground(Main.Instance.getBackgroundColor());

		helpm.add(getHelp());

		return helpm;
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

		JCheckBoxMenuItem cacI = new JCheckBoxMenuItem();
		cacI.setText("Show Gif in ConfigWindow");
		cacI.setSelected(false);
		cacI.addActionListener(e -> {

			cacI.setSelected(this.cfgwindow.setGifEnabled(cacI.getState()));
			this.cfgwindow.updateWindow();

		});

		return cacI;
	}

	protected JMenuItem getPlaylistViewExport() {

		JMenuItem pveI = new JMenuItem();
		pveI.setText("Export Playlistview");
		pveI.addActionListener(e -> {
			this.cfgwindow.getPlayviewgen().save();

		});

		return pveI;
	}

	protected JMenuItem getPlaylistViewImport() {

		JMenuItem pviI = new JMenuItem();
		pviI.setText("Import Playlistview");
		pviI.addActionListener(e -> {
			this.cfgwindow.getPlayviewgen().load();
		});

		return pviI;
	}

	protected JCheckBoxMenuItem getPlaylistViewCheck() {

		JCheckBoxMenuItem cvI = new JCheckBoxMenuItem();
		cvI.setText("Enable Playlistview");
		cvI.setSelected(false);
		cvI.addActionListener(e -> {

			cvI.setSelected(this.cfgwindow.setPlaylistview(cvI.getState()));

			this.cfgwindow.updateWindow();

		});

		return cvI;
	}

	protected JMenuItem getHelp() {
		JMenuItem cbI = new JMenuItem();
		cbI.setText("Get Help");
		cbI.addActionListener(e -> {

			try {

				File temp = File.createTempFile("Dihelp-" + System.currentTimeMillis(), ".html");
				temp.deleteOnExit();

				Files.write(temp.toPath(), getClass().getResourceAsStream("/help.html").readAllBytes(),
						StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

				Desktop.getDesktop().open(temp);
			} catch (IOException e1) {
				log.error(e1.getMessage(), e);
			}

		});

		return cbI;
	}

}
