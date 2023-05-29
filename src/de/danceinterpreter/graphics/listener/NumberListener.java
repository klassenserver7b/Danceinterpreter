
package de.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.AppModes;
import de.danceinterpreter.Main;
import de.danceinterpreter.songprocessing.dataprovider.PlaylistSongDataProvider;

/**
**/

public class NumberListener implements CKeyListener {

	private boolean pressed;
	private String numbers;
	private final Logger log;

	/**
	 * 
	 
	
	
	*/
	public NumberListener() {
		super();
		this.pressed = false;
		this.numbers = "";
		this.log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void performPressedAction(int keycode, int keylocation) {

		if (keycode == KeyEvent.VK_CONTROL) {
			pressed = true;
		}

		if (pressed) {

			if (keycode == KeyEvent.VK_BACK_SPACE) {
				if (numbers.length() <= 0) {
					return;
				} else if (numbers.length() == 1) {
					numbers = "";
					return;
				}
				numbers = numbers.substring(0, numbers.length() - 1);
				return;
			}

			if (keycode >= 96 && keycode <= 105) {
				keycode -= 48;
			}

			Character c = (char) keycode;

			if (!Character.isDigit(c)) {
				return;
			}

			numbers = numbers + c;

		}

	}

	@Override
	public void performReleasedAction(int keycode) {

		if (keycode == KeyEvent.VK_CONTROL) {
			pressed = false;

			if (numbers.isBlank()) {
				numbers = "";
				return;
			}

			try {

				int pos = Integer.valueOf(numbers);

				AppModes am = Main.Instance.getAppMode();
				if (am != AppModes.Playlist) {
					return;
				}

				assert (am.getDataProvider() instanceof PlaylistSongDataProvider);
				PlaylistSongDataProvider prov = (PlaylistSongDataProvider) am.getDataProvider();

				if (prov.setPosition(pos)) {
					prov.provideAsync();
				}

			} catch (NumberFormatException e) {
				log.warn(e.getMessage(), e);
			}

			numbers = "";
		}
	}

}
