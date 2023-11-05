
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

		this.imgpath = "./pics/tech_dance2.gif";
		this.log = LoggerFactory.getLogger(this.getClass());

		this.time = 0L;
		this.mainframe = new JFrame();
		this.mainpanel = new JPanel();
		this.playlistview = false;
		this.imgenabled = false;

		this.dropTarget = new DropTarget(this.mainframe, new FileDropListener());

		this.mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		File file = new File("./icon.png");

		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			this.mainframe.setIconImage(bufferedImage);
		} catch (IOException e) {
			this.log.error("No Icon Found!");
		}
		this.mainframe.setTitle("DI - Config");

		this.mainframe.setBounds(10, 10, 500, 281);

		MenuGenerator mgen = new MenuGenerator(this);

		this.mainframe.setJMenuBar(mgen.getMenuBar());

		JLabel img = new JLabel();

		// Image image;

		// image = ImageIO.read(new File(imgpath));
		// ImageIcon icon = new ImageIcon(image.getScaledInstance(mainframe.getWidth(),
		// mainframe.getHeight(), 0));
		img = new JLabel(new ImageIcon(this.imgpath));

		// img.setIcon(icon);

		this.mainpanel.add(img);
		this.mainframe.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent componentEvent) {

				if (System.currentTimeMillis() - 1000 <= ConfigWindow.this.time) {
					return;
				}

				ConfigWindow.this.time = System.currentTimeMillis();

				if (ConfigWindow.this.playlistview) {
					updateWindow(loadPlaylistView());
				} else {
					updateWindow();
				}

			}

		});
		this.mainframe.add(this.mainpanel);
		this.mainframe.setVisible(true);

	}

	public void updateWindow() {

		this.mainpanel.removeAll();
		this.mainpanel.paintImmediately(0, 0, this.mainframe.getWidth(), this.mainframe.getHeight());

		if (!this.playlistview && this.imgenabled) {
			JLabel img = new JLabel();
			img = new JLabel(new ImageIcon(this.imgpath));

			this.mainpanel.add(img);
		}

		this.mainpanel.paintComponents(this.mainframe.getGraphics());
		this.mainframe.setVisible(true);
	}

	public void updateWindow(List<JLabel> l) {

		this.mainpanel.removeAll();
		//mainpanel.paintImmediately(0, 0, mainframe.getWidth(), mainframe.getHeight());

		if (!this.playlistview) {
			JLabel img = new JLabel();

			Image image;

			try {
				image = ImageIO.read(new File("./pics/splash_small.jpg"));
				ImageIcon icon = new ImageIcon(image.getScaledInstance(this.mainframe.getWidth(), this.mainframe.getHeight(), 0));

				img.setIcon(icon);

			} catch (IOException e) {
				e.printStackTrace();
			}

			this.mainpanel.add(img);
		} else {
			for (JLabel label : l) {
				this.mainpanel.add(label);
			}
		}

		this.mainpanel.paintComponents(this.mainframe.getGraphics());
		this.mainframe.repaint();
		this.mainframe.setVisible(true);
	}

	public List<JLabel> loadPlaylistView() {

		LinkedList<SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();
		List<JLabel> labels = new ArrayList<>();

		if (songs == null || songs.isEmpty()) {
			this.playlistview = !this.playlistview;
			return null;
		}

		for (int i = 0; i < songs.size(); i++) {

			SongData data = songs.get(i);
			JLabel songp = new JLabel();

			songp.addMouseListener(new ClickListener(i));
			songp.setSize(200, 200);
			songp.setBorder(BorderFactory.createLineBorder(Color.black, 5, true));
			labels.add(songp);

			if (data.getDance().isBlank()) {
				songp.setFont(new Font("Arial", Font.BOLD, 20));
				songp.setText("BLANK");
				continue;
			}

			if (data.getAuthor().isBlank() || data.getTitle().isBlank()) {

				songp.setFont(new Font("Arial", Font.BOLD, 20));
				songp.setText(data.getDance());
				continue;

			}

			songp.setText("<html><body>Title: " + data.getTitle() + "<br>Author: " + data.getAuthor() + "<br>Dance: "
					+ data.getDance() + "</body></html>");

		}

		return labels;

	}

	public void close() {
		this.mainframe.removeAll();
		this.mainframe.setEnabled(false);
		this.mainframe.setVisible(false);

		this.mainframe.dispose();

		this.mainframe = null;
	}

	public boolean isPlaylistview() {
		return this.playlistview;
	}

	public boolean isImgenabled() {
		return this.imgenabled;
	}

	public void setPlaylistview(boolean playlistview) {
		this.playlistview = playlistview;
	}

	public void setImgenabled(boolean imgenabled) {
		this.imgenabled = imgenabled;
	}

	public JFrame getMainframe() {
		return this.mainframe;
	}

	public JPanel getMainpanel() {
		return this.mainpanel;
	}

	class ClickListener implements MouseListener {

		private final int clickid;

		public ClickListener(int id) {
			this.clickid = id;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			PlaylistSongDataProvider pd = (PlaylistSongDataProvider) Main.Instance.getAppMode().getDataProvider();
			pd.setDirection(0);
			pd.setPosition(this.clickid + 1);
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
		return this.dropTarget;
	}
}
