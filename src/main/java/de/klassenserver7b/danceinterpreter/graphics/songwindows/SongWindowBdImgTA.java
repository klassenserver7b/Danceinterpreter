package de.klassenserver7b.danceinterpreter.graphics.songwindows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.graphics.FormattedSongWindow;
import de.klassenserver7b.danceinterpreter.graphics.SongWindowSpecs;

/**
 * @author K7
 */
public class SongWindowBdImgTA extends FormattedSongWindow {
	public final Logger log = LoggerFactory.getLogger("Window");

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
	}

	/**
	 * 
	 * @param windowspecs
	 */
	protected SongWindowBdImgTA(SongWindowSpecs windowspecs) {
		super(windowspecs);
		init();
	}

	private void init() {
		textDance = new JLabel();
		textDance.setHorizontalAlignment(SwingConstants.CENTER);
		textDance.setVerticalAlignment(SwingConstants.TOP);
		textDance.setForeground(Color.white);

		textSong = new JLabel();
		textSong.setHorizontalAlignment(SwingConstants.LEFT);
		textSong.setForeground(Color.white);

		textArtist = new JLabel();
		textArtist.setHorizontalAlignment(SwingConstants.LEFT);
		textArtist.setForeground(Color.white);

		imgAlbum = new JLabel();
	}

	@Override
	public void initComponents() {
		frame.add(textDance);
		frame.add(textSong);
		frame.add(textArtist);
		frame.add(imgAlbum);
	}

	@Override
	public void onResize() {

		int OUTER_SPACING = frame.getWidth() / 7;
		int INNER_SPACING = frame.getWidth() / 150;

		textDance.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 8));
		textSong.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 20));
		textArtist.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, frame.getHeight() / 25));

		textDance.setBounds(0, OUTER_SPACING, frame.getWidth(), frame.getHeight() / 2 - OUTER_SPACING / 2);

		int songViewTotalWidth = Math.max(calcEstimatedWidth(textSong), calcEstimatedWidth(textArtist));

		int songViewStart = calcSongViewStart(songViewTotalWidth);

		int songViewEnd = calcSongViewEnd(songViewStart, songViewTotalWidth);

		if (hasAlbumImage) {

			int imgAlbumHeight = calcEstimatedHeight(textSong) + INNER_SPACING + calcEstimatedHeight(textArtist);

			int imgAlbumWidth = (int) (albumImage.getWidth() * (imgAlbumHeight / (double) albumImage.getHeight()));

			Image scaledImage = albumImage.getScaledInstance(imgAlbumWidth, imgAlbumHeight, Image.SCALE_SMOOTH);
			imgAlbum.setIcon(new ImageIcon(scaledImage));

			songViewTotalWidth += imgAlbumWidth;
			songViewStart = calcSongViewStart(songViewTotalWidth);
			songViewEnd = calcSongViewEnd(songViewStart, songViewTotalWidth);

			imgAlbum.setBounds(songViewStart, (int) (OUTER_SPACING * 1.75), imgAlbumWidth, imgAlbumHeight);

			songViewStart += imgAlbumWidth + INNER_SPACING;
			songViewEnd = calcSongViewEnd(songViewStart, songViewTotalWidth);
		}

		textSong.setBounds(songViewStart, (int) (OUTER_SPACING * 1.75), songViewEnd - songViewStart,
				calcEstimatedHeight(textSong));

		textArtist.setBounds(songViewStart, textSong.getY() + textSong.getHeight() + INNER_SPACING,
				songViewEnd - songViewStart, calcEstimatedHeight(textArtist));
	}

	protected int calcSongViewStart(int songViewTotalWidth) {
		return (frame.getWidth() - songViewTotalWidth) / 2;
	}

	protected int calcSongViewEnd(int songViewStart, int songViewTotalWidth) {
		return songViewStart + songViewTotalWidth;
	}

	@Override
	public void refresh() {

		textDance.setText(super.danceName);
		textSong.setText(super.songName);
		textArtist.setText(super.artistName);

		this.onResize();
	}

}