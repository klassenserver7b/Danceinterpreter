/**
 * 
 */
package de.danceinterpreter.graphics;

import javax.swing.JFrame;

/**
 * @author K7
 *
 */
public interface TypedWindow {

	void show();

	void refresh();

	void close();

	JFrame getMainFrame();

}
