/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics.util;

/**
 * @author K7
 */
public class DIVector {

	private double x;
	private double y;
	private double z;

	public DIVector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public DIVector(double x, double y) {
		this(x, y, 0);
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return this.z;
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

	public DIVector copy() {
		return new DIVector(this.x, this.y, this.z);
	}

}
