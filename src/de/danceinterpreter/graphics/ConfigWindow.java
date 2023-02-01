
package de.danceinterpreter.graphics;

import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.AppModes;
import de.danceinterpreter.Main;
import de.danceinterpreter.connections.SpotifyInteractions;
import de.danceinterpreter.graphics.icons.ExitIcon;
import de.danceinterpreter.songprocessing.DanceInterpreter;
import de.danceinterpreter.songprocessing.SongData;
import de.danceinterpreter.songprocessing.dataprovider.PlaylistSongDataProvider;

/**
**/

public class ConfigWindow {

	private JFrame mainframe;
	private JPanel mainpanel;
	private Long time;
	private boolean playlistview;
	private boolean imgenabled;
	private String imgpath;
	private final Logger log;

	/**
	 * 
	 
	
	
	*/
	public ConfigWindow() {

		imgpath = "./pics/tech_dance2.gif";

		time = 0L;
		mainframe = new JFrame();
		mainpanel = new JPanel();
		playlistview = false;
		imgenabled = false;
		log = LoggerFactory.getLogger(this.getClass());

		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		File file = new File("./icon.png");

		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			mainframe.setIconImage(bufferedImage);
		} catch (IOException e) {
			log.error("No Icon Found!");
		}
		mainframe.setTitle("DI - Config");

		mainframe.setBounds(10, 10, 500, 281);

		JMenuBar bar = new JMenuBar();

		JMenu filem = new JMenu("File");
		filem.add(getExit());

		bar.add(filem);

		JMenu editm = new JMenu("Edit");
		editm.add(getPictureCheck());
		editm.add(getConfigAnimationCheck());
		editm.add(getSpotifyReset());

		if (Main.Instance.getAppMode() == AppModes.Playlist) {
			editm.add(getPlaylistViewCheck());
		}

		bar.add(editm);

		if (Main.Instance.getAppMode() == AppModes.Mixxx) {

			JMenu mixxxm = new JMenu("Mixxx");

			mixxxm.add(getMixxxConfig());

			bar.add(mixxxm);
		}

		bar.setVisible(true);

		mainframe.setJMenuBar(bar);

		JLabel img = new JLabel();

		// Image image;

		// image = ImageIO.read(new File(imgpath));
		// ImageIcon icon = new ImageIcon(image.getScaledInstance(mainframe.getWidth(),
		// mainframe.getHeight(), 0));
		img = new JLabel(new ImageIcon(imgpath));

		// img.setIcon(icon);

		mainpanel.add(img);
		mainframe.addComponentListener(new ComponentAdapter() {

			public void componentResized(ComponentEvent componentEvent) {

				if (System.currentTimeMillis() - 1000 <= time) {
					return;
				}

				time = System.currentTimeMillis();

				if (playlistview) {
					updateWindow(loadPlaylistView());
				} else {
					updateWindow();
				}

			}

		});
		mainframe.add(mainpanel);
		mainframe.setVisible(true);

	}

	private void updateWindow() {

		mainpanel.removeAll();
		mainpanel.paintImmediately(0, 0, mainframe.getWidth(), mainframe.getHeight());

		if (!playlistview && imgenabled) {
			JLabel img = new JLabel();

			// Image image;

			// image = ImageIO.read(new File(imgpath));
			// ImageIcon icon = new ImageIcon(image.getScaledInstance(mainframe.getWidth(),
			// mainframe.getHeight(), 0));
			img = new JLabel(new ImageIcon(imgpath));

			// img.setIcon(icon);

			mainpanel.add(img);
		}

		mainpanel.paintComponents(mainframe.getGraphics());
		mainframe.setVisible(true);
	}

	private void updateWindow(List<JLabel> l) {

		mainpanel.removeAll();
		mainpanel.paintImmediately(0, 0, mainframe.getWidth(), mainframe.getHeight());

		if (!playlistview) {
			JLabel img = new JLabel();

			Image image;

			try {
				image = ImageIO.read(new File("./pics/splash_small.jpg"));
				ImageIcon icon = new ImageIcon(image.getScaledInstance(mainframe.getWidth(), mainframe.getHeight(), 0));

				img.setIcon(icon);

			} catch (IOException e) {
				e.printStackTrace();
			}

			mainpanel.add(img);
		} else {
			for (JLabel label : l) {
				mainpanel.add(label);
			}
		}

		mainpanel.paintComponents(mainframe.getGraphics());
		mainframe.setVisible(true);
	}

	public List<JLabel> loadPlaylistView() {

		LinkedHashMap<File, SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();
		List<JLabel> labels = new ArrayList<>();

		if (songs == null || songs.isEmpty()) {
			playlistview = !playlistview;
			return null;
		}

		List<Entry<File, SongData>> listedsongs = songs.entrySet().stream().toList();

		for (int i = 0; i < listedsongs.size(); i++) {

			Entry<File, SongData> song = listedsongs.get(i);

			SongData data = song.getValue();
			JLabel songp = new JLabel();

			songp.setText("<html><body>Title: " + data.getTitle() + "<br>Author: " + data.getAuthor() + "<br>Dance: "
					+ data.getDance() + "</body></html>");
			songp.addMouseListener(new ClickListener(i));
			songp.setSize(200, 200);
			songp.setBorder(BorderFactory.createLineBorder(Color.black, 5, true));
			labels.add(songp);

		}

		return labels;

	}

	public void close() {
		mainframe.removeAll();
		mainframe.setEnabled(false);
		mainframe.setVisible(false);

		mainframe.dispose();

		mainframe = null;
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
		cbI.setText("Show Picture in ConfigWindow");
		cbI.setSelected(false);
		cbI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				imgenabled = cbI.getState();

				switch (Main.Instance.getAppMode()) {
				case Playlist -> {

					if (playlistview) {
						updateWindow(loadPlaylistView());
					} else {
						updateWindow();
					}

				}
				default -> {

					updateWindow();
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

				playlistview = !playlistview;

				if (playlistview) {
					updateWindow(loadPlaylistView());
				} else {
					updateWindow();
				}

			}
		});

		return cbI;
	}

}

class ClickListener implements MouseListener {

	private final int clickid;

	public ClickListener(int id) {
		clickid = id;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		PlaylistSongDataProvider pd = (PlaylistSongDataProvider) Main.Instance.getAppMode().getDataProvider();
		pd.setDirection(0);
		pd.setPosition(clickid + 1);
		pd.provideAsynchronous();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
