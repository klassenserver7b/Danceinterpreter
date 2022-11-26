/**
 * 
 */
package de.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import de.danceinterpreter.Main;
import de.danceinterpreter.songprocessing.DanceInterpreter;

/**
 * @author Felix
 *
 */
public class FullscreenListener extends CKeyListener {

	@Override
	public void performAction(int keycode) {

		if (keycode != KeyEvent.VK_F11) {
			return;
		}

		DanceInterpreter interpreter = Main.Instance.getDanceInterpreter();

		JFrame frame = interpreter.getWindow().getMainFrame();

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
