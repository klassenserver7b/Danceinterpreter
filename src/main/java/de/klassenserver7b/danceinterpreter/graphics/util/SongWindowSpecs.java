/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics.util;

import java.util.Objects;

/**
 * 
 */
public class SongWindowSpecs {

	private boolean containsImage;
	private boolean containsTitle;
	private boolean containsArtist;
	private boolean containsDance;
	private boolean containsNext;

	/**
	 * @param containsImage
	 * @param containsArtist
	 * @param containsTitle
	 * @param containsDance
	 * @param hasNext
	 */
	public SongWindowSpecs(boolean containsImage, boolean containsArtist, boolean containsTitle, boolean containsDance,
			boolean hasNext) {
		this.containsImage = containsImage;
		this.containsArtist = containsArtist;
		this.containsTitle = containsTitle;
		this.containsDance = containsDance;
		this.containsNext = hasNext;
	}

	/**
	 * All true Constructor
	 */
	public SongWindowSpecs() {
		this(true, true, true, true, true);
	}

	public boolean containsImage() {
		return this.containsImage;
	}

	public boolean containsArtist() {
		return this.containsArtist;
	}

	public boolean containsTitle() {
		return this.containsTitle;
	}

	public boolean containsDance() {
		return this.containsDance;
	}

	public boolean containsNext() {
		return this.containsNext;
	}

	public void setContainsImage(boolean containsImage) {
		this.containsImage = containsImage;
	}

	public void setContainsArtist(boolean containsArtist) {
		this.containsArtist = containsArtist;
	}

	public void setContainsTitle(boolean containsTitle) {
		this.containsTitle = containsTitle;
	}

	public void setContainsDance(boolean containsDance) {
		this.containsDance = containsDance;
	}

	public void setContainsNext(boolean hasNext) {
		this.containsNext = hasNext;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.containsArtist, this.containsDance, this.containsImage, this.containsNext,
				this.containsTitle);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SongWindowSpecs))
			return false;
		SongWindowSpecs other = (SongWindowSpecs) obj;
		return this.containsArtist == other.containsArtist && this.containsDance == other.containsDance
				&& this.containsImage == other.containsImage && this.containsNext == other.containsNext
				&& this.containsTitle == other.containsTitle;
	}

}
