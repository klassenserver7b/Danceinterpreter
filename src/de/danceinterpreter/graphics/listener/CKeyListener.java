/**
 * 
 */
package de.danceinterpreter.graphics.listener;

/**
 * @author Felix
 *
 */
public abstract class CKeyListener {

	public void performPressedAction(int keycode) {
	};

	public void performPressedAction(int keycode, int keylocation) {
		performPressedAction(keycode);
	};

	public void performReleasedAction(int keycode) {
	};

}
