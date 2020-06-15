package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.border.LineBorder;

import cc.Pair;
import cc.Shell;
import group.GroupHandler;

import javax.swing.UIManager;

import settings.*;
import trigger.Trigger;
import trigger.TriggerHandler;
import trigger.TriggerType;

@SuppressWarnings("serial")
public class Main extends JFrame {
	
	public static final String SESSIONTIME = DateTimeFormatter.ofPattern("HH_mm_ss").format(LocalDateTime.now());

	private JPanel contentPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		Logger.init();
		

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			Logger.reportException(Main.class.getName(), "main", e);			
		}
		
		SettingHandler.init();

		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				SettingHandler.close();
			}
		});
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					Logger.reportException("Main", "main", e);
				}
			}
		});
		ArrayList<Pair<String, String>> dummy = new ArrayList<Pair<String, String>>();
		dummy.add(new Pair<String, String>("000000030", "000000012"));
		ArrayList<String> dummy1 = new ArrayList<String>();
		dummy1.add("000000011");
		Trigger trigger = new Trigger(TriggerType.Listener, dummy, "000000100", "Test", dummy1, -1);
		TriggerHandler.addTrigger(trigger);
		
		GroupHandler.getListenerHandler(GroupHandler.ltgID("000000011")).runListener("000000011");
		TriggerHandler.runTrigger("000000080");
		
	}
	//TODO: Build interface for groups, which can contain Listener, Responder and Trigger. Parser are independent (makes it easier to implement the same parser for multiple groups)!
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
	
	public static void fatalError(String message) {
		
	}
	
	public static void popupMessage(String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, message);
			}
		});
	}
	
	public static int askMessage(String question, String title) {
		final Shell<Integer> returnVal = new Shell<Integer>();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					int test = JOptionPane.showConfirmDialog(null, question, title, JOptionPane.YES_NO_OPTION);
					returnVal.setValue(test);
				}
			});
		} catch (Exception e) {
			Logger.reportException("Main", "askMessage", e);
		}
		return returnVal.getValue();
	}
}
