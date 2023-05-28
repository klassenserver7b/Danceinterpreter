/**
 * 
 */
package de.danceinterpreter.graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author felix
 *
 */
public interface TypedWindow {
	
	public int show();
	public int refresh();
	public int close();

	public JFrame getMainFrame();
	public JPanel getMainPanel();

}
