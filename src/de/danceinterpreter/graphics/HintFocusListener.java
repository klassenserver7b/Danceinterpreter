/**
 * 
 */
package de.danceinterpreter.graphics;

import java.awt.TextComponent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

/**
 * @author Felix
 *
 */
public class HintFocusListener implements FocusListener {

	private final String hint;

	/**
	 * 
	 */
	public HintFocusListener(String hint) {
		this.hint = hint;
	}

	@Override
	public void focusGained(FocusEvent e) {

		if (e.getComponent() instanceof TextComponent) {
			TextComponent comp = (TextComponent) e.getComponent();

			if (comp.getText().equals(hint)) {
				comp.setText("");
			}
		}

		if (e.getComponent() instanceof JTextComponent) {
			JTextComponent comp = (JTextComponent) e.getComponent();

			if (comp.getText().equals(hint)) {
				comp.setText("");
			}
		}

	}

	@Override
	public void focusLost(FocusEvent e) {

		if (e.getComponent() instanceof TextComponent) {
			TextComponent comp = (TextComponent) e.getComponent();

			if (comp.getText().isBlank()) {
				comp.setText(hint);
			}
		}

		if (e.getComponent() instanceof JTextComponent) {
			JTextComponent comp = (JTextComponent) e.getComponent();

			if (comp.getText().isBlank()) {
				comp.setText(hint);
			}
		}

	}

}
