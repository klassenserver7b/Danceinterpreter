package de.danceinterpreter.graphics.icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

/**
 * 
 * @author Felix
 *
 */
public class ExitIcon implements Icon {

	private int width = 24;
	private int height = 24;

	private BasicStroke stroke = new BasicStroke(4);

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setColor(Color.WHITE);
		g2d.fillRect(x + 1, y + 1, width - 2, height - 2);

		g2d.setColor(Color.BLACK);
		g2d.drawRect(x + 1, y + 1, width - 2, height - 2);

		g2d.setColor(Color.RED);

		g2d.setStroke(stroke);
		g2d.drawLine(x + 5, y + 5, x + width - 5, y + height - 5);
		g2d.drawLine(x + 5, y + height - 5, x + width - 5, y + 5);

		g2d.dispose();
	}

	public int getIconWidth() {
		return width;
	}

	public int getIconHeight() {
		return height;
	}
}
