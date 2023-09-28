package de.klassenserver7b.danceinterpreter.graphics.songwindows;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		this(new SongWindowSpecs(withimage, true, true, true, false));
	}

	/**
	 * 
	 * @param windowspecs
	 */
	protected SongWindowBdImgTAN(SongWindowSpecs windowspecs) {
		super(windowspecs);
		initComponents();
	}

	private void initComponents() {
		textNextDance = new JLabel();
		textNextDance.setHorizontalAlignment(SwingConstants.RIGHT);
		textNextDance.setVerticalAlignment(SwingConstants.BOTTOM);

		textNextDance.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 10));
		textNextDance.setForeground(Color.white);
		super.frame.add(textNextDance);
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