package de.klassenserver7b.danceinterpreter.songprocessing;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.imageio.ImageIO;

import de.klassenserver7b.danceinterpreter.graphics.util.SongWindowSpecs;

/**


 *
 
 
 */
public class SongData {

	private String title;
	private String artist;
	private String dance;
	private long duration;
	private BufferedImage img;
	private boolean hasNext;
	private SongData nextData;

	/**
	 * @param title
	 * @param artist
	 * @param dance
	 * @param duration
	 * @param img
	 * @param hasNext
	 * @param nextData
	 */
	public SongData(String title, String artist, String dance, long duration, BufferedImage img, boolean hasNext,
			SongData nextData) {
		this.title = title;
		this.artist = artist;
		this.dance = dance;
		this.duration = duration;
		this.img = img;
		this.hasNext = hasNext;
		this.nextData = nextData;
	}

	/**
	 * 
	 * @param title
	 * @param artist
	 * @param dance
	 * @param duration
	 * @param img
	 * @param nextData
	 */
	public SongData(String title, String artist, String dance, long duration, BufferedImage img, SongData nextData) {
		this(title, artist, dance, duration, img, nextData != null, nextData);
	}

	/**
	 * 
	 * @param tit
	 * @param artist
	 * @param dance
	 * @param dur
	 * @param img
	 */
	public SongData(String tit, String artist, String dance, long dur, BufferedImage img) {
		this(tit, artist, dance, dur, img, false, null);
	}

	/**
	 * 
	 * @param tit
	 * @param artist
	 * @param dance
	 * @param dur
	 * @param imgurl
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public SongData(String tit, String artist, String dance, Long dur, String imgurl)
			throws MalformedURLException, IOException {

		this(tit, artist, dance, dur, ImageIO.read(new URL(imgurl)));
	}

	/**
	 * 
	 * @param tit
	 * @param artist
	 * @param dance
	 */
	public SongData(String tit, String artist, String dance) {

		this(tit, artist, dance, -1, null, false, null);
	}

	/**
	 * 
	 * @param dance
	 */
	public SongData(String dance) {

		this("", "", dance, -1, null, false, null);
	}

	/**
	 * 
	 * @return
	 */
	public SongWindowSpecs toSongWindowSpecs() {
		return new SongWindowSpecs(this.img != null, this.artist != null, this.title != null,
				(this.dance != null && !this.dance.equalsIgnoreCase("unknown")), this.hasNext);
	}

	public String getTitle() {
		return this.title;
	}

	public String getArtist() {
		return this.artist;
	}

	public String getDance() {
		return this.dance;
	}

	public long getDuration() {
		return this.duration;
	}

	public BufferedImage getImage() {
		return this.img;
	}

	public boolean hasNext() {
		return this.hasNext;
	}

	public SongData getNext() {
		if (!hasNext()) {
			return null;
		}
		return this.nextData;
	}

	public void setTitle(String tit) {
		this.title = tit;
	}

	public void setArtist(String auth) {
		this.artist = auth;
	}

	public void setDance(String dance) {
		this.dance = dance;
	}

	public void setDuration(Long dur) {
		this.duration = dur;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public void setImg(String imgurl) throws MalformedURLException, IOException {
		BufferedImage buffimg = ImageIO.read(new URL(imgurl));
		this.img = buffimg;
	}

	public void setNext(SongData data) {
		if (data == null) {
			this.hasNext = false;
			return;
		}

		this.hasNext = true;
		this.nextData = data;

	}

	@Override
	public int hashCode() {
		return Objects.hash(this.artist, this.dance, this.duration, this.title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SongData other = (SongData) obj;
		return Objects.equals(this.artist, other.artist) && Objects.equals(this.dance, other.dance)
				&& Objects.equals(this.duration, other.duration) && Objects.equals(this.title, other.title);
	}

}
