/**
 * 
 */
package de.danceinterpreter.graphics;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.AppModes;
import de.danceinterpreter.Main;
import de.danceinterpreter.connections.SpotifyInteractions;
import de.danceinterpreter.graphics.icons.ExitIcon;
import de.danceinterpreter.graphics.listener.EnterListener;
import de.danceinterpreter.songprocessing.DanceInterpreter;
import de.danceinterpreter.songprocessing.dataprovider.PlaylistSongDataProvider;

/**
 * @author Felix
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

	public JMenuBar getMenuBar() {
		JMenuBar bar = new JMenuBar();

		bar.add(getFileMenu());

		bar.add(getEditMenu());

		bar.add(getSongWindowMenu());

		if (Main.Instance.getAppMode() == AppModes.Mixxx) {
			JMenu mixxxm = new JMenu("Mixxx");

			mixxxm.add(getMixxxConfig());

			bar.add(mixxxm);
		}

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
		songwindowm.add(getFontSizeOpt());

		return songwindowm;
	}

	private JMenuItem getFontSizeOpt() {

		JMenuItem fontsizeopt = new JMenuItem("Change Fontsize");

		fontsizeopt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JDialog dial = getFontSizeDialogue();
				dial.setVisible(true);

			}
		});

		return fontsizeopt;

	}

	private JDialog getFontSizeDialogue() {

		JDialog dialogue = new JDialog(cfgwindow.getMainframe(), "Fontsize");

		Rectangle d = cfgwindow.getMainframe().getBounds();
		dialogue.setSize((int) (d.getWidth() / 1.5), (int) (d.getHeight() / 3));
		dialogue.setLocation(d.x + 20, d.y + 20);
		dialogue.setLayout(null);

		JPanel popupmenu = new JPanel();
		popupmenu.setLayout(new BoxLayout(popupmenu, BoxLayout.Y_AXIS));

		popupmenu.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		popupmenu.setAlignmentY(JPanel.CENTER_ALIGNMENT);

		dialogue.setContentPane(popupmenu);

		JLabel label = new JLabel("Please insert the new FontSize:");
		label.setSize(popupmenu.getWidth(), label.getFontMetrics(label.getFont()).getHeight());
		label.setLocation(popupmenu.getX(), popupmenu.getY());
		label.setBorder(null);
		popupmenu.add(label);

		JTextField txt = new JTextField();
		txt.addFocusListener(new HintFocusListener("Insert Fontsize"));
		txt.setText("Insert Fontsize");
		txt.setEditable(true);
		txt.setVisible(true);
		txt.setToolTipText("Fontsize in px, -1 -> auto");
		txt.setSize(popupmenu.getWidth(), 70);

		JButton change = new JButton("Apply");
		change.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String text = txt.getText();

				if (text.matches("^-?[0-9]+")) {

					Integer fontsize = Integer.parseInt(txt.getText());
					log.debug("fontsize: " + fontsize);

					SongWindow sw = Main.Instance.getDanceInterpreter().getWindow();

					if (fontsize == -1) {
						sw.setAutofontsize(true);
					} else {
						sw.setAutofontsize(false);
						sw.setFontsize(fontsize);
					}

					dialogue.setVisible(false);
					sw.refresh();
					dialogue.dispose();
				} else {
					label.setText("Please a valid FontSize");
					label.setForeground(Color.decode("ff0000"));
				}

			}
		});

		txt.addKeyListener(new EnterListener(change, txt));

		popupmenu.add(txt);
		popupmenu.add(change);
		popupmenu.setBounds(400, 400, 500, 300);

		return dialogue;
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

	private JMenuItem getMixxxConfig() {
		JMenuItem configI = new JMenuItem("Open Config");
		configI.setMargin(new Insets(2, -20, 2, 2));

		return configI;
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

	private JCheckBoxMenuItem getPictureCheck() {

		JCheckBoxMenuItem pictureI = new JCheckBoxMenuItem();
		pictureI.setText("Show Thumbnails");
		pictureI.setSelected(true);
		pictureI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				DanceInterpreter di = Main.Instance.getDanceInterpreter();

				if (di == null) {
					return;
				}

				if (di.getWindow() == null) {
					return;
				}

				di.getWindow().setImageenabled(pictureI.getState());

				switch (Main.Instance.getAppMode()) {
				case Playlist -> {

					PlaylistSongDataProvider dp = (PlaylistSongDataProvider) Main.Instance.getAppMode()
							.getDataProvider();
					dp.setDirection(0);
					dp.provideAsynchronous();

				}
				default -> {

					Main.Instance.getAppMode().getDataProvider().provideAsynchronous();
				}
				}

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
