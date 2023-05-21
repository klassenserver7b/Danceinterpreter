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
public class SongWindow {
	public final Logger log = LoggerFactory.getLogger("Window");
	private JFrame mainframe;
	private JPanel mainpanel;
	private JLabel imglabel;
	private JTextArea textf;
	private int autofontsizestate;
	private boolean imageenabled;
	private int fontsize;

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
		textf = new JTextArea();
		imageenabled = true;
		autofontsizestate = 1;

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
		Rectangle rect = b;

		mainpanel = new JPanel();

		mainframe.add(mainpanel);

		mainpanel.setBackground(Color.BLACK);
		mainpanel.setLayout(new FlowLayout(FlowLayout.CENTER, rect.width, rect.height / 20));
		mainpanel.setBackground(Color.BLACK);
		mainpanel.setBounds(0, 0, rect.width, rect.height);

		mainframe.setTitle("DanceInterpreter");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainpanel.add(imglabel);
		mainpanel.add(textf);
		mainpanel.setVisible(true);

		imglabel.setBackground(Color.BLACK);

		textf.setEditable(false);
		textf.setSelectedTextColor(Color.WHITE);
		textf.setSelectionColor(Color.BLACK);
		textf.setBackground(Color.BLACK);
		textf.setForeground(Color.WHITE);
		// text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 36));

		CustomKeyListener keylis = new CustomKeyListener();

		keylis.registerKeyListeners(new RefreshListener(), new FullscreenListener(), new ArrowSpaceKeyListener(),
				new NumberListener());

		textf.addKeyListener(keylis);
		mainpanel.addKeyListener(keylis);
		mainframe.addKeyListener(keylis);

		updateWindow(songname, artist, dance, img);
	}

	/**
	 * 
	 * @param songname
	 * @param artist
	 * @param dance
	 * @param image
	 */
	public void updateWindow(String songname, String artist, String dance, BufferedImage image) {
		String datatext = "\nSongname: " + songname + "\n\nArtist: " + artist + "\n\nTanz: " + dance;
		updateWindow(datatext, image);
	}

	/**
	 * 
	 */
	public void refresh() {
		updateWindow(textf.getText(), iconToImage(imglabel.getIcon()));
	}

	/**
	 * 
	 * @param songname
	 * @param artist
	 * @param dance
	 * @param img
	 */
	public void updateWindow(String datatext, BufferedImage img) {

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Rectangle rect = devices[0].getDefaultConfiguration().getBounds();

		mainpanel.removeAll();
		mainpanel.paintImmediately(0, 0, rect.width, rect.height);
		mainpanel.setBounds(0, 0, mainframe.getWidth(), mainframe.getHeight());

		textf.setText(datatext);

		if (autofontsizestate > 0) {
			setFont(textf);
		} else {
			textf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontsize));
		}

		if (imageenabled) {
			updateImage(img, rect);
		} else {
			applyOnlyTextLayout();
		}

		mainpanel.add(textf);
		mainpanel.paintComponents(mainpanel.getGraphics());

		mainframe.setVisible(true);
		mainframe.requestFocus(Cause.ACTIVATION);
	}

	/**
	 * 
	 * @param img
	 * @param rect
	 */
	protected void updateImage(BufferedImage img, Rectangle rect) {
		Image scaledimg = scaleImage(img, rect);
		ImageIcon imageIcon = new ImageIcon(scaledimg);

		imglabel.setIcon(imageIcon);
		imglabel.setBounds((rect.width / 2) - (imageIcon.getIconWidth() / 2), (rect.height / 10),
				imageIcon.getIconWidth() + rect.width / 3, imageIcon.getIconHeight() + 64);

		mainpanel.setLayout(new FlowLayout(FlowLayout.CENTER, rect.width, rect.height / 20));
		mainpanel.add(imglabel);
	}

	/**
	 * 
	 * @param img
	 * @param rect
	 * @return
	 */
	protected Image scaleImage(BufferedImage img, Rectangle rect) {

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
	 */
	protected void applyOnlyTextLayout() {
		mainpanel.setLayout(null);

		int estwidth = calcEstimatedWidth(textf, textf.getFont(), textf.getText(), textf.getGraphics());
		int estheight = calcEstimatedHeight(textf, textf.getFont(), textf.getText(), textf.getGraphics());

		int x = (mainpanel.getWidth() / 2 - (int) (estwidth / 2));
		int y = (mainpanel.getHeight() / 2) - (int) (estheight / 2);

		textf.setBounds(x, y, (int) estwidth, estheight);
	}

	/**
	 * 
	 * @param comp
	 * @param font
	 * @param text
	 * @param g
	 * @return
	 */
	protected int calcEstimatedWidth(JComponent comp, Font font, String text, Graphics g) {

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

	/**
	 * 
	 * @param comp
	 * @param font
	 * @param text
	 * @param g
	 * @return
	 */
	protected int calcEstimatedHeight(JComponent comp, Font font, String text, Graphics g) {

		double oneline = comp.getFontMetrics(font).getStringBounds(text, g).getBounds().getHeight();

		return (int) oneline * text.split("\n").length;
	}

	/**
	 * 
	 * @param area
	 */
	protected void setFont(JTextArea area) {

		int height = mainframe.getHeight();
		int fontsize;

		if (imageenabled) {

			fontsize = (int) height / 20;

		} else {

			if (autofontsizestate <= 0) {
				return;
			}

			if (autofontsizestate == 1) {
				fontsize = (int) height / 20;
			} else {
				fontsize = (int) height / 10;
			}
		}
		area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontsize));
		this.fontsize = fontsize;

	}

	/**
	 * 
	 * @param icon
	 * @return
	 */
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

	/**
	 * 
	 * @return
	 */
	public JFrame getMainFrame() {
		return this.mainframe;
	}

	/**
	 * 
	 * @return
	 */
	public JPanel getMainPanel() {
		return this.mainpanel;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isImageenabled() {
		return imageenabled;
	}

	/**
	 * 
	 * @param imageenabled
	 */
	public void setImageenabled(boolean imageenabled) {
		this.imageenabled = imageenabled;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAutofontsize() {
		return autofontsizestate > 0;
	}

	/**
	 * 
	 * @return
	 */
	public int getAutofontsizeState() {
		return autofontsizestate;
	}

	/**
	 * 
	 * @param autofontsize
	 */
	public void setAutofontsizeState(int autofontsizestate) {
		this.autofontsizestate = autofontsizestate;
	}

	/**
	 * 
	 * @param fontsize
	 */
	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}

}
