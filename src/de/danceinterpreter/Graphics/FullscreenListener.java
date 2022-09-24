/**
 * 
 */
package de.danceinterpreter.Graphics;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import de.danceinterpreter.Main;
import de.danceinterpreter.Songprocessing.DanceInterpreter;

/**
 * @author Felix
 *
 */
public class FullscreenListener extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		DanceInterpreter interpreter = Main.Instance.getDanceInterpreter();

		JFrame frame = interpreter.getWindow().getMainFrame();

		frame.dispose();

		if (frame.isUndecorated()) {
			frame.setUndecorated(false);
		} else {
			frame.setUndecorated(true);
		}

		frame.setVisible(true);
		frame.repaint();
		
	}

}
