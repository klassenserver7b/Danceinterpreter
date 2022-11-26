/**
 * 
 */
package de.danceinterpreter.graphics;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.danceinterpreter.Main;
import de.danceinterpreter.songprocessing.DanceInterpreter;

/**
 * @author Felix
 *
 */
public class RefreshListenerListener extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {

		DanceInterpreter interpreter = Main.Instance.getDanceInterpreter();
		interpreter.getWindow().log.debug("KEY_PRESSED: REFRESH");
		Main.Instance.getAppMode().getDataProvider().provideAsynchronous();

	}
}
