/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics.listener;

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

		this.frame.dispose();

		if (this.frame.isUndecorated()) {
			this.frame.setUndecorated(false);
		} else {
			this.frame.setUndecorated(true);
		}

		this.frame.setVisible(true);
		this.frame.repaint();

	}
}
