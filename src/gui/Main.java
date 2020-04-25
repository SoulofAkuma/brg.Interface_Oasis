package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import settings.*;

public class Main extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		SettingHandler.handle();
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			System.out.println("Error");			
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel MainPanel = new JPanel();
		MainPanel.setFocusCycleRoot(true);
		MainPanel.setBackground(Color.DARK_GRAY);
		MainPanel.setBounds(0, 0, 995, 465);
		contentPane.add(MainPanel);
		MainPanel.setLayout(null);
		
		JButton StartButton = new JButton("Launch Oasis");
		StartButton.setBorder(new LineBorder(Color.BLACK));
		StartButton.setBackground(new Color(169, 169, 169));
		StartButton.setForeground(new Color(0, 128, 128));
		StartButton.setBounds(397, 197, 200, 50);
		StartButton.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		StartButton.setMargin(new Insets(5, 5, 5, 5));
		MainPanel.add(StartButton);
		
		JButton btnSetup = new JButton("Setup");
		btnSetup.setBorder(new EmptyBorder(1, 1, 1, 1));
		btnSetup.setBackground(Color.LIGHT_GRAY);
		btnSetup.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btnSetup.setBounds(875, 424, 100, 30);
		MainPanel.add(btnSetup);
	}
}
