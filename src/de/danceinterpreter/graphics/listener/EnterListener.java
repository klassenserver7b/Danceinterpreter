/**
 * 
 */
package de.danceinterpreter.graphics.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.text.JTextComponent;

/**
 * @author Felix
 *
 */
public class EnterListener implements KeyListener {

	private JButton button;
	private JTextComponent comp;

	/**
	 * 
	 */
	public EnterListener(JButton b, JTextComponent txt) {
		button = b;
		comp = txt;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			if (!comp.getText().isBlank()) {
				button.doClick();
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
