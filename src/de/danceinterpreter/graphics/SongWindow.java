package de.danceinterpreter.graphics;

import java.awt.*;
import java.awt.event.FocusEvent.Cause;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.graphics.listener.ArrowKeyListener;
import de.danceinterpreter.graphics.listener.CustomKeyListener;
import de.danceinterpreter.graphics.listener.FullscreenListener;
import de.danceinterpreter.graphics.listener.NumberListener;
import de.danceinterpreter.graphics.listener.RefreshListener;

import java.awt.image.*;
import java.io.File;
import java.io.IOException;

/**
 * 
 * @author Felix
 *
 */
public class SongWindow {
	public final Logger log = LoggerFactory.getLogger("Window");
	private JFrame mainframe;
	private JPanel mainpanel;
	private Rectangle rect;
	private JLabel imglabel;
	private JTextArea text;
	private boolean imageenabled;

	/**
	 * 
	 * @param songname
	 * @param artist
	 * @param dance
	 * @param img
	 */
	public SongWindow(String songname, String artist, String dance, BufferedImage img) {

		mainframe = new JFrame();
		imglabel = new JLabel();
		text = new JTextArea();
		imageenabled = true;

		File file = new File("./icon.png");
		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			mainframe.setIconImage(bufferedImage);
		} catch (IOException e) {
			log.error("No Icon Found!");
		}

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		if (devices.length > 1) {

			Rectangle b = devices[1].getDefaultConfiguration().getBounds();

			mainframe.setBounds(b);
			rect = b;

		} else {
			Rectangle b = devices[0].getDefaultConfiguration().getBounds();

			mainframe.setBounds(b);
			rect = b;
		}

		mainpanel = new JPanel();

		mainframe.add(mainpanel);

		mainpanel.setBackground(Color.BLACK);
		mainpanel.setLayout(new FlowLayout(FlowLayout.CENTER, rect.width, rect.height / 20));
		mainpanel.setBackground(Color.BLACK);
		mainpanel.setBounds(0, 0, rect.width, rect.height);

		mainframe.setTitle("DanceInterpreter");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainpanel.add(imglabel);
		mainpanel.add(text);
		mainpanel.setVisible(true);

		imglabel.setBackground(Color.BLACK);

		text.setBackground(Color.BLACK);
		text.setEditable(false);
		text.setForeground(Color.WHITE);
		text.setFont(new Font("Monospaced", Font.PLAIN, 36));

		CustomKeyListener keylis = new CustomKeyListener();

		keylis.registerKeyListeners(new RefreshListener(), new FullscreenListener(), new ArrowKeyListener(),
				new NumberListener());

		text.addKeyListener(keylis);
		mainpanel.addKeyListener(keylis);
		mainframe.addKeyListener(keylis);

		UpdateWindow(songname, artist, dance, img);
	}

	/**
	 * 
	 * @param songname
	 * @param artist
	 * @param dance
	 * @param img
	 */
	public void UpdateWindow(String songname, String artist, String dance, BufferedImage img) {

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		if (devices.length > 1) {
			rect = devices[1].getDefaultConfiguration().getBounds();
		} else {
			rect = devices[0].getDefaultConfiguration().getBounds();
		}

		log.info("width: " + rect.width + " height: " + rect.height);

		Image scaledimg = scaleImage(img, rect);
		ImageIcon imageIcon = new ImageIcon(scaledimg);

		mainpanel.removeAll();
		mainpanel.paintImmediately(0, 0, rect.width, rect.height);

		imglabel.setIcon(imageIcon);
		imglabel.setBounds((rect.width / 2) - (imageIcon.getIconWidth() / 2), (rect.height / 10),
				imageIcon.getIconWidth() + rect.width / 3, imageIcon.getIconHeight() + 64);

		System.out.println("IMG: " + imglabel.getBounds());

		text.setText("\nSongname: " + songname + "\n\nArtist: " + artist + "\n\nTanz: " + dance);

		mainpanel.setBounds(0, 0, mainframe.getWidth(), mainframe.getHeight());

		if (imageenabled) {

			mainpanel.setLayout(new FlowLayout(FlowLayout.CENTER, rect.width, rect.height / 20));
			mainpanel.add(imglabel);

		} else {

			mainpanel.setLayout(null);

			System.out.println(text.getBounds());

			int x = (mainpanel.getWidth() / 2) - text.getWidth() / 2;
			int y = (mainpanel.getHeight() / 2) - text.getHeight() / 2;

			text.setBounds(x, y, text.getWidth(), text.getHeight());

		}

		System.out.println("TEXT: " + text.getBounds());

		mainpanel.add(text);

		mainpanel.paintComponents(mainpanel.getGraphics());

		mainframe.setVisible(true);
		mainframe.requestFocus(Cause.ACTIVATION);
	}

	public Image scaleImage(BufferedImage img, Rectangle rect) {

		if (img.getHeight() >= rect.height * 0.50 || img.getWidth() >= rect.width * 0.75) {
			double heightscale = (rect.getHeight() * 0.50) / img.getHeight();
			double widthscale = (rect.getWidth() * 0.75) / img.getWidth();

			double scale;
			if (heightscale <= widthscale) {
				scale = heightscale;
			} else {
				scale = widthscale;
			}

			return img.getScaledInstance((int) (img.getWidth() * scale), (int) (img.getHeight() * scale), 0);
		}

		return img.getScaledInstance(-1, -1, 0);

	}

	public JFrame getMainFrame() {
		return this.mainframe;
	}

	public JPanel getMainPanel() {
		return this.mainpanel;
	}

	public boolean isImageenabled() {
		return imageenabled;
	}

	public void setImageenabled(boolean imageenabled) {
		this.imageenabled = imageenabled;
	}

}
