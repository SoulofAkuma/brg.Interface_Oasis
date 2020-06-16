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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.border.LineBorder;

import cc.Pair;
import cc.Shell;
import constant.Constant;
import constant.ConstantHandler;
import constant.Value;
import group.GroupHandler;
import group.listener.Listener;
import group.listener.ListenerHandler;
import group.responder.Body;
import group.responder.Header;
import group.responder.ResponderHandler;
import indexassigner.IndexAssigner;
import indexassigner.IndexAssignerHandler;
import parser.CustomParser;
import parser.Discard;
import parser.ParserHandler;
import parser.Rule;

import javax.swing.UIManager;

import settings.*;
import trigger.Trigger;
import trigger.TriggerHandler;
import trigger.TriggerType;
import javax.swing.JLayeredPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class Main extends JFrame {
	
	public static final String SESSIONTIME = DateTimeFormatter.ofPattern("HH_mm_ss").format(LocalDateTime.now());
	private static List<JPanel> settingPanels = Collections.synchronizedList(new ArrayList<JPanel>());

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
//		ArrayList<Pair<String, String>> dummy = new ArrayList<Pair<String, String>>();
//		dummy.add(new Pair<String, String>("000000030", "000000012"));
//		ArrayList<String> dummy1 = new ArrayList<String>();
//		dummy1.add("000000011");
//		Trigger trigger = new Trigger(TriggerType.Listener, dummy, "000000100", "Test", dummy1, -1);
//		TriggerHandler.addTrigger(trigger);
//		TriggerHandler.removeTrigger("000000100");
//		
//		ArrayList<Pair<String, String>> responderIDs = TriggerHandler.triggers.get("000000080").getResponderIDs();
//		responderIDs.remove(0);
		
//		GroupHandler.addGroup("TestGroup", "000000110");
//		ListenerHandler lhandler = GroupHandler.getListenerHandler("000000110");
//		lhandler.addListener(lhandler.genListener("9977", "TestListener", "000000111", false));
//		ResponderHandler rhandler = GroupHandler.getResponderHandler("000000110");
//		rhandler.addResponder(rhandler.genResponder("000000112", "TestResponder", false, new Header("GET", "000000060", null, null, new ArrayList<String>(), "000000112", "TestResponder"), new Body(new ArrayList<String>(), ",")));
//		rhandler.getResponder("000000112").setName("RealResponder");
//		rhandler.getResponder("000000112").getHeader().setRequestType("POST");
//		rhandler.getResponder("000000112").getBody().getContent().add("000000060");
//		lhandler.getListener("000000111").setName("RealListener");
//		lhandler.removeListener("000000111");
//		rhandler.removeResponder("000000112");
//		GroupHandler.removeGroup("000000110");
//		
//		ConcurrentHashMap<String, Rule> rules = new ConcurrentHashMap<String, Rule>();
//		List<Pair<Short, String>> list = new ArrayList<Pair<Short, String>>();
//		list.add(new Pair<Short, String>(Short.parseShort("1"), "Hello2"));
//		rules.put("000000114", new Discard("test", false, false, new String[] {}, false));
//		rules.put("000000115", new xmlhandler.Trace(list, "dummy"));
//		
//		ParserHandler.addParser(new CustomParser(rules, new ArrayList<String>(Arrays.asList(new String[] {"000000090"})), new ArrayList<String>(Arrays.asList(new String[] {"000000114","000000115"})), "RealParser", "000000113"));
		
//		ParserHandler.getCustomParser("000000113").setName("UmperDumper");
//		ParserHandler.getCustomParser("000000113").addRule("000000120", new xmlhandler.Trace(list, "dummy2"));
//		ParserHandler.getCustomParser("000000113").changeRulePosition("000000120", 0);
//		ParserHandler.getCustomParser("000000113").changeRulePosition("000000114", 1);
//		ParserHandler.getCustomParser("000000113").removeRule("000000114");
//		ParserHandler.removeParser("000000113");
		
//		ConcurrentHashMap<String, Value> vlist = new ConcurrentHashMap<String, Value>();
//		vlist.put("000000117", new Value("000000117", "Hello", false, false, false));
//		vlist.put("000000118", new Value("000000118", " There", false, false, false));
		
//		ConstantHandler.addConstant(new Constant("000000116", "realrealcs", new ArrayList<String>(Arrays.asList("000000117","000000118")), vlist));
		
//		ConstantHandler.getConstant("000000116").setName("UmpaLumpa");
//		ConstantHandler.getConstant("000000116").addValue("000000121", new Value("000000121", "Added", false, false, false));
//		ConstantHandler.getConstant("000000116").changeValuePosition("000000121", 0);
//		ConstantHandler.getConstant("000000116").changeValuePosition("000000117", 2);
//		ConstantHandler.getConstant("000000116").removeValue("000000117");
//		ConstantHandler.removeConstant("000000116");
		
//		ConcurrentHashMap<String, Pair<Integer, String>> indexes = new ConcurrentHashMap<String, Pair<Integer,String>>();
//		ConcurrentHashMap<String, Pair<String, String[]>> regexes = new ConcurrentHashMap<String, Pair<String,String[]>>();
//		ConcurrentHashMap<String, Integer> defInd = new ConcurrentHashMap<String, Integer>();
//		indexes.put("000000131", new Pair<Integer, String>(1,"firsti"));
//		indexes.put("000000132", new Pair<Integer, String>(2,"secondi"));
//		indexes.put("000000133", new Pair<Integer, String>(3,"thirdi"));
//		regexes.put("000000134", new Pair<String, String[]>("[0-9]+", new String[] {"1","2"}));
//		regexes.put("000000135", new Pair<String, String[]>("[a-z]+", new String[] {"3","4"}));
//		regexes.put("000000136", new Pair<String, String[]>("[A-Z]+", new String[] {"5","6"}));
//		defInd.put("000000134", 1);
//		defInd.put("000000135", 2);
//		defInd.put("000000136", 3);
//		
//		IndexAssignerHandler.addIndexAssigner(new IndexAssigner("000000130", "Shit", indexes, regexes, defInd, false, new ArrayList<String>(Arrays.asList(new String[] {"000000131","000000132","000000133"})), new ArrayList<String>(Arrays.asList(new String[] {"000000134","000000135","000000136"}))));
//		IndexAssignerHandler.getIndexAsssigner("000000130").setName("Shitty");
//		IndexAssignerHandler.getIndexAsssigner("000000130").changeIndexPosition("000000131", 2);
//		IndexAssignerHandler.getIndexAsssigner("000000130").changeRegexPosition("000000134", 0);
//		IndexAssignerHandler.getIndexAsssigner("000000130").addIndex("000000137", 4, "fourthi");
//		IndexAssignerHandler.getIndexAsssigner("000000130").addRegex("000000138", "[0-9a-zA-Z]+", new String[] {"7", "8"}, 0);
//		IndexAssignerHandler.getIndexAsssigner("000000130").removeIndex("000000137");
//		IndexAssignerHandler.getIndexAsssigner("000000130").removeRegex("000000138");
//		IndexAssignerHandler.removeIndexAssigner("000000130");
		
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
		
		JPanel settingPanel = new JPanel();
		settingPanel.setLayout(null);
		settingPanel.setFocusCycleRoot(true);
		settingPanel.setBackground(Color.DARK_GRAY);
		settingPanel.setBounds(0, 0, 995, 465);
		contentPane.add(settingPanel);
		
		JPanel header = new JPanel();
		header.setBounds(0, 0, 995, 30);
		settingPanel.add(header);
		header.setLayout(null);
		
		JButton headerGroupButton = new JButton("Groups");
		headerGroupButton.setBounds(0, 0, 179, 30);
		header.add(headerGroupButton);
		
		JButton headerTriggerButton = new JButton("Triggers");
		headerTriggerButton.setBounds(179, 0, 179, 30);
		header.add(headerTriggerButton);
		
		JButton headerParserButton = new JButton("IndexAssigners");
		headerParserButton.setBounds(358, 0, 179, 30);
		header.add(headerParserButton);
		
		JButton headerConstantsButton = new JButton("Constants");
		headerConstantsButton.setBounds(537, 0, 179, 30);
		header.add(headerConstantsButton);
		
		JButton headerGeneralButton = new JButton("General");
		headerGeneralButton.setBounds(716, 0, 179, 30);
		header.add(headerGeneralButton);
		
		JButton headerCloseButton = new JButton("Close");
		headerCloseButton.setBounds(895, 0, 100, 30);
		header.add(headerCloseButton);
		
		JPanel groupsPanel = new JPanel();
		groupsPanel.setBackground(Color.DARK_GRAY);
		groupsPanel.setBounds(0, 30, 995, 435);
		settingPanel.add(groupsPanel);
		groupsPanel.setLayout(null);
		
		JPanel triggersPanel = new JPanel();
		triggersPanel.setBackground(Color.DARK_GRAY);
		triggersPanel.setBounds(0, 30, 995, 435);
		settingPanel.add(triggersPanel);
		triggersPanel.setLayout(null);
		
		JPanel indexAssignersPanel = new JPanel();
		indexAssignersPanel.setBackground(Color.DARK_GRAY);
		indexAssignersPanel.setBounds(0, 30, 995, 435);
		settingPanel.add(indexAssignersPanel);
		indexAssignersPanel.setLayout(null);
		
		JPanel constantsPanel = new JPanel();
		constantsPanel.setBackground(Color.DARK_GRAY);
		constantsPanel.setBounds(0, 30, 995, 435);
		settingPanel.add(constantsPanel);
		constantsPanel.setLayout(null);
		
		JPanel generalPanel = new JPanel();
		generalPanel.setBounds(0, 30, 995, 435);
		settingPanel.add(generalPanel);
		
		Main.settingPanels.add(groupsPanel);
		Main.settingPanels.add(triggersPanel);
		Main.settingPanels.add(indexAssignersPanel);
		Main.settingPanels.add(constantsPanel);
		Main.settingPanels.add(generalPanel);
		
		settingPanel.setVisible(false);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setFocusCycleRoot(true);
		mainPanel.setBackground(Color.DARK_GRAY);
		mainPanel.setBounds(0, 0, 995, 465);
		contentPane.add(mainPanel);
		mainPanel.setLayout(null);
		
		JButton StartButton = new JButton("Launch Oasis");
		StartButton.setBorder(new LineBorder(Color.BLACK));
		StartButton.setBackground(new Color(169, 169, 169));
		StartButton.setForeground(new Color(0, 128, 128));
		StartButton.setBounds(397, 197, 200, 50);
		StartButton.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		StartButton.setMargin(new Insets(5, 5, 5, 5));
		mainPanel.add(StartButton);
		
		JButton btnSetup = new JButton("Setup");
		btnSetup.setBorder(new EmptyBorder(1, 1, 1, 1));
		btnSetup.setBackground(Color.LIGHT_GRAY);
		btnSetup.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btnSetup.setBounds(875, 424, 100, 30);
		mainPanel.add(btnSetup);
		
		
		headerCloseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingPanel.setVisible(false);
				mainPanel.setVisible(true);
			}
		});
		StartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (LaunchIDS.isRunning()) {
					LaunchIDS.stopAll();
					StartButton.setText("Launch Oasis");
				} else {
					LaunchIDS.startAll();
					StartButton.setText("Stop Oasis");
				}
			}
		});
		btnSetup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingPanel.setVisible(true);
				mainPanel.setVisible(false);
			}
		});
	}
	
	public static void hideSettingPanels() {
		for (JPanel panel : Main.settingPanels) {
			panel.setVisible(false);
		}
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
