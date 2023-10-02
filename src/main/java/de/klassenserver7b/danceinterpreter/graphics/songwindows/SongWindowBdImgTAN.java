package de.klassenserver7b.danceinterpreter.graphics.songwindows;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.graphics.PVector;
import de.klassenserver7b.danceinterpreter.graphics.SongWindowSpecs;

/**
 * @author K7
 */
public class SongWindowBdImgTAN extends SongWindowBdImgTA {
	public final Logger log = LoggerFactory.getLogger("Window");

	private JLabel textNextDance;

	/**
	 * 
	 */
	public SongWindowBdImgTAN() {
		this(true);
	}

	/**
	 * 
	 * @param withimage
	 */
	public SongWindowBdImgTAN(boolean withimage) {
		this(new SongWindowSpecs(withimage, true, true, true, true));
	}

	/**
	 * 
	 * @param windowspecs
	 */
	protected SongWindowBdImgTAN(SongWindowSpecs windowspecs) {
		super(windowspecs);
		initComponents();
	}

	protected void initComponents() {
		textNextDance = new JLabel();
		textNextDance.setHorizontalAlignment(SwingConstants.CENTER);
		textNextDance.setVerticalAlignment(SwingConstants.CENTER);

		textNextDance.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 30));
		textNextDance.setForeground(Color.white);
		super.frame.add(textNextDance);
	}

	@Override
	protected void calculateSizes() {
		super.calculateSizes();

		PVector size = calcSize(textNextDance);
		int size_x = (int) size.getX();
		int size_y = (int) size.getY();

		textNextDance.setBounds(super.frame.getWidth() - (int) (size_x * 1.5),
				super.frame.getHeight() - size_y * 3, size_x, size_y);
	}

	@Override
	public void close() {
		frame.dispose();
		frame.setVisible(false);
	}

	@Override
	public void refresh() {
		super.refresh();

		textNextDance.setText(nextData.getDance());

		calculateSizes();

		frame.setVisible(true);
		frame.requestFocus();
	}

	@Override
	public JFrame getMainFrame() {
		return frame;
	}

}