package de.klassenserver7b.danceinterpreter.graphics.songwindows;

import java.awt.Color;
import java.awt.Font;

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
		init();
	}

	private void init() {
		this.textNextDance = new JLabel();
		this.textNextDance.setHorizontalAlignment(SwingConstants.CENTER);
		this.textNextDance.setVerticalAlignment(SwingConstants.CENTER);
		this.textNextDance.setForeground(Color.white);
	}

	@Override
	public void initComponents() {
		super.initComponents();
		this.frame.add(this.textNextDance);
	}

	@Override
	public void onResize() {
		super.onResize();

		PVector size = calcSize(this.textNextDance);
		int size_x = (int) size.getX();
		int size_y = (int) size.getY();

		this.textNextDance.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, this.frame.getHeight() / 20));
		this.textNextDance.setBounds(this.frame.getWidth() - size_x - this.frame.getWidth() / 20,
				this.frame.getHeight() - size_y - this.frame.getHeight() / 10, size_x, size_y);
	}

	@Override
	public void refresh() {
		super.refresh();
		this.textNextDance.setText("<html><body>nächster Tanz:<br>" + this.nextData.getDance() + "</body></html>");
		this.onResize();
	}

}