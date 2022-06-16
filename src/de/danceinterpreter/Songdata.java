package de.danceinterpreter;

import java.util.Objects;


/**
 * 
 * @author felix
 *
 */
public class Songdata {

	private String title;
	private String author;
	private String dance;
	private Long duration;
	private String img;

	public Songdata(String tit, String author, String dance, Long dur, String img) {

		this.title = tit;
		this.author = author;
		this.dance = dance;
		this.duration = dur;
		this.img = img;
	}

	public String getTitle() {
		return this.title;
	}

	public String getAuthor() {
		return this.author;
	}

	public String getDance() {
		return this.dance;
	}

	public Long getDuration() {
		return this.duration;
	}
	
	public String getImageURL() {
		return this.img;
	}

	public void setTitle(String tit) {
		this.title = tit;
	}

	public void setAuthor(String auth) {
		this.author = auth;
	}

	public void setDance(String dance) {
		this.dance = dance;
	}

	public void setDuration(Long dur) {
		this.duration = dur;
	}

	@Override
	public int hashCode() {
		return Objects.hash(author, dance, duration, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Songdata other = (Songdata) obj;
		return Objects.equals(author, other.author) && Objects.equals(dance, other.dance)
				&& Objects.equals(duration, other.duration) && Objects.equals(title, other.title);
	}

}
