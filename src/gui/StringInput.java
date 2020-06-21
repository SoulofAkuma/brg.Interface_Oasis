package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class StringInput extends JDialog {

	private String input;
	private static StringInput strInput;
	private JTextField inputText;
	
	public static String getSelection(Frame parent, String selecting) {
		
		strInput = new StringInput(parent, selecting);
		strInput.setVisible(true);
		return strInput.getInput();
	}
	
	private StringInput(Frame frame, String selecting) {
		super(frame, "Input " + selecting, true);
		getContentPane().setBackground(Color.DARK_GRAY);
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Input:");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(24, 20, 46, 13);
		getContentPane().add(lblNewLabel);
		
		inputText = new JTextField();
		inputText.setBounds(56, 17, 320, 19);
		getContentPane().add(inputText);
		inputText.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(291, 39, 85, 20);
		getContentPane().add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(202, 39, 85, 21);
		getContentPane().add(btnCancel);
		this.setSize(new Dimension(400, 100));
		
		getRootPane().setDefaultButton(btnSave);
		setLocationRelativeTo(frame);
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input = null;
				strInput.setVisible(false);
			}
		});
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input = inputText.getText();
				strInput.setVisible(false);
			}
		});
	}
	
	private String getInput() {
		return this.input;
	}
}
