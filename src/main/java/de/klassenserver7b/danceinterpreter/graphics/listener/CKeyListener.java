package de.klassenserver7b.danceinterpreter.graphics.listener;

public interface CKeyListener {

	default void performPressedAction(int keycode) {
	};

	default void performPressedAction(int keycode, int keylocation) {
		performPressedAction(keycode);
	};

	default void performReleasedAction(int keycode) {
	};

}
