package de.klassenserver7b.danceinterpreter.graphics.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.klassenserver7b.danceinterpreter.Main;

public class SongAddPanel extends JFrame {

	private static final long serialVersionUID = 9007970982830756365L;

	public SongAddPanel() {

		setSize(800, 600);
		setLayout(null);

		JPanel mainPan = new JPanel();
		mainPan.setSize(getWidth(), getHeight());
		mainPan.setLayout(null);

		initComponents(mainPan);
		add(mainPan);

		setVisible(true);
	}

	public void initComponents(JPanel mainPan) {

		JLabel titleLabel = new JLabel();
		titleLabel.setText("Title:");
		titleLabel.setBounds(20, 0, getWidth() - 40, 50);

		JTextField titleField = new JTextField();
		titleField.setBounds(20, 75, getWidth() - 40, 50);

		mainPan.add(titleLabel);
		mainPan.add(titleField);

		JLabel artistLabel = new JLabel();
		artistLabel.setText("Artist:");
		artistLabel.setBounds(20, 150, getWidth() - 40, 50);

		JTextField artistField = new JTextField();
		artistField.setBounds(20, 225, getWidth() - 40, 50);

		mainPan.add(artistLabel);
		mainPan.add(artistField);

		JLabel danceLabel = new JLabel();
		danceLabel.setText("Dance:");
		danceLabel.setBounds(20, 300, getWidth() - 40, 50);

		JTextField danceField = new JTextField();
		danceField.setBounds(20, 375, getWidth() - 40, 50);

		mainPan.add(danceLabel);
		mainPan.add(danceField);

		JButton addButton = new JButton();
		addButton.setText("Add");
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Main.Instance.getConfigWindow().getPlayviewgen().addSong(titleField.getText(), artistField.getText(),
						danceField.getText());
				dispose();

			}
		});
		addButton.setBounds(100, 450, getWidth() - 200, 50);
		addButton.setHorizontalAlignment(SwingConstants.CENTER);
		mainPan.add(addButton);
	}
}
