/**
 * 
 */
package de.danceinterpreter.Graphics;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.danceinterpreter.Main;
import de.danceinterpreter.Songprocessing.DanceInterpreter;


/**
 * @author Felix
 *
 */
public class AsynchronousProvideListener extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		
		DanceInterpreter interpreter = Main.Instance.getDanceInterpreter();
		
		interpreter.getWindow().log.debug("KEY_PRESSED: REFRESH");
		interpreter.provideAsynchronous();

	}

}
