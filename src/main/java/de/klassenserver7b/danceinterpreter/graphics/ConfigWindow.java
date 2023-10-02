
package de.klassenserver7b.danceinterpreter.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.graphics.listener.FileDropListener;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;
import de.klassenserver7b.danceinterpreter.songprocessing.dataprovider.PlaylistSongDataProvider;

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
	private final DropTarget dropTarget;

	/**
	 * 
	 
	
	
	*/
	public ConfigWindow() {

		imgpath = "./pics/tech_dance2.gif";
		log = LoggerFactory.getLogger(this.getClass());

		time = 0L;
		mainframe = new JFrame();
		mainpanel = new JPanel();
		playlistview = false;
		imgenabled = false;

		dropTarget = new DropTarget(mainframe, new FileDropListener());

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

		MenuGenerator mgen = new MenuGenerator(this);

		mainframe.setJMenuBar(mgen.getMenuBar());

		JLabel img = new JLabel();

		// Image image;

		// image = ImageIO.read(new File(imgpath));
		// ImageIcon icon = new ImageIcon(image.getScaledInstance(mainframe.getWidth(),
		// mainframe.getHeight(), 0));
		img = new JLabel(new ImageIcon(imgpath));

		// img.setIcon(icon);

		mainpanel.add(img);
		mainframe.addComponentListener(new ComponentAdapter() {

			@Override
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

	public void updateWindow() {

		mainpanel.removeAll();
		mainpanel.paintImmediately(0, 0, mainframe.getWidth(), mainframe.getHeight());

		if (!playlistview && imgenabled) {
			JLabel img = new JLabel();
			img = new JLabel(new ImageIcon(imgpath));

			mainpanel.add(img);
		}

		mainpanel.paintComponents(mainframe.getGraphics());
		mainframe.setVisible(true);
	}

	public void updateWindow(List<JLabel> l) {

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

		LinkedList<SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();
		List<JLabel> labels = new ArrayList<>();

		if (songs == null || songs.isEmpty()) {
			playlistview = !playlistview;
			return null;
		}

		for (int i = 0; i < songs.size(); i++) {

			SongData data = songs.get(i);
			JLabel songp = new JLabel();

			if (!(data.getTitle().isBlank() && data.getAuthor().isBlank() && data.getDance().isBlank())) {

				songp.setText("<html><body>Title: " + data.getTitle() + "<br>Author: " + data.getAuthor()
						+ "<br>Dance: " + data.getDance() + "</body></html>");

			} else {
				songp.setFont(new Font("Arial", Font.BOLD, 20));
				songp.setText("BLANK");
			}

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

	public boolean isPlaylistview() {
		return playlistview;
	}

	public boolean isImgenabled() {
		return imgenabled;
	}

	public void setPlaylistview(boolean playlistview) {
		this.playlistview = playlistview;
	}

	public void setImgenabled(boolean imgenabled) {
		this.imgenabled = imgenabled;
	}

	public JFrame getMainframe() {
		return mainframe;
	}

	public JPanel getMainpanel() {
		return mainpanel;
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
			pd.provideAsync();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// Nothing to do here

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// Nothing to do here

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// Nothing to do here

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// Nothing to do here

		}
	}

	public DropTarget getDropTarget() {
		return dropTarget;
	}
}
