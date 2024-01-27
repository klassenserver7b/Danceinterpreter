
package de.klassenserver7b.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

/**
**/

public class BlankListener implements CKeyListener {

	@Override
	public void performPressedAction(int keycode) {

		if (keycode != KeyEvent.VK_END && keycode != KeyEvent.VK_PAUSE) {
			return;
		}

		Main.Instance.getSongWindowServer().provideData(new SongData(""));

	}
}
