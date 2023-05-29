
package de.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;

import de.danceinterpreter.AppModes;
import de.danceinterpreter.Main;
import de.danceinterpreter.songprocessing.dataprovider.PlaylistSongDataProvider;

/**
**/

public class ArrowSpaceKeyListener implements CKeyListener {

	@Override
	public void performPressedAction(int keycode) {

		if (keycode != KeyEvent.VK_LEFT && keycode != KeyEvent.VK_RIGHT && keycode != KeyEvent.VK_SPACE) {
			return;
		}

		AppModes am = Main.Instance.getAppMode();
		if (am != AppModes.Playlist) {
			return;
		}

		assert (am.getDataProvider() instanceof PlaylistSongDataProvider);
		PlaylistSongDataProvider prov = (PlaylistSongDataProvider) am.getDataProvider();

		if (keycode == KeyEvent.VK_LEFT) {
			prov.setDirection(-1);
		} else {
			prov.setDirection(1);
		}

		prov.provideAsync();

	}

}
