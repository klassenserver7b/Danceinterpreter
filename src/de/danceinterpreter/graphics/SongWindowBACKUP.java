package de.danceinterpreter.graphics;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.FocusEvent.Cause;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.graphics.listener.ArrowSpaceKeyListener;
import de.danceinterpreter.graphics.listener.CustomKeyListener;
import de.danceinterpreter.graphics.listener.FullscreenListener;
import de.danceinterpreter.graphics.listener.NumberListener;
import de.danceinterpreter.graphics.listener.RefreshListener;

/**
 * 
 * @author Felix
 *
 */
public class SongWindowBACKUP {
	public final Logger log = LoggerFactory.getLogger("Window");
	private JFrame mainframe;
	private JPanel mainpanel;
	private Rectangle rect;
	private JLabel imglabel;
	private JTextArea text;
	private boolean imageenabled;
	private boolean autofontsize;
	private int fontsize;

	/**
	 * 
	 * @param songname
	 * @param artist
	 * @param dance
	 * @param img
	 */
	public SongWindowBACKUP(String songname, String artist, String dance, BufferedImage img) {

		mainframe = new JFrame();
		imglabel = new JLabel();
		text = new JTextArea();
		imageenabled = true;
		autofontsize = true;

		File file = new File("./icon.png");
		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			mainframe.setIconImage(bufferedImage);
		} catch (IOException e) {
			log.error("No Icon Found!");
		}

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		Rectangle b = devices[0].getDefaultConfiguration().getBounds();

		mainframe.setBounds(b);
		rect = b;

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

		text.setEditable(false);
		text.setSelectedTextColor(Color.WHITE);
		text.setSelectionColor(Color.BLACK);
		text.setBackground(Color.BLACK);
		text.setForeground(Color.WHITE);
		// text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 36));

		CustomKeyListener keylis = new CustomKeyListener();

		keylis.registerKeyListeners(new RefreshListener(), new FullscreenListener(), new ArrowSpaceKeyListener(),
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

		rect = devices[0].getDefaultConfiguration().getBounds();

		Image scaledimg = scaleImage(img, rect);
		ImageIcon imageIcon = new ImageIcon(scaledimg);

		mainpanel.removeAll();
		mainpanel.paintImmediately(0, 0, rect.width, rect.height);

		imglabel.setIcon(imageIcon);
		imglabel.setBounds((rect.width / 2) - (imageIcon.getIconWidth() / 2), (rect.height / 10),
				imageIcon.getIconWidth() + rect.width / 3, imageIcon.getIconHeight() + 64);

		log.debug("IMG: " + imglabel.getBounds());

		text.setText("\nSongname: " + songname + "\n\nArtist: " + artist + "\n\nTanz: " + dance);

		mainpanel.setBounds(0, 0, mainframe.getWidth(), mainframe.getHeight());

		if (autofontsize) {
			setFont(text);
		} else {
			text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontsize));
		}

		if (imageenabled) {

			mainpanel.setLayout(new FlowLayout(FlowLayout.CENTER, rect.width, rect.height / 20));
			mainpanel.add(imglabel);

		} else {

			mainpanel.setLayout(null);

			int estwidth = calcEstimatedWidth(text, text.getFont(), text.getText(), text.getGraphics());
			int estheight = calcEstimatedHeight(text, text.getFont(), text.getText(), text.getGraphics());

			int x = (mainpanel.getWidth() / 2 - (int) (estwidth / 2));
			int y = (mainpanel.getHeight() / 2) - (int) (estheight / 2);

			text.setBounds(x, y, (int) estwidth, estheight);

		}

		log.debug("TEXT: " + text.getBounds());

		mainpanel.add(text);

		mainpanel.paintComponents(mainpanel.getGraphics());

		mainframe.setVisible(true);
		mainframe.requestFocus(Cause.ACTIVATION);
	}

	public int calcEstimatedWidth(JComponent comp, Font font, String text, Graphics g) {

		int estwidth = 0;

		for (String s : text.split("\n")) {

			if (s.isBlank()) {
				continue;
			}

			double testwidth = comp.getFontMetrics(font).getStringBounds(s, g).getBounds().getWidth();

			if (testwidth > estwidth) {
				estwidth = (int) testwidth;
			}
		}

		return estwidth;
	}

	public int calcEstimatedHeight(JComponent comp, Font font, String text, Graphics g) {

		double oneline = comp.getFontMetrics(font).getStringBounds(text, g).getBounds().getHeight();

		return (int) oneline * text.split("\n").length;
	}

	public void refresh() {

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		rect = devices[0].getDefaultConfiguration().getBounds();

		Image scaledimg = scaleImage(iconToImage(imglabel.getIcon()), rect);
		ImageIcon imageIcon = new ImageIcon(scaledimg);

		mainpanel.removeAll();
		mainpanel.paintImmediately(0, 0, rect.width, rect.height);

		imglabel.setIcon(imageIcon);
		imglabel.setBounds((rect.width / 2) - (imageIcon.getIconWidth() / 2), (rect.height / 10),
				imageIcon.getIconWidth() + rect.width / 3, imageIcon.getIconHeight() + 64);

		log.debug("IMG: " + imglabel.getBounds());

		text.setText(text.getText());

		mainpanel.setBounds(0, 0, mainframe.getWidth(), mainframe.getHeight());

		if (autofontsize) {
			setFont(text);
		} else {
			text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontsize));
		}

		if (imageenabled) {

			mainpanel.setLayout(new FlowLayout(FlowLayout.CENTER, rect.width, rect.height / 20));
			mainpanel.add(imglabel);

		} else {

			mainpanel.setLayout(null);

			int estwidth = calcEstimatedWidth(text, text.getFont(), text.getText(), text.getGraphics());
			int estheight = calcEstimatedHeight(text, text.getFont(), text.getText(), text.getGraphics());

			int x = (mainpanel.getWidth() / 2 - (int) (estwidth / 2));
			int y = (mainpanel.getHeight() / 2) - (int) (estheight / 2);

			text.setBounds(x, y, (int) estwidth, estheight);

		}

		log.debug("TEXT: " + text.getBounds());

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

	/**
	 * 
	 * @param area
	 */
	public void setFont(JTextArea area) {

		int height = mainframe.getHeight();
		int fontsize;

		if (imageenabled) {
			fontsize = (int) height / 30;
		} else {
			fontsize = (int) height / 20;
		}
		area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontsize));
		this.fontsize = fontsize;

	}

	public static BufferedImage iconToImage(Icon icon) {

		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage image = gc.createCompatibleImage(w, h);
		Graphics2D g = image.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		return image;

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

	public boolean isAutofontsize() {
		return autofontsize;
	}

	public void setAutofontsize(boolean autofontsize) {
		this.autofontsize = autofontsize;
	}

	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}

}
