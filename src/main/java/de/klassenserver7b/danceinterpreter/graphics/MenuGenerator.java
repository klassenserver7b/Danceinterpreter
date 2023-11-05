/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics;

import de.klassenserver7b.danceinterpreter.AppModes;
import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.connections.SpotifyInteractions;
import de.klassenserver7b.danceinterpreter.graphics.icons.ExitIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

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
		songwindowm.add(getNextCheck());
		songwindowm.add(getRefreshWindow());

		return songwindowm;
	}

	private JMenuItem getSpotifyReset() {

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

	private JMenuItem getExit() {
		JMenuItem exitI = new JMenuItem("Exit");
		exitI.setIcon(new ExitIcon());
		exitI.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		exitI.addActionListener(e -> System.exit(0));
		return exitI;
	}

	private JMenuItem getRefreshWindow() {

		JMenuItem label = new JMenuItem();

		label.setText("Refresh SongWindow");
		label.addActionListener(e -> Main.Instance.getSongWindowServer().refresh());

		return label;
	}

	private JCheckBoxMenuItem getPictureCheck() {

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

	private JCheckBoxMenuItem getNextCheck() {

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

	private JCheckBoxMenuItem getConfigAnimationCheck() {

		JCheckBoxMenuItem cbI = new JCheckBoxMenuItem();
		cbI.setText("Show Gif in ConfigWindow");
		cbI.setSelected(false);
		cbI.addActionListener(e -> {

            this.cfgwindow.setImgenabled(cbI.getState());

            switch (Main.Instance.getAppMode()) {
            case Playlist -> {

                if (this.cfgwindow.isPlaylistview()) {
                    this.cfgwindow.updateWindow(this.cfgwindow.loadPlaylistView());
                } else {
                    this.cfgwindow.updateWindow();
                }

            }
            default -> {
                this.cfgwindow.updateWindow();
            }
            }

        });

		return cbI;
	}

	private JCheckBoxMenuItem getPlaylistViewCheck() {

		JCheckBoxMenuItem cbI = new JCheckBoxMenuItem();
		cbI.setText("Enable Playlistview");
		cbI.setSelected(false);
		cbI.addActionListener(e -> {

            this.cfgwindow.setPlaylistview(!this.cfgwindow.isPlaylistview());

            if (this.cfgwindow.isPlaylistview()) {
                this.cfgwindow.updateWindow(this.cfgwindow.loadPlaylistView());
            } else {
                this.cfgwindow.updateWindow();
            }

        });

		return cbI;
	}

}
