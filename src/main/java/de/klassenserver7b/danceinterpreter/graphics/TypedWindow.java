/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics;

import javax.swing.JFrame;

/**
 * @author K7
 *
 */
public interface TypedWindow {

	void refresh();
	
	void onResize();
	
	void onInit(JFrame mainFrame);
	
	void initComponents();

}
