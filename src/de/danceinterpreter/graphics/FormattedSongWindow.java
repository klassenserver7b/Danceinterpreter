/**
 * 
 */
package de.danceinterpreter.graphics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * @author felix
 *
 */
public abstract class FormattedSongWindow implements TypedWindow {

	/**
	 * 
	 */
	public FormattedSongWindow() {

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

}
