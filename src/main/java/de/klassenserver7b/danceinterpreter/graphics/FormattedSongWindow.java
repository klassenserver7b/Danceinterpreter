/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
 * @author K7
 *
 */
public abstract class FormattedSongWindow implements TypedWindow {

	protected JFrame frame;
	protected final SongWindowSpecs windowSpecs;
	protected String danceName;
	protected String songName;
	protected String artistName;
	protected BufferedImage albumImage;
	protected final boolean hasAlbumImage;
	protected boolean hasNextData;
	protected SongData nextData;

	/**
	 * 
	 * @param songWindowSpecs
	 */
	public FormattedSongWindow(SongWindowSpecs songWindowSpecs) {
		this.windowSpecs = songWindowSpecs;
		this.hasAlbumImage = songWindowSpecs.containsImage();
		this.danceName = "";
		this.songName = "";
		this.artistName = "";
		this.albumImage = null;
		this.hasNextData = false;
		this.nextData = null;
	}

	@Override
	public void onInit(JFrame mainFrame) {
		if (this.frame == null) {
			this.frame = mainFrame;
		}
		initComponents();
	}

	/**
	 * 
	 * @param comp
	 * @param font
	 * @param g
	 * @return
	 */
	protected int calcEstimatedWidth(JLabel comp, Font font, Graphics g) {

		int estwidth = 0;

		for (String s : comp.getText().split("<br>|\n")) {
			s = s.replaceAll("<.*>", "");

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
	 * @param g
	 * @return
	 */
	protected int calcEstimatedWidth(JLabel comp, Graphics g) {

		return calcEstimatedWidth(comp, comp.getFont(), g);
	}

	/**
	 * 
	 * @param comp
	 * @return
	 */
	protected int calcEstimatedWidth(JLabel comp) {

		return calcEstimatedWidth(comp, comp.getFont(), comp.getGraphics());
	}

	/**
	 * 
	 * @param comp
	 * @param font
	 * @param g
	 * @return
	 */
	protected int calcEstimatedHeight(JLabel comp, Font font, Graphics g) {

		double oneline = comp.getFontMetrics(font).getStringBounds(comp.getText(), g).getBounds().getHeight();

		return (int) (oneline * comp.getText().split("<br>|\n").length);
	}

	/**
	 * 
	 * @param comp
	 * @param g
	 * @return
	 */
	protected int calcEstimatedHeight(JLabel comp, Graphics g) {

		return calcEstimatedHeight(comp, comp.getFont(), g);
	}

	/**
	 * 
	 * @param comp
	 * @return
	 */
	protected int calcEstimatedHeight(JLabel comp) {
		return calcEstimatedHeight(comp, comp.getFont(), comp.getGraphics());
	}

	protected PVector calcSize(JLabel comp, Font font, Graphics g) {
		double x = calcEstimatedWidth(comp, font, g);
		double y = calcEstimatedHeight(comp, font, g);
		return new PVector(x, y);
	}

	protected PVector calcSize(JLabel comp, Graphics g) {
		return calcSize(comp, comp.getFont(), g);
	}

	protected PVector calcSize(JLabel comp) {
		return calcSize(comp, comp.getFont(), comp.getGraphics());
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
	 * @param data
	 */
	public void updateData(SongData data) {

		this.songName = data.getTitle();

		this.artistName = data.getAuthor();

		this.albumImage = data.getImage();

		this.danceName = data.getDance();

		this.nextData = data.getNext();
		this.hasNextData = this.nextData != null;
		refresh();
	}

	/**
	 * 
	 * @return
	 */
	public SongWindowSpecs getWindowSpecs() {
		return this.windowSpecs;
	}
}
