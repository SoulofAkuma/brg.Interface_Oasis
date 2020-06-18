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
import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JLayeredPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;

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


import settings.*;
import trigger.Trigger;
import trigger.TriggerHandler;
import trigger.TriggerType;
import java.awt.Rectangle;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Main extends JFrame {
	
	//All GUI classes which extend the JFrame (including the main) cannot be re initialized. To re initialize the GUI restart the program
	
	public static final String SESSIONTIME = DateTimeFormatter.ofPattern("HH_mm_ss").format(LocalDateTime.now());
	private static List<Component> settingPanels = Collections.synchronizedList(new ArrayList<Component>());

	private JPanel contentPane;
	private JScrollPane groupsPane;
	private JScrollPane triggersPane;
	private JScrollPane parsersPane;
	private JScrollPane constantsPane;
	private JScrollPane generalPane;
	private JScrollPane listenersPane;
	private JScrollPane respondersPane;
	private JScrollPane indexAssignersPane;
	private JScrollPane rulesPane;
	private JScrollPane valuesPane;
	private JPanel groupsPanel;
	private JPanel triggersPanel;
	private JPanel parsersPanel;
	private JPanel constantsPanel;
	private JPanel generalPanel;
	private JPanel listenersPanel;
	private JPanel respondersPanel;
	private JPanel indexAssignersPanel;
	private JPanel rulesPanel;
	private JPanel valuesPanel;
	
	public static Main frame;
	
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
					frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
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
		
		for (int i = 0; i < 20; i++) {
			Logger.addMessage(MessageType.Error, MessageOrigin.Parser, "Test", "000000001", null, null, false);
			Logger.addMessage(MessageType.Information, MessageOrigin.Parser, "Test", "000000001", null, null, false);
			Logger.addMessage(MessageType.Warning, MessageOrigin.Parser, "Test", "000000001", null, null, false);
		}
	}
	//TODO: Build interface for groups, which can contain Listener, Responder and Trigger. Parser are independent (makes it easier to implement the same parser for multiple groups)!
	/**
	 * Create the frame.
	 */
	public Main() {
		//Main
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 500);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
				
				//Setting panel
				JLayeredPane settingPanel = new JLayeredPane();
				settingPanel.setLayout(null);
				settingPanel.setFocusCycleRoot(true);
				settingPanel.setBackground(Color.DARK_GRAY);
				settingPanel.setBounds(0, 0, 995, 465);
				contentPane.add(settingPanel);
				
				
				//Header
				JPanel header = new JPanel();
				header.setBounds(0, 0, 995, 30);
				settingPanel.add(header);
				header.setLayout(null);
				
				JButton headerGroupButton = new JButton("Groups");
				headerGroupButton.setBounds(1, 0, 178, 30);
				header.add(headerGroupButton);
				
				JButton headerTriggerButton = new JButton("Triggers");
				headerTriggerButton.setBounds(179, 0, 179, 30);
				header.add(headerTriggerButton);
				
				JButton headerParserButton = new JButton("Parsers");
				headerParserButton.setBounds(358, 0, 179, 30);
				header.add(headerParserButton);
				
				JButton headerConstantsButton = new JButton("Constants");
				headerConstantsButton.setBounds(537, 0, 179, 30);
				header.add(headerConstantsButton);
				
				JButton headerGeneralButton = new JButton("General");
				headerGeneralButton.setBounds(716, 0, 179, 30);
				header.add(headerGeneralButton);
				
				JButton headerCloseButton = new JButton("Close");
				headerCloseButton.setBounds(895, 0, 90, 30);
				header.add(headerCloseButton);
				
				
				groupsPanel = new JPanel();
				groupsPanel.setBackground(Color.DARK_GRAY);
				
				triggersPanel = new JPanel();
				triggersPanel.setBackground(Color.DARK_GRAY);
				
				parsersPanel = new JPanel();
				parsersPanel.setBackground(Color.DARK_GRAY);
				
				constantsPanel = new JPanel();
				constantsPanel.setBackground(Color.DARK_GRAY);
				
				generalPanel = new JPanel();
				generalPanel.setBackground(Color.DARK_GRAY);
				
				listenersPanel = new JPanel();
				listenersPanel.setBackground(Color.DARK_GRAY);
				
				respondersPanel = new JPanel();
				respondersPanel.setBackground(Color.DARK_GRAY);
				
				indexAssignersPanel = new JPanel();
				indexAssignersPanel.setBackground(Color.DARK_GRAY);
				
				rulesPanel = new JPanel();
				rulesPanel.setBackground(Color.DARK_GRAY);
				
				valuesPanel = new JPanel();
				valuesPanel.setBackground(Color.DARK_GRAY);
				
				
				groupsPane = new JScrollPane(groupsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				groupsPane.setBackground(Color.DARK_GRAY);
				groupsPane.setBounds(0, 30, 986, 435);
				groupsPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(groupsPane);
				
				triggersPane = new JScrollPane(triggersPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				triggersPane.setBackground(Color.DARK_GRAY);
				triggersPane.setBounds(0, 30, 986, 435);
				triggersPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(triggersPane);
				
				parsersPane = new JScrollPane(parsersPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				parsersPane.setBackground(Color.DARK_GRAY);
				parsersPane.setBounds(0, 30, 986, 435);
				parsersPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(parsersPane);
				
				constantsPane = new JScrollPane(constantsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				constantsPane.setBackground(Color.DARK_GRAY);
				constantsPane.setBounds(0, 30, 986, 435);
				constantsPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(constantsPane);
				
				generalPane = new JScrollPane(generalPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				generalPane.setBackground(Color.DARK_GRAY);
				generalPane.setBounds(0, 30, 986, 435);
				generalPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(generalPane);
				
				listenersPane = new JScrollPane(listenersPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				listenersPane.setBackground(Color.DARK_GRAY);
				listenersPane.setBounds(0, 30, 986, 435);
				listenersPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(listenersPane);

				respondersPane = new JScrollPane(respondersPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				respondersPane.setBackground(Color.DARK_GRAY);
				respondersPane.setBounds(0, 30, 986, 435);
				respondersPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(respondersPane);

				indexAssignersPane = new JScrollPane(indexAssignersPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				indexAssignersPane.setBackground(Color.DARK_GRAY);
				indexAssignersPane.setBounds(0, 30, 986, 435);
				indexAssignersPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(indexAssignersPane);
				
				rulesPane = new JScrollPane(rulesPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				rulesPane.setBackground(Color.DARK_GRAY);
				rulesPane.setBounds(0, 30, 986, 435);
				rulesPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(rulesPane);

				valuesPane = new JScrollPane(valuesPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				valuesPane.setBackground(Color.DARK_GRAY);
				valuesPane.setBounds(0, 30, 986, 435);
				valuesPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(valuesPane);
				
				Main.settingPanels.add(groupsPane);
				Main.settingPanels.add(groupsPanel);
				groupsPanel.setLayout(null);
				groupsPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(parsersPane);
				Main.settingPanels.add(parsersPanel);
				parsersPanel.setLayout(null);
				parsersPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(constantsPane);
				Main.settingPanels.add(constantsPanel);
				constantsPanel.setLayout(null);
				constantsPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(generalPane);
				Main.settingPanels.add(generalPanel);
				generalPanel.setLayout(null);
				generalPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(triggersPane);
				Main.settingPanels.add(triggersPanel);
				triggersPanel.setLayout(null);
				triggersPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(listenersPane);
				Main.settingPanels.add(listenersPanel);
				listenersPanel.setLayout(null);
				listenersPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(respondersPane);
				Main.settingPanels.add(respondersPanel);
				respondersPanel.setLayout(null);
				respondersPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(indexAssignersPane);
				Main.settingPanels.add(indexAssignersPanel);
				indexAssignersPanel.setLayout(null);
				indexAssignersPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(rulesPane);
				Main.settingPanels.add(rulesPanel);
				rulesPanel.setLayout(null);
				rulesPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(valuesPane);
				Main.settingPanels.add(valuesPanel);
				valuesPanel.setLayout(null);
				valuesPanel.setPreferredSize(new Dimension(986, 435));
				
				//Main Button Listeners
				settingPanel.setVisible(false);
		
				
				//Main components
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
				
				JButton startLoggingConsole = new JButton("Open Console");
				startLoggingConsole.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Logger.showGUI = true;
					}
				});
				headerCloseButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						settingPanel.setVisible(false);
						mainPanel.setVisible(true);
					}
				});
				headerGroupButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						hideAllSettingPanels();
						groupsPane.setVisible(true);
						groupsPanel.setVisible(true);
					}
				});
				headerTriggerButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						hideAllSettingPanels();
						triggersPane.setVisible(true);
						triggersPanel.setVisible(true);
						populateTrigger();
					}
				});
				headerParserButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						hideAllSettingPanels();
						parsersPane.setVisible(true);
						parsersPanel.setVisible(true);
						populateParser();
					}
				});
				headerConstantsButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						hideAllSettingPanels();
						constantsPane.setVisible(true);
						constantsPanel.setVisible(true);
					}
				});
				headerGeneralButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						hideAllSettingPanels();
						generalPane.setVisible(true);
						generalPanel.setVisible(true);
					}
				});
				startLoggingConsole.setFont(new Font("Times New Roman", Font.PLAIN, 14));
				startLoggingConsole.setBorder(new EmptyBorder(1, 1, 1, 1));
				startLoggingConsole.setBackground(Color.LIGHT_GRAY);
				startLoggingConsole.setBounds(706, 424, 159, 30);
				mainPanel.add(startLoggingConsole);
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
				mainPanel.setVisible(true);
		
		//Setting header button Listeners
		btnSetup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideAllSettingPanels();
				settingPanel.setVisible(true);
				mainPanel.setVisible(false);
			}
		});
		
	}
	
	public static void hideAllSettingPanels() {
		for (Component panel : Main.settingPanels) {
			panel.setVisible(false);
		}
	}
	
	public static void hideSettingPanels() {
		for (Component panel : Main.settingPanels) {
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
	
	private void assignPos(Component component, int x, int y) {
		component.setBounds(x, y, component.getWidth(), component.getHeight());
	}
	
	private void populateTrigger() {
		while (triggersPanel.getComponentCount() > 0) {
			triggersPanel.remove(0);
		}
		int x = 10;
		int y = 10;
		List<TriggerGUIPanel> panels = TriggerHandler.getTriggerPanels();
		for (TriggerGUIPanel panel : panels) {
			assignPos(panel, x, y);
			triggersPanel.add(panel);
			y += 110;
		}
		triggersPanel.setPreferredSize(new Dimension(986, y));
		revalidate();
	}
	
	private void populateParser() {
		while (parsersPanel.getComponentCount() > 0) {
			parsersPanel.remove(0);
		}
		int x = 10;
		int y = 10;
		List<ParserGUIPanel> panels = ParserHandler.getParserPanels();
		for (ParserGUIPanel panel : panels) {
			assignPos(panel, x, y);
			parsersPanel.add(panel);
			y += 140;
		}
		parsersPanel.setPreferredSize(new Dimension(986, y));
		revalidate();
	}
	
	public static void deleteTrigger(String id) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TriggerHandler.removeTrigger(id);
				Main.frame.populateTrigger();
			}
		});
	}
	
	public static void deleteParser(String id) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ParserHandler.removeParser(id);
				Main.frame.populateParser();
			}
		});
	}
	
	public static void ruleMode() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			}
		});
	}
}
