/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics;

/**
 * @author K7
 */
public class PVector {

	private double x;
	private double y;
	private double z;

	public PVector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PVector(double x, double y) {
		this(x, y, 0);
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}

	public PVector copy() {
		return new PVector(this.x, this.y, this.z);
	}

}
