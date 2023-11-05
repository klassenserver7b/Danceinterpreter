package de.klassenserver7b.danceinterpreter.graphics.listener;

public interface CKeyListener {

	/**
	 * @param keycode the keycode of the key pressed by the user
	 */
	default void performPressedAction(int keycode) {
		return;
	}

	/**
	 * 
	 * @param keycode the keycode of the key pressed by the user
	 * @param keylocation the location of the key (e.g. NumPad-0 or 0)
	 */
	default void performPressedAction(int keycode, int keylocation) {
		performPressedAction(keycode);
	}

	/**
	 * @param keycode the keycode of the key released by the user
	 */
	default void performReleasedAction(int keycode) {
		return;
	}

}
