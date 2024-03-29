
package de.klassenserver7b.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class CustomKeyListener implements KeyListener {

	private List<CKeyListener> listeners = new ArrayList<>();

	@Override
	public void keyTyped(KeyEvent e) {
		// unused
	}

	@Override
	public void keyPressed(KeyEvent e) {

		for (CKeyListener listener : this.listeners) {
			listener.performPressedAction(e.getKeyCode(), e.getKeyLocation());
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		for (CKeyListener listener : this.listeners) {
			listener.performReleasedAction(e.getKeyCode());
		}
	}

	public void registerKeyListener(CKeyListener listener) {
		this.listeners.add(listener);
	}

	public void registerKeyListeners(CKeyListener... keylisteners) {

		for (CKeyListener l : keylisteners) {
			this.listeners.add(l);
		}
	}

}
