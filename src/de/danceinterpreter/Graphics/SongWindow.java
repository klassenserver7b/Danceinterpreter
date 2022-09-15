package de.danceinterpreter.Graphics;

import java.awt.*;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.*;

/**
 * 
 * @author Felix
 *
 */
public class SongWindow {
	public final Logger log = LoggerFactory.getLogger("Window");
	public JFrame mainframe;
	public JPanel mainpanel;
	private Rectangle rect;
	private JLabel imglabel = new JLabel();
	private JTextArea text = new JTextArea();

	/**
	 * 
	 * @param songname
	 * @param artist
	 * @param dance
	 * @param img
	 */
	public SongWindow(String songname, String artist, String dance, BufferedImage img) {

		mainframe = new JFrame();
		mainframe.setUndecorated(true);
		JRootPane root = mainframe.getRootPane();
		root.setWindowDecorationStyle(JRootPane.FRAME);
		root.setBorder(BorderFactory.createEmptyBorder());

		mainframe.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().getBounds());

		mainpanel = new JPanel();

		mainpanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "asynchronUpdate");
		mainpanel.getActionMap().put("asynchronUpdate", new AsynchronousProvideListener());

		mainframe.add(mainpanel);

		rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
				.getBounds();

		mainpanel.setBackground(Color.BLACK);
		mainpanel.setLayout(new FlowLayout(FlowLayout.CENTER, rect.width, rect.height / 20));
		mainpanel.setBackground(Color.BLACK);
		mainpanel.setBounds(0, 0, rect.width, rect.height);
		mainframe.setTitle("DanceList");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainpanel.add(imglabel);
		mainpanel.add(text);
		mainpanel.setVisible(true);

		imglabel.setBackground(Color.BLACK);

		text.setBackground(Color.BLACK);
		text.setEditable(false);
		text.setForeground(Color.WHITE);
		text.setFont(new Font("Monospaced", Font.PLAIN, 36));

		UpdateWindow(songname, artist, dance, img);
	}

	/**
	 * 
	 * @param songname
	 * @param artist
	 * @param dance
	 * @param img
	 */
	public void UpdateWindow(String songname, String artist, String dance, BufferedImage img) {

		this.rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
				.getBounds();
		log.info("width: " + rect.width + " height: " + rect.height);

		Image scaledimg = scaleImage(img, rect);
		ImageIcon imageIcon = new ImageIcon(scaledimg);

		imglabel.setIcon(imageIcon);
		imglabel.setBounds((rect.width / 2) - (imageIcon.getIconWidth() / 2), (rect.height / 10),
				imageIcon.getIconWidth() + rect.width / 3, imageIcon.getIconHeight() + 64);
		System.out.println("IMG: "+imglabel.getBounds());

		text.setText("\nSongname: " + songname + "\n\nArtist: " + artist + "\n\nTanz: " + dance);
		text.setBounds((rect.width / 2) - (text.getWidth() / 2), rect.height / 5 + imglabel.getHeight(), rect.width,
				rect.height - imglabel.getHeight());
		
		System.out.println("TEXT: "+text.getBounds());

		mainpanel.removeAll();
		mainpanel.add(imglabel);
		mainpanel.add(text);
		
		mainpanel.paintComponents(mainpanel.getGraphics());

		mainframe.setVisible(true);
	}

	public Image scaleImage(BufferedImage img, Rectangle rect) {
		Image scaledimg = img.getScaledInstance(-1, -1, 0);

		if (img.getHeight() >= rect.height * 0.50 || img.getWidth() >= rect.width * 0.75) {
			double heightscale = (rect.getHeight() * 0.50) / img.getHeight();
			double widthscale = (rect.getWidth() * 0.75) / img.getWidth();

			double scale;
			if (heightscale <= widthscale) {
				scale = heightscale;
			} else {
				scale = widthscale;
			}

			scaledimg = img.getScaledInstance((int) (img.getWidth() * scale), (int) (img.getHeight() * scale), 0);
		}

		return scaledimg;
	}

}
