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
		
		String input = "<?xml version=\"1.0\"?>  \r\n" + 
				"<Tests xmlns=\"http://www.adatum.com\">  \r\n" + 
				"  <Test TestId=\"0001\" TestType=\"CMD\">  \r\n" + 
				"    <Name>Convert number to string</Name>  \r\n" + 
				"    <CommandLine>Examp1.EXE</CommandLine>  \r\n" + 
				"    <Input>1</Input>  \r\n" + 
				"    <Output>One</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0002\" TestType=\"CMD\">  \r\n" + 
				"    <Name>Find succeeding characters</Name>  \r\n" + 
				"    <CommandLine>Examp2.EXE</CommandLine>  \r\n" + 
				"    <Input>abc</Input>  \r\n" + 
				"    <Output>def</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0003\" TestType=\"GUI\">  \r\n" + 
				"    <Name>Convert multiple numbers to strings</Name>  \r\n" + 
				"    <CommandLine>Examp2.EXE /Verbose</CommandLine>  \r\n" + 
				"    <Input>123</Input>  \r\n" + 
				"    <Output>One Two Three</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0004\" TestType=\"GUI\">  \r\n" + 
				"    <Name>Find correlated key</Name>  \r\n" + 
				"    <CommandLine>Examp3.EXE</CommandLine>  \r\n" + 
				"    <Input>a1</Input>  \r\n" + 
				"    <Output>b1</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0005\" TestType=\"GUI\">  \r\n" + 
				"    <Name>Count characters</Name>  \r\n" + 
				"    <CommandLine>FinalExamp.EXE</CommandLine>  \r\n" + 
				"    <Input>This is a test</Input>  \r\n" + 
				"    <Output>14</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0006\" TestType=\"GUI\">  \r\n" + 
				"    <Name>Another Test</Name>  \r\n" + 
				"    <CommandLine>Examp2.EXE</CommandLine>  \r\n" + 
				"    <Input>Test Input</Input>  \r\n" + 
				"    <Output>10</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"</Tests>  ";
		Setting bookSettings = Setting.parseSetting(input, 1);
		System.out.println(bookSettings.getXML());
		
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
