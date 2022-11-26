/**
 * 
 */
package de.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;

import de.danceinterpreter.AppModes;
import de.danceinterpreter.Main;
import de.danceinterpreter.songprocessing.dataprovider.PlaylistSongDataProvider;

/**
 * @author Felix
 *
 */
public class ArrowKeyListener extends CKeyListener {

	@Override
	public void performAction(int keycode) {

		if (keycode != KeyEvent.VK_LEFT && keycode != KeyEvent.VK_RIGHT) {
			return;
		}

		AppModes am = Main.Instance.getAppMode();
		if (am != AppModes.Playlist) {
			return;
		}

		PlaylistSongDataProvider prov = (PlaylistSongDataProvider) am.getDataProvider();

		if (keycode == KeyEvent.VK_RIGHT) {
			prov.setDirection(true);
		} else {
			prov.setDirection(false);
		}

		prov.provideAsynchronous();

	}

}
