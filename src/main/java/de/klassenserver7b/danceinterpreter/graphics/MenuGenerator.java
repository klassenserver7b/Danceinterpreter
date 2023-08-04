/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		log = LoggerFactory.getLogger(getClass());
		cfgwindow = window;
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

	private JMenu getFileMenu() {

		JMenu filem = new JMenu("File");
		filem.add(getExit());
		return filem;
	}

	private JMenu getEditMenu() {

		JMenu editm = new JMenu("Edit");
		editm.add(getConfigAnimationCheck());

		if (Main.Instance.getAppMode() == AppModes.Playlist) {
			editm.addSeparator();
			editm.add(getPlaylistViewCheck());
		}

		if (Main.Instance.getAppMode() == AppModes.Spotify) {
			editm.addSeparator();
			editm.add(getSpotifyReset());

		}

		return editm;

	}

	private JMenu getSongWindowMenu() {

		JMenu songwindowm = new JMenu("SongWindow");

		songwindowm.add(getPictureCheck());
		songwindowm.add(getRefreshWindow());

		return songwindowm;
	}

	private JMenuItem getSpotifyReset() {

		JMenuItem spi = new JMenuItem("Reset Spotify Config");
		spi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Preferences prefs = Preferences.userRoot().node(new File("").getParent() + "_" + new File("").getName()
						+ "_" + SpotifyInteractions.class.getName());
				try {
					prefs.clear();
				} catch (BackingStoreException e1) {
					log.error(e1.getMessage(), e1);
				}

			}
		});

		return spi;

	}

	private JMenuItem getExit() {
		JMenuItem exitI = new JMenuItem("Exit");
		exitI.setIcon(new ExitIcon());
		exitI.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		exitI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		return exitI;
	}

	private JMenuItem getRefreshWindow() {

		JMenuItem label = new JMenuItem();

		label.setText("Refresh SongWindow");
		label.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Main.Instance.getSongWindowServer().refresh();

			}
		});

		return label;
	}

	private JCheckBoxMenuItem getPictureCheck() {

		JCheckBoxMenuItem pictureI = new JCheckBoxMenuItem();
		pictureI.setText("Show Thumbnails");
		pictureI.setSelected(true);
		pictureI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Main.Instance.getSongWindowServer().setAllowImages(pictureI.getState());
				Main.Instance.getSongWindowServer().refresh();

			}
		});

		return pictureI;
	}

	private JCheckBoxMenuItem getConfigAnimationCheck() {

		JCheckBoxMenuItem cbI = new JCheckBoxMenuItem();
		cbI.setText("Show Gif in ConfigWindow");
		cbI.setSelected(false);
		cbI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cfgwindow.setImgenabled(cbI.getState());

				switch (Main.Instance.getAppMode()) {
				case Playlist -> {

					if (cfgwindow.isPlaylistview()) {
						cfgwindow.updateWindow(cfgwindow.loadPlaylistView());
					} else {
						cfgwindow.updateWindow();
					}

				}
				default -> {
					cfgwindow.updateWindow();
				}
				}

			}
		});

		return cbI;
	}

	private JCheckBoxMenuItem getPlaylistViewCheck() {

		JCheckBoxMenuItem cbI = new JCheckBoxMenuItem();
		cbI.setText("Allow Playlistview");
		cbI.setSelected(false);
		cbI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cfgwindow.setPlaylistview(!cfgwindow.isPlaylistview());

				if (cfgwindow.isPlaylistview()) {
					cfgwindow.updateWindow(cfgwindow.loadPlaylistView());
				} else {
					cfgwindow.updateWindow();
				}

			}
		});

		return cbI;
	}

}
