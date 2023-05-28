package de.danceinterpreter.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.FocusEvent.Cause;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Felix
 *
 */
public class SongWindow extends FormattedSongWindow {
	public final Logger log = LoggerFactory.getLogger("Window");
	private JFrame mainframe;
	private JPanel mainpanel;

	/**
	 * 
	 * @param songname
	 * @param artist
	 * @param dance
	 * @param img
	 */
	public SongWindow() {

		mainframe = new JFrame();
		mainpanel = new JPanel();

		mainframe.setLayout(null);
		mainpanel.setLayout(null);

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Rectangle b = devices[0].getDefaultConfiguration().getBounds();

		System.err.println(b);

		mainframe.setBounds(b);
		mainpanel.setBounds(b);

		mainpanel = new JPanel();

		mainframe.setContentPane(mainpanel);

		mainpanel.setBackground(Color.BLACK);

		mainframe.setTitle("DanceInterpreter");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTextArea tanz = new JTextArea();
		tanz.setEditable(false);
		tanz.setSelectedTextColor(Color.white);
		tanz.setSelectionColor(Color.black);
	
		tanz.setText("Jive");
		tanz.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 100));
		tanz.setForeground(Color.white);
		tanz.setBackground(Color.black);
		tanz.setBounds(200, 500, 1000, 900);
		tanz.setVisible(true);

		mainpanel.add(tanz);
		mainpanel.paintComponents(mainpanel.getGraphics());

		mainframe.setVisible(true);
		mainframe.requestFocus(Cause.ACTIVATION);

	}

	@Override
	public int show() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int close() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int refresh() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JFrame getMainFrame() {
		return mainframe;
	}

	@Override
	public JPanel getMainPanel() {
		return mainpanel;
	}
}