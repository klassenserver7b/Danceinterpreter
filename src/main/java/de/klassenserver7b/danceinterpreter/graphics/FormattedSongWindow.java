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
import javax.swing.JLabel;

import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
 * @author K7
 *
 */
public abstract class FormattedSongWindow implements TypedWindow {

	protected final SongWindowSpecs windowSpecs;
	protected String danceName;
	protected String songName;
	protected String artistName;
	protected BufferedImage albumImage;
	protected boolean hasAlbumImage;
	protected boolean hasNextData;
	protected SongData nextData;

	/**
	 * 
	 * @param songWindowSpecs
	 * @param withImage
	 */
	public FormattedSongWindow(SongWindowSpecs songWindowSpecs) {
		this.windowSpecs = songWindowSpecs;
		this.hasAlbumImage = songWindowSpecs.containsImage();
		this.danceName = "";
		this.songName = "";
		this.artistName = "";
		this.hasAlbumImage = false;
		this.albumImage = null;
		this.hasNextData = false;
		this.nextData = null;
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

		for (String s : comp.getText().split("\n")) {

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

		int estwidth = 0;

		for (String s : comp.getText().split("\n")) {

			if (s.isBlank()) {
				continue;
			}

			double testwidth = comp.getFontMetrics(comp.getFont()).getStringBounds(s, g).getBounds().getWidth();

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
	 * @param g
	 * @return
	 */
	protected int calcEstimatedHeight(JLabel comp, Font font, Graphics g) {

		double oneline = comp.getFontMetrics(font).getStringBounds(comp.getText(), g).getBounds().getHeight();

		return (int) oneline * comp.getText().split("\n").length;
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
		refresh();
	}

	/**
	 * 
	 * @return
	 */
	public SongWindowSpecs getWindowSpecs() {
		return windowSpecs;
	}
}
