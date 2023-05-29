package de.danceinterpreter.graphics;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Felix
 */
public class SongWindow extends FormattedSongWindow {
	public final Logger log = LoggerFactory.getLogger("Window");

	private static final int OUTER_SPACING = 20;
	private static final int INNER_SPACING = 10;
	private static final int SONG_VIEW_MAX_WIDTH = 500;


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

		initComponents();

		//TODO use actual content
		textDance.setText(danceName);
		textSong.setText(songName);
		textArtist.setText(artistName);

		try {
			albumImage = ImageIO.read(new URL("https://i.scdn.co/image/ab67616d0000b273f1bff89049561177b7cccebb"));
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

		textDance.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 100));
		textDance.setForeground(Color.white);
		frame.add(textDance);

		textSong = new JLabel();
		textSong.setHorizontalAlignment(SwingConstants.LEFT);

		textSong.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		textSong.setForeground(Color.white);
		frame.add(textSong);

		textArtist = new JLabel();
		textArtist.setHorizontalAlignment(SwingConstants.LEFT);

		textArtist.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		textArtist.setForeground(Color.white);
		frame.add(textArtist);

		imgAlbum = new JLabel();
		frame.add(imgAlbum);
	}

	private void calculateSizes() {
		textDance.setBounds(0, OUTER_SPACING / 2, frame.getWidth(), frame.getHeight() / 2 - OUTER_SPACING / 2);

		int songViewTotalWidth = Math.min(frame.getWidth() - OUTER_SPACING * 2, SONG_VIEW_MAX_WIDTH);
		int songViewStart = (frame.getWidth() - songViewTotalWidth) / 2;
		int songViewEnd = (frame.getWidth() + songViewTotalWidth) / 2;

		if (hasAlbumImage) {
			int imgAlbumSize = textSong.getFont().getSize() + INNER_SPACING + textArtist.getFont().getSize();
			imgAlbum.setBounds(songViewStart, frame.getHeight() / 2 + OUTER_SPACING / 2, imgAlbumSize, imgAlbumSize);
			songViewStart += imgAlbumSize + INNER_SPACING;

			Image scaledImage = albumImage.getScaledInstance(imgAlbumSize, imgAlbumSize, Image.SCALE_SMOOTH);
			imgAlbum.setIcon(new ImageIcon(scaledImage));
		}

		textSong.setBounds(songViewStart, frame.getHeight() / 2 + OUTER_SPACING / 2, songViewEnd - songViewStart, textSong.getFont().getSize());
		textArtist.setBounds(songViewStart, textSong.getY() + textSong.getHeight() + INNER_SPACING, songViewEnd - songViewStart, textArtist.getFont().getSize());
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

	@Override
	public JPanel getMainPanel() {
		return null;
	}
}