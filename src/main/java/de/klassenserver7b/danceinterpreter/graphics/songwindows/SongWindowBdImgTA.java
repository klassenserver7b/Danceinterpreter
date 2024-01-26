package de.klassenserver7b.danceinterpreter.graphics.songwindows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.graphics.util.SongWindowSpecs;

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
		this.textDance = new JLabel();
		this.textDance.setHorizontalAlignment(SwingConstants.CENTER);
		this.textDance.setVerticalAlignment(SwingConstants.TOP);
		this.textDance.setForeground(Color.white);

		this.textSong = new JLabel();
		this.textSong.setHorizontalAlignment(SwingConstants.LEFT);
		this.textSong.setForeground(Color.white);

		this.textArtist = new JLabel();
		this.textArtist.setHorizontalAlignment(SwingConstants.LEFT);
		this.textArtist.setForeground(Color.white);

		this.imgAlbum = new JLabel();
	}

	@Override
	public void initComponents() {
		this.frame.add(this.textDance);
		this.frame.add(this.textSong);
		this.frame.add(this.textArtist);
		this.frame.add(this.imgAlbum);
	}

	@Override
	public void onResize() {

		int OUTER_SPACING = this.frame.getWidth() / 7;
		int INNER_SPACING = this.frame.getWidth() / 150;

		this.textDance.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, this.frame.getHeight() / 8));
		this.textSong.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, this.frame.getHeight() / 20));
		this.textArtist.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, this.frame.getHeight() / 25));

		this.textDance.setBounds(0, OUTER_SPACING, this.frame.getWidth(), this.frame.getHeight() / 2 - OUTER_SPACING / 2);

		int songViewTotalWidth = Math.max(calcEstimatedWidth(this.textSong), calcEstimatedWidth(this.textArtist));

		int songViewStart = calcSongViewStart(songViewTotalWidth);

		int songViewEnd = calcSongViewEnd(songViewStart, songViewTotalWidth);

		if (this.hasAlbumImage) {

			int imgAlbumHeight = calcEstimatedHeight(this.textSong) + INNER_SPACING + calcEstimatedHeight(this.textArtist);

			int imgAlbumWidth = (int) (this.albumImage.getWidth() * (imgAlbumHeight / (double) this.albumImage.getHeight()));

			Image scaledImage = this.albumImage.getScaledInstance(imgAlbumWidth, imgAlbumHeight, Image.SCALE_SMOOTH);
			this.imgAlbum.setIcon(new ImageIcon(scaledImage));

			songViewTotalWidth += imgAlbumWidth;
			songViewStart = calcSongViewStart(songViewTotalWidth);
			songViewEnd = calcSongViewEnd(songViewStart, songViewTotalWidth);

			this.imgAlbum.setBounds(songViewStart, (int) (OUTER_SPACING * 1.75), imgAlbumWidth, imgAlbumHeight);

			songViewStart += imgAlbumWidth + INNER_SPACING;
			songViewEnd = calcSongViewEnd(songViewStart, songViewTotalWidth);
		}

		this.textSong.setBounds(songViewStart, (int) (OUTER_SPACING * 1.75), songViewEnd - songViewStart,
				calcEstimatedHeight(this.textSong));

		this.textArtist.setBounds(songViewStart, this.textSong.getY() + this.textSong.getHeight() + INNER_SPACING,
				songViewEnd - songViewStart, calcEstimatedHeight(this.textArtist));
	}

	protected int calcSongViewStart(int songViewTotalWidth) {
		return (this.frame.getWidth() - songViewTotalWidth) / 2;
	}

	protected int calcSongViewEnd(int songViewStart, int songViewTotalWidth) {
		return songViewStart + songViewTotalWidth;
	}

	@Override
	public void refresh() {

		this.textDance.setText(super.danceName);
		this.textSong.setText(super.songName);
		this.textArtist.setText(super.artistName);

		this.onResize();
	}

}