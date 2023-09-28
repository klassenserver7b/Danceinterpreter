package de.klassenserver7b.danceinterpreter.graphics.songwindows;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.graphics.FormattedSongWindow;
import de.klassenserver7b.danceinterpreter.graphics.SongWindowSpecs;
import de.klassenserver7b.danceinterpreter.graphics.listener.ArrowSpaceKeyListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.CustomKeyListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.FullscreenListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.NumberListener;
import de.klassenserver7b.danceinterpreter.graphics.listener.RefreshListener;

/**
 * @author K7
 */
public class SongWindowBdImgTA extends FormattedSongWindow {
	public final Logger log = LoggerFactory.getLogger("Window");

	protected JFrame frame;

	protected JLabel textDance;
	protected JLabel textSong;
	protected JLabel textArtist;
	protected JLabel imgAlbum;

	/**
	 * 
	 */
	public SongWindowBdImgTA() {
		this(true);
	}

	/**
	 * 
	 * @param withimage
	 */
	public SongWindowBdImgTA(boolean withimage) {
		this(new SongWindowSpecs(withimage, true, true, true, false));
		this.hasAlbumImage = withimage;
	}

	/**
	 * 
	 * @param windowspecs
	 */
	protected SongWindowBdImgTA(SongWindowSpecs windowspecs) {

		super(windowspecs);

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Rectangle screenBounds = devices[0].getDefaultConfiguration().getBounds();

		frame = new JFrame();

		frame.setTitle("DanceInterpreter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setBounds(screenBounds);
		frame.setLayout(null);

		frame.getContentPane().setBackground(Color.BLACK);

		CustomKeyListener keylis = new CustomKeyListener();
		keylis.registerKeyListeners(new FullscreenListener(frame));
		keylis.registerKeyListeners(new ArrowSpaceKeyListener());
		keylis.registerKeyListeners(new NumberListener());
		keylis.registerKeyListeners(new RefreshListener());
		frame.addKeyListener(keylis);

		initComponents();

		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				calculateSizes();
			}
		});
	}

	private void initComponents() {
		textDance = new JLabel();
		textDance.setHorizontalAlignment(SwingConstants.CENTER);
		textDance.setVerticalAlignment(SwingConstants.BOTTOM);

		textDance.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 10));
		textDance.setForeground(Color.white);
		frame.add(textDance);

		textSong = new JLabel();
		textSong.setHorizontalAlignment(SwingConstants.LEFT);

		textSong.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 20));
		textSong.setForeground(Color.white);
		frame.add(textSong);

		textArtist = new JLabel();
		textArtist.setHorizontalAlignment(SwingConstants.LEFT);

		textArtist.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 30));
		textArtist.setForeground(Color.white);
		frame.add(textArtist);

		imgAlbum = new JLabel();
		frame.add(imgAlbum);
	}

	protected void calculateSizes() {
		int OUTER_SPACING = frame.getWidth() / 7;
		int INNER_SPACING = frame.getWidth() / 150;

		textDance.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 8));
		textSong.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 20));
		textArtist.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 30));

		textDance.setBounds(0, OUTER_SPACING / 2, frame.getWidth(), frame.getHeight() / 2 - OUTER_SPACING / 2);

		// int songViewTotalWidth = Math.min(frame.getWidth() - OUTER_SPACING * 2,
		// SONG_VIEW_MAX_WIDTH);
		int songViewTotalWidth = Math.max(calcEstimatedWidth(textSong, frame.getGraphics()),
				calcEstimatedWidth(textArtist, frame.getGraphics()));
		int songViewStart = calcSongViewStart(songViewTotalWidth);
		int songViewEnd = calcSongViewEnd(songViewStart, songViewTotalWidth);

		if (hasAlbumImage) {
			int imgAlbumHeight = calcEstimatedHeight(textSong, textSong.getFont(), textSong.getGraphics())
					+ INNER_SPACING + calcEstimatedHeight(textArtist, textArtist.getFont(), textArtist.getGraphics());
			int imgAlbumWidth = (int) (albumImage.getWidth() * (imgAlbumHeight / (double) albumImage.getHeight()));

			Image scaledImage = albumImage.getScaledInstance(imgAlbumWidth, imgAlbumHeight, Image.SCALE_SMOOTH);
			imgAlbum.setIcon(new ImageIcon(scaledImage));

			songViewTotalWidth += imgAlbumWidth;
			songViewStart = calcSongViewStart(songViewTotalWidth);
			songViewEnd = calcSongViewEnd(songViewStart, songViewTotalWidth);

			imgAlbum.setBounds(songViewStart, frame.getHeight() / 2 + OUTER_SPACING / 2, imgAlbumWidth, imgAlbumHeight);

			songViewStart += imgAlbumWidth + INNER_SPACING;
			songViewEnd = calcSongViewEnd(songViewStart, songViewTotalWidth);
		}

		textSong.setBounds(songViewStart, frame.getHeight() / 2 + OUTER_SPACING / 2, songViewEnd - songViewStart,
				calcEstimatedHeight(textSong, textSong.getFont(), textSong.getGraphics()));

		textArtist.setBounds(songViewStart, textSong.getY() + textSong.getHeight() + INNER_SPACING,
				songViewEnd - songViewStart,
				calcEstimatedHeight(textArtist, textArtist.getFont(), textArtist.getGraphics()));
	}

	protected int calcSongViewStart(int songViewTotalWidth) {
		return (frame.getWidth() - songViewTotalWidth) / 2;
	}

	protected int calcSongViewEnd(int songViewStart, int songViewTotalWidth) {
		return songViewStart + songViewTotalWidth;
	}

	@Override
	public void show() {
		refresh();
	}

	@Override
	public void close() {
		frame.dispose();
		frame.setVisible(false);
	}

	@Override
	public void refresh() {

		textDance.setText(super.danceName);
		textSong.setText(super.songName);
		textArtist.setText(super.artistName);

		calculateSizes();

		frame.setVisible(true);
		frame.requestFocus();
	}

	@Override
	public JFrame getMainFrame() {
		return frame;
	}

}