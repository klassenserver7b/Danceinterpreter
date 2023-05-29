
package de.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;

import de.danceinterpreter.Main;

/**
**/

public class RefreshListener implements CKeyListener {

	@Override
	public void performPressedAction(int keycode) {

		if (keycode != KeyEvent.VK_F5) {
			return;
		}

		Main.Instance.getAppMode().getDataProvider().provideAsync();

	}
}
