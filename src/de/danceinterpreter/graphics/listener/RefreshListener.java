/**
 * 
 */
package de.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;

import de.danceinterpreter.AppModes;
import de.danceinterpreter.Main;
import de.danceinterpreter.songprocessing.DanceInterpreter;

/**
 * @author Felix
 *
 */
public class RefreshListener extends CKeyListener {

	@Override
	public void performAction(int keycode) {

		if (keycode != KeyEvent.VK_F5) {
			return;
		}

		if (Main.Instance.getAppMode() == AppModes.Playlist) {
			return;
		}

		DanceInterpreter interpreter = Main.Instance.getDanceInterpreter();
		interpreter.getWindow().log.debug("KEY_PRESSED: REFRESH");
		Main.Instance.getAppMode().getDataProvider().provideAsynchronous();

	}
}
