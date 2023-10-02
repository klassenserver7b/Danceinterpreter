/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics;

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
	 * @param containsNext
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
		return containsImage;
	}

	public boolean containsArtist() {
		return containsArtist;
	}

	public boolean containsTitle() {
		return containsTitle;
	}

	public boolean containsDance() {
		return containsDance;
	}

	public boolean containsNext() {
		return containsNext;
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
		return Objects.hash(containsArtist, containsDance, containsImage, containsNext, containsTitle);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SongWindowSpecs))
			return false;
		SongWindowSpecs other = (SongWindowSpecs) obj;
		return containsArtist == other.containsArtist && containsDance == other.containsDance
				&& containsImage == other.containsImage && containsNext == other.containsNext
				&& containsTitle == other.containsTitle;
	}

}
