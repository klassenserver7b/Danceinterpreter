/**
 * 
 */
package de.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Felix
 *
 */
public class CustomKeyListener implements KeyListener {

	private List<CKeyListener> listeners = new ArrayList<>();

	@Override
	public void keyTyped(KeyEvent e) {
		// unused
	}

	@Override
	public void keyPressed(KeyEvent e) {

		for (CKeyListener listener : listeners) {
			listener.performAction(e.getKeyCode());
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// unused
	}

	public void registerKeyListener(CKeyListener listener) {
		listeners.add(listener);
	}

	public void registerKeyListeners(CKeyListener... keylisteners) {

		for (CKeyListener l : keylisteners) {
			listeners.add(l);
		}
	}

}
