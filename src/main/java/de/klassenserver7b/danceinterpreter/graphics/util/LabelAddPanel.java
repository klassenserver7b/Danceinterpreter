package de.klassenserver7b.danceinterpreter.graphics.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.klassenserver7b.danceinterpreter.Main;

public class LabelAddPanel extends JFrame {

	private static final long serialVersionUID = 9007970982830756365L;

	public LabelAddPanel() {
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

		JLabel textLabel = new JLabel();
		textLabel.setText("Text:");
		textLabel.setBounds(20, 0, getWidth() - 40, 50);
		JTextField textField = new JTextField();
		textField.setBounds(20, 75, getWidth() - 40, 50);

		add(textLabel);
		add(textField);

		JCheckBox boldCheck = new JCheckBox();
		boldCheck.setText("Bold");
		boldCheck.setBounds(20, 150, getWidth() - 40, 50);

		JCheckBox italicCheck = new JCheckBox();
		italicCheck.setText("Italic");
		italicCheck.setBounds(20, 225, getWidth() - 40, 50);

		add(boldCheck);
		add(italicCheck);

		JButton addButton = new JButton();
		addButton.setText("Add");
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String s = "";
				if (boldCheck.isSelected()) {
					s += "b";
				}
				if (italicCheck.isSelected()) {
					s += "i";
				}

				Main.Instance.getConfigWindow().getPlayviewgen().addLabel(textField.getText(), s);
				dispose();

			}
		});

		addButton.setBounds(100, 450, getWidth() - 200, 50);
		addButton.setHorizontalAlignment(SwingConstants.CENTER);
		mainPan.add(addButton);
	}
}
