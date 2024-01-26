
package de.klassenserver7b.danceinterpreter.graphics;

import java.awt.Image;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.graphics.listener.CustomKeyListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.FileDropListener;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
**/

public class ConfigWindow {

	private JFrame mainFrame;
	private JPanel mainPanel;
	private Long time;
	private boolean gifEnabled;
	private String gifPath;
	private final Logger log;
	private final DropTarget dropTarget;
	private final PlaylistViewGenerator playlistViewGen;

	/**
	 * 
	 
	
	
	*/
	public ConfigWindow() {

		this.playlistViewGen = new PlaylistViewGenerator();
		playlistViewGen.setPlaylistViewEnabled(false);

		this.gifPath = "./pics/tech_dance2.gif";
		this.log = LoggerFactory.getLogger(this.getClass());

		this.time = 0L;
		this.mainFrame = new JFrame();
		this.mainPanel = new JPanel();
		this.gifEnabled = false;

		this.dropTarget = new DropTarget(this.mainFrame, new FileDropListener());

		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		File file = new File("./icon.png");

		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			this.mainFrame.setIconImage(bufferedImage);
		} catch (IOException e) {
			this.log.error("No Icon Found!");
		}
		this.mainFrame.setTitle("DI - Config");

		this.mainFrame.setBounds(10, 10, 1280, 720);

		MenuGenerator mgen = new MenuGenerator(this);

		this.mainFrame.setJMenuBar(mgen.getMenuBar());

		JLabel img = new JLabel();

		// Image image;

		// image = ImageIO.read(new File(gifPath));
		// ImageIcon icon = new ImageIcon(image.getScaledInstance(mainFrame.getWidth(),
		// mainFrame.getHeight(), 0));
		img = new JLabel(new ImageIcon(this.gifPath));

		// img.setIcon(icon);

		this.mainPanel.add(img);
		this.mainFrame.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent componentEvent) {

				if (System.currentTimeMillis() - 1000 <= time) {
					return;
				}

				time = System.currentTimeMillis();

				updateWindow();

			}

		});
		this.mainFrame.add(this.mainPanel);
		this.mainFrame.setVisible(true);

	}

	public void updateWindow() {

		this.mainPanel.removeAll();
		this.mainPanel.paintImmediately(0, 0, this.mainFrame.getWidth(), this.mainFrame.getHeight());

		if (this.gifEnabled) {

			JLabel img = new JLabel();
			img = new JLabel(new ImageIcon(this.gifPath));

			this.mainPanel.add(img);

		} else if (this.playlistViewGen.isPlaylistViewEnabled()) {

			for (JLabel label : this.playlistViewGen.loadPlaylistView()) {
				this.mainPanel.add(label);
			}

		} else {

			JLabel img = new JLabel();

			Image image;

			try {
				image = ImageIO.read(new File("./pics/splash_small.jpg"));
				ImageIcon icon = new ImageIcon(
						image.getScaledInstance(this.mainFrame.getWidth(), this.mainFrame.getHeight(), 0));

				img.setIcon(icon);

			} catch (IOException e) {
				e.printStackTrace();
			}

			this.mainPanel.add(img);

		}

		this.mainPanel.paintComponents(this.mainFrame.getGraphics());
		this.mainFrame.repaint();
		this.mainFrame.setVisible(true);

	}

	public void initKeyListeners(CustomKeyListener keylis) {
		this.mainFrame.addKeyListener(keylis);
	}

	public void close() {
		this.mainFrame.removeAll();
		this.mainFrame.setEnabled(false);
		this.mainFrame.setVisible(false);

		this.mainFrame.dispose();

		this.mainFrame = null;
	}

	public boolean isGifEnabled() {
		return this.gifEnabled;
	}

	public boolean setPlaylistview(boolean playlistViewEnabled) {

		if (isGifEnabled()) {
			return false;
		}

		LinkedList<SongData> songs = Main.Instance.getDanceInterpreter().getPlaylistSongs();

		if (songs == null || songs.isEmpty()) {
			this.playlistViewGen.setPlaylistViewEnabled(false);
			return false;
		}

		this.playlistViewGen.setPlaylistViewEnabled(playlistViewEnabled);
		return playlistViewEnabled;
	}

	public boolean setGifEnabled(boolean gifEnabled) {

		if (this.playlistViewGen.isPlaylistViewEnabled()) {
			return false;
		}

		this.gifEnabled = gifEnabled;

		return gifEnabled;
	}

	public JFrame getMainframe() {
		return this.mainFrame;
	}

	public JPanel getMainpanel() {
		return this.mainPanel;
	}

	public DropTarget getDropTarget() {
		return this.dropTarget;
	}

	/**
	 * @return the playlistViewGen
	 */
	public PlaylistViewGenerator getPlayviewgen() {
		return this.playlistViewGen;
	}
}
