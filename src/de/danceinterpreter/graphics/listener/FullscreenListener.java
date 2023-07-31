/**
 * 
 */
package de.danceinterpreter.graphics.listener;

import java.awt.Frame;
import java.awt.event.KeyEvent;

/**
 * @author K7
 *
 */
public class FullscreenListener implements CKeyListener {

	private final Frame frame;

	/**
	 * 
	 */
	public FullscreenListener(Frame frame) {
		this.frame = frame;
	}

	@Override
	public void performPressedAction(int keycode) {

		if (keycode != KeyEvent.VK_F11) {
			return;
		}

		frame.dispose();

		if (frame.isUndecorated()) {
			frame.setUndecorated(false);
		} else {
			frame.setUndecorated(true);
		}

		frame.setVisible(true);
		frame.repaint();

	}
}
