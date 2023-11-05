
package de.klassenserver7b.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.AppModes;
import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.songprocessing.dataprovider.PlaylistSongDataProvider;

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
			this.pressed = true;
		}

		if (this.pressed) {

			if (keycode == KeyEvent.VK_BACK_SPACE) {
				if (this.numbers.length() <= 0) {
					return;
				} else if (this.numbers.length() == 1) {
					this.numbers = "";
					return;
				}
				this.numbers = this.numbers.substring(0, this.numbers.length() - 1);
				return;
			}

			if (keycode >= 96 && keycode <= 105) {
				keycode -= 48;
			}

			Character c = (char) keycode;

			if (!Character.isDigit(c)) {
				return;
			}

			this.numbers = this.numbers + c;

		}

	}

	@Override
	public void performReleasedAction(int keycode) {

		if (keycode == KeyEvent.VK_CONTROL) {
			this.pressed = false;

			if (this.numbers.isBlank()) {
				this.numbers = "";
				return;
			}

			try {

				int pos = Integer.valueOf(this.numbers);

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
				this.log.warn(e.getMessage(), e);
			}

			this.numbers = "";
		}
	}

}
