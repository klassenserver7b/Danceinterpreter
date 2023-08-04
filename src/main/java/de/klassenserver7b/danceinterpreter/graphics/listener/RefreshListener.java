
package de.klassenserver7b.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;

import de.klassenserver7b.danceinterpreter.Main;

/**
**/

public class RefreshListener implements CKeyListener {

	@Override
	public void performPressedAction(int keycode) {

		if (keycode != KeyEvent.VK_F5) {
			return;
		}

		Main.Instance.getSongWindowServer().refresh();

	}
}
