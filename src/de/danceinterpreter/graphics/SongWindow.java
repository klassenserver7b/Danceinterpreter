package de.danceinterpreter.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.graphics.listener.CustomKeyListener;
import de.danceinterpreter.graphics.listener.FullscreenListener;

/**
 * @author Felix
 */
public class SongWindow extends FormattedSongWindow {
	public final Logger log = LoggerFactory.getLogger("Window");

	private JFrame frame;

	private String danceName = "Jive";
	private String songName = "Someone To You";
	private String artistName = "BANNERS";
	private BufferedImage albumImage;

	private boolean hasAlbumImage = true;

	private JLabel textDance;
	private JLabel textSong;
	private JLabel textArtist;
	private JLabel imgAlbum;

	/**
	 * @param songname
	 * @param artist
	 * @param dance
	 * @param img
	 */
	public SongWindow() {
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
		frame.addKeyListener(keylis);

		initComponents();

		// TODO use actual content
		textDance.setText(danceName);
		textSong.setText(songName);
		textArtist.setText(artistName);

		try {
			// albumImage = ImageIO.read(new
			// URL("https://i.scdn.co/image/ab67616d0000b273f1bff89049561177b7cccebb"));
			albumImage = ImageIO.read(new URL("https://img.youtube.com/vi/U2H1LInYPXE/maxresdefault.jpg"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				calculateSizes();
			}
		});

		frame.setVisible(true);
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

	private void calculateSizes() {
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
			int imgAlbumHeight = textSong.getFont().getSize() + INNER_SPACING + textArtist.getFont().getSize();
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
				textSong.getFont().getSize());
		textArtist.setBounds(songViewStart, textSong.getY() + textSong.getHeight() + INNER_SPACING,
				songViewEnd - songViewStart, textArtist.getFont().getSize());
	}

	protected int calcSongViewStart(int songViewTotalWidth) {
		return (frame.getWidth() - songViewTotalWidth) / 2;
	}

	protected int calcSongViewEnd(int songViewStart, int songViewTotalWidth) {
		return songViewStart + songViewTotalWidth;
	}

	@Override
	public int show() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int close() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int refresh() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JFrame getMainFrame() {
		return frame;
	}

}