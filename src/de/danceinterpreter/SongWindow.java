package de.danceinterpreter;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;

public class SongWindow {
	private GraphicsDevice fullscreenDevice;
	private JFrame mainframe;
	private JPanel mainpanel;
	private Rectangle rect;
	private JLabel imglabel = new JLabel();
	private JTextArea text = new JTextArea();

	/**
	 * 
	 */

	public SongWindow(String songname, String artist, String dance, String img) {

		mainframe = new JFrame();
		mainpanel = new JPanel();
		
		mainframe.add(mainpanel);

		rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
				.getBounds();

		mainpanel.setBackground(Color.BLACK);
		mainpanel.setLayout(new FlowLayout(FlowLayout.CENTER, rect.width/3, rect.height/20));
		mainpanel.setBackground(Color.BLACK);
		mainpanel.setBounds(0, 0, rect.width, rect.height);
		mainframe.setTitle("DanceList");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainpanel.add(imglabel);
		mainpanel.add(text);
		mainpanel.setVisible(true);
		
		imglabel.setBackground(Color.BLACK);
		imglabel.setAlignmentY(0);
		imglabel.setAlignmentX(0);
		
		text.setBackground(Color.BLACK);
		text.setEditable(false);
		text.setForeground(Color.WHITE);
		text.setAlignmentY(1);
		text.setAlignmentX(0);
		text.setFont(new Font("Monospaced", Font.PLAIN, 36));
		

		UpdateWindow(songname, artist, dance, img);
	}

	public void UpdateWindow(String songname, String artist, String dance, String img) {
		
		this.rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().getBounds();
		System.out.println(rect.height);
		System.out.println(rect.width);

		try {
			BufferedImage buffimg = ImageIO.read(new URL(img));
			Image scaledimg = buffimg.getScaledInstance(rect.height/3, rect.height/3, 0);
			ImageIcon imageIcon = new ImageIcon(scaledimg);

			
			imglabel.setIcon(imageIcon);
			imglabel.setBounds((rect.width/2)-(imageIcon.getIconWidth()/2), (rect.height / 10), rect.width, 640);

		} catch (IOException e) {
			e.printStackTrace();
		}

		text.setText("\nSongname: " + songname + "\n\nArtist: " + artist + "\n\nTanz: " + dance);
		text.setBounds((rect.width/2)-(text.getWidth()/2), (rect.height / 10) + 700, text.getWidth(), rect.height/5);
		
		mainpanel.paintComponents(mainpanel.getGraphics());
		
		fullscreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		fullscreenDevice.setFullScreenWindow(mainframe);
	}

}
