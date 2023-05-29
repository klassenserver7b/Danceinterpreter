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

	int show();

	int refresh();

	int close();

	JFrame getMainFrame();

}
