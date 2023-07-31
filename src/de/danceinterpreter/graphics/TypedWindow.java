/**
 * 
 */
package de.danceinterpreter.graphics;

import javax.swing.JFrame;

/**
 * @author felix
 *
 */
public interface TypedWindow {

	void show();

	void refresh();

	void close();

	JFrame getMainFrame();

}
