package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
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
import group.responder.Responder;
import group.responder.ResponderHandler;
import indexassigner.IndexAssigner;
import indexassigner.IndexAssignerHandler;
import parser.CustomParser;
import parser.ParserHandler;
import parser.Rule;
import settings.IDType;
import settings.SettingHandler;
import trigger.Trigger;
import trigger.TriggerHandler;
import trigger.TriggerType;

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
	public static final String versionNumber = "1.0";
	private JLabel lblNewLabel;
	private JLabel label;
	private JList listenersList;
	private JList triggersList;
	private JButton addListener;
	private JButton removeListener;
	private JButton addTrigger;
	private JButton removeTrigger;
	
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
				
				rulesPanel = new JPanel();
				rulesPanel.setBackground(Color.DARK_GRAY);
				
				rulesPane = new JScrollPane(rulesPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				rulesPane.setBackground(Color.DARK_GRAY);
				rulesPane.setBounds(0, 30, 986, 435);
				rulesPane.getVerticalScrollBar().setUnitIncrement(10);
				
				listenersPanel = new JPanel();
				listenersPanel.setBackground(Color.DARK_GRAY);
				
				listenersPane = new JScrollPane(listenersPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				listenersPane.setBackground(Color.DARK_GRAY);
				listenersPane.setBounds(0, 30, 986, 435);
				listenersPane.getVerticalScrollBar().setUnitIncrement(10);
				
				respondersPanel = new JPanel();
				respondersPanel.setBackground(Color.DARK_GRAY);
				
								respondersPane = new JScrollPane(respondersPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
								respondersPane.setBackground(Color.DARK_GRAY);
								respondersPane.setBounds(0, 30, 986, 435);
								respondersPane.getVerticalScrollBar().setUnitIncrement(10);
								
								generalPanel = new JPanel();
								generalPanel.setBackground(Color.DARK_GRAY);
								
								generalPane = new JScrollPane(generalPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
								generalPane.setBackground(Color.DARK_GRAY);
								generalPane.setBounds(0, 30, 986, 435);
								generalPane.getVerticalScrollBar().setUnitIncrement(10);
								settingPanel.add(generalPane);
								Main.settingPanels.add(generalPane);
								Main.settingPanels.add(generalPanel);
								generalPanel.setLayout(null);
								generalPanel.setPreferredSize(new Dimension(986, 435));
								
								lblNewLabel = new JLabel("Listeners");
								lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
								lblNewLabel.setForeground(Color.WHITE);
								lblNewLabel.setBounds(10, 10, 83, 21);
								generalPanel.add(lblNewLabel);
								
								label = new JLabel("Triggers");
								label.setForeground(Color.WHITE);
								label.setFont(new Font("Tahoma", Font.PLAIN, 14));
								label.setBounds(478, 10, 83, 21);
								generalPanel.add(label);
								
								JScrollPane listenersScroll = new JScrollPane();
								listenersScroll.setBorder(null);
								listenersScroll.setBounds(10, 33, 450, 361);
								generalPanel.add(listenersScroll);
								
												listenersList = new JList();
												listenersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
												listenersList.setForeground(Color.WHITE);
												listenersList.setBackground(Color.GRAY);
												listenersList.setModel(new DefaultListModel<ListElement>());
												listenersScroll.setViewportView(listenersList);
												
												JScrollPane triggersScroll = new JScrollPane();
												triggersScroll.setBorder(null);
												triggersScroll.setBounds(478, 33, 450, 361);
												generalPanel.add(triggersScroll);
												
																triggersList = new JList();
																triggersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
																triggersList.setForeground(Color.WHITE);
																triggersList.setBackground(Color.GRAY);
																triggersList.setModel(new DefaultListModel<ListElement>());
																triggersScroll.setViewportView(triggersList);
																
																addListener = new JButton("Add Listener");
																addListener.setBounds(10, 404, 130, 21);
																generalPanel.add(addListener);
																
																removeListener = new JButton("Remove Listener");
																removeListener.setBounds(150, 404, 130, 21);
																generalPanel.add(removeListener);
																
																addTrigger = new JButton("Add Trigger");
																addTrigger.setBounds(478, 404, 130, 21);
																generalPanel.add(addTrigger);
																
																removeTrigger = new JButton("Remove Trigger");
																removeTrigger.setBounds(618, 404, 130, 21);
																generalPanel.add(removeTrigger);
																
																addListener.addActionListener(new ActionListener() {
																	@Override
																	public void actionPerformed(ActionEvent e) {
																		ListElement selection = ParamSelector.getSelection(Main.frame, GroupHandler.getListenerElements(), "Listener");
																		if (selection != null) {
																			if (LaunchIDS.getListenerIDs().contains(selection.getID())) {
																				Main.popupMessage("Error - Duplicate LaunchIDs are forbidden");
																				return;
																			}
																			LaunchIDS.getListenerIDs().add(selection.getID());
																			populateGeneral();
																		}
																	}
																});
																
																addTrigger.addActionListener(new ActionListener() {
																	@Override
																	public void actionPerformed(ActionEvent e) {
																		ListElement selection = ParamSelector.getSelection(Main.frame, TriggerHandler.getTriggerElements(), "Trigger");
																		if (selection != null) {
																			if (LaunchIDS.getTriggerIDs().contains(selection.getID())) {
																				Main.popupMessage("Error - Duplicate LaunchIDs are forbidden");
																				return;
																			}
																			LaunchIDS.getTriggerIDs().add(selection.getID());
																			populateGeneral();
																		}
																	}
																});
																
																removeListener.addActionListener(new ActionListener() {
																	@Override
																	public void actionPerformed(ActionEvent e) {
																		if (listenersList.getSelectedIndex() != -1) {
																			LaunchIDS.getListenerIDs().remove(listenersList.getSelectedIndex());
																			populateGeneral();
																		}
																	}
																});
																
																removeTrigger.addActionListener(new ActionListener() {
																	@Override
																	public void actionPerformed(ActionEvent e) {
																		if (triggersList.getSelectedIndex() != -1) {
																			LaunchIDS.getTriggerIDs().remove(triggersList.getSelectedIndex());
																			populateGeneral();
																		}
																	}
																});
								settingPanel.add(respondersPane);
								Main.settingPanels.add(respondersPane);
								Main.settingPanels.add(respondersPanel);
								respondersPanel.setLayout(null);
								respondersPanel.setPreferredSize(new Dimension(986, 435));
				
				settingPanel.add(listenersPane);
				Main.settingPanels.add(listenersPane);
				Main.settingPanels.add(listenersPanel);
				listenersPanel.setLayout(null);
				listenersPanel.setPreferredSize(new Dimension(986, 435));
				settingPanel.add(rulesPane);
				Main.settingPanels.add(rulesPane);
				Main.settingPanels.add(rulesPanel);
				rulesPanel.setLayout(null);
				rulesPanel.setPreferredSize(new Dimension(986, 435));
				
				
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
				
				indexAssignersPanel = new JPanel();
				indexAssignersPanel.setBackground(Color.DARK_GRAY);
				
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

				indexAssignersPane = new JScrollPane(indexAssignersPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				indexAssignersPane.setBackground(Color.DARK_GRAY);
				indexAssignersPane.setBounds(0, 30, 986, 435);
				indexAssignersPane.getVerticalScrollBar().setUnitIncrement(10);
				settingPanel.add(indexAssignersPane);

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
				Main.settingPanels.add(triggersPane);
				Main.settingPanels.add(triggersPanel);
				triggersPanel.setLayout(null);
				triggersPanel.setPreferredSize(new Dimension(986, 435));
				Main.settingPanels.add(indexAssignersPane);
				Main.settingPanels.add(indexAssignersPanel);
				indexAssignersPanel.setLayout(null);
				indexAssignersPanel.setPreferredSize(new Dimension(986, 435));
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
						populateGroups();
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
						populateConstants();
					}
				});
				headerGeneralButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						hideAllSettingPanels();
						generalPane.setVisible(true);
						generalPanel.setVisible(true);
						populateGeneral();
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
	
	private static void assignPos(Component component, int x, int y) {
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
		JButton addTrigger = new JButton("Add Trigger");
		addTrigger.setBounds(x + 5, y + 10, 100, 20);
		triggersPanel.add(addTrigger);
		addTrigger.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TriggerHandler.addTrigger(new Trigger(TriggerType.Manual, Collections.synchronizedList(new ArrayList<Pair<String, String>>()), SettingHandler.getNewID(IDType.Trigger), "New Trigger", Collections.synchronizedList(new ArrayList<String>()), 0));
				populateTrigger();
			}
		});
		y += 40;
		triggersPanel.setPreferredSize(new Dimension(986, y));
		revalidate();
		repaint();
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
		JButton addParser = new JButton("Add Parser");
		addParser.setBounds(x + 5, y + 10, 100, 20);
		parsersPanel.add(addParser);
		addParser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (IndexAssignerHandler.getRIDIfExists() != null) {
					ParserHandler.addParser(new CustomParser(new ConcurrentHashMap<String, Rule>(), Collections.synchronizedList(new ArrayList<String>(Arrays.asList(new String[] {IndexAssignerHandler.getRIDIfExists()}))), Collections.synchronizedList(new ArrayList<String>()), "New Parser", SettingHandler.getNewID(IDType.Trigger)));
					populateParser();
				} else {
					System.out.println("error");
					JOptionPane.showMessageDialog(null, "Error - You need at least one IndexAssigner before adding a parser");
				}
			}
		});
		JButton manageAssigners = new JButton("Manage IndexAssigners");
		manageAssigners.setBounds(x + 115, y + 10, 150, 20);
		parsersPanel.add(manageAssigners);
		manageAssigners.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.frame.assignersMode();
			}
		});
		y += 40;
		
		
		parsersPanel.setPreferredSize(new Dimension(986, y));
		revalidate();
		repaint();
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
	
	public void ruleMode(ConcurrentHashMap<String, Rule> rules, List<String> order, ParserGUIPanel parent) {
		while (rulesPanel.getComponentCount() > 0) {
			rulesPanel.remove(0);
		}
		int x = 10;
		int y = 10;
		for (String ruleID : order) {
			RuleGUIPanel panel = new RuleGUIPanel();
			int height = panel.init(rules.get(ruleID), ruleID, parent);
			assignPos(panel, x, y);
			rulesPanel.add(panel);
			y += height;
		}
		JButton addRule = new JButton("Add Rule");
		addRule.setBounds(x + 5, y + 10, 80, 20);
		rulesPanel.add(addRule);
		y += 40;
		addRule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ListElement[] types = new ListElement[8];
				types[0] = new ListElement("parser.AddHeaderVal", "AddHeaderVal");
				types[1] = new ListElement("parser.Cut", "Cut");
				types[2] = new ListElement("parser.Discard", "Discard");
				types[3] = new ListElement("parser.Isolate", "Isolate");
				types[4] = new ListElement("parser.Replace", "Replace");
				types[5] = new ListElement("parser.Split", "Split");
				types[6] = new ListElement("xmlhandler.Trace", "XMLTrace");
				types[7] = new ListElement("jsonhandler.Trace", "JSONTrace");
				ListElement selection = ParamSelector.getSelection(Main.frame, types, "Rule Type");
				if (selection != null) {
					parent.addRule(selection.getID());
				}
			}
		});
		rulesPanel.setPreferredSize(new Dimension(986, y));
		rulesPane.setVisible(true);
		rulesPanel.setVisible(true);
		parsersPanel.setVisible(false);
		parsersPane.setVisible(false);
		revalidate();
		repaint();
	}
	
	public void assignersMode() {
		while (indexAssignersPanel.getComponentCount() > 0) {
			indexAssignersPanel.remove(0);
		}
		int x = 10;
		int y = 10;
		List<IndexAssignerGUIPanel> panels = IndexAssignerHandler.getAssignerPanels();
		for (IndexAssignerGUIPanel panel : panels) {
			assignPos(panel, x, y);
			indexAssignersPanel.add(panel);
			y += 110;
		}
		JButton addAssigner = new JButton("Add IndexAssigner");
		addAssigner.setBounds(x + 5, y + 10, 150, 20);
		indexAssignersPanel.add(addAssigner);
		addAssigner.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IndexAssignerHandler.addIndexAssigner(new IndexAssigner(SettingHandler.getNewID(IDType.IndexAssigner), "New Assigner", new ConcurrentHashMap<String, Pair<Integer, String>>(), new ConcurrentHashMap<String, Pair<String, String[]>>(), new ConcurrentHashMap<String, Integer>(), false, Collections.synchronizedList(new ArrayList<String>()), Collections.synchronizedList(new ArrayList<String>()))); 
				assignersMode();
			}
		});
		Main.frame.indexAssignersPane.setVisible(true);
		Main.frame.indexAssignersPanel.setVisible(true);
		Main.frame.parsersPane.setVisible(false);
		Main.frame.parsersPanel.setVisible(false);
		revalidate();
		repaint();
	}
	
	public void populateGeneral() {
		
		DefaultListModel<ListElement> listenersModel = (DefaultListModel<ListElement>) listenersList.getModel();
		DefaultListModel<ListElement> triggersModel = (DefaultListModel<ListElement>) triggersList.getModel();
		HashMap<String, String> listenerNames = GroupHandler.getListenerNames();
		HashMap<String, String> triggerNames = TriggerHandler.getTriggerNames();
		
		listenersModel.clear();
		triggersModel.clear();
		
		for (String listenerID : LaunchIDS.getListenerIDs()) {
			listenersModel.addElement(new ListElement(listenerID, listenerNames.get(listenerID)));
		}
		
		for (String triggerID : LaunchIDS.getTriggerIDs()) {
			triggersModel.addElement(new ListElement(triggerID, triggerNames.get(triggerID)));
		}
		
		if (LaunchIDS.isRunning()) {
			addTrigger.setEnabled(false);
			addListener.setEnabled(false);
			removeTrigger.setEnabled(false);
			removeListener.setEnabled(false);
		} else {
			addTrigger.setEnabled(true);
			addListener.setEnabled(true);
			removeTrigger.setEnabled(true);
			removeListener.setEnabled(true);
		}
	}
	
	public void populateConstants() {
		while (constantsPanel.getComponentCount() > 0) {
			constantsPanel.remove(0);
		}
		int x = 10;
		int y = 10;
		List<ConstantGUIPanel> panels = ConstantHandler.getConstantPanels();
		for (ConstantGUIPanel panel : panels) {
			assignPos(panel, x, y);
			constantsPanel.add(panel);
			y += 100;
		}
		JButton addConstant = new JButton("Add Constant");
		addConstant.setBounds(x + 5, y + 10, 100, 20);
		constantsPanel.add(addConstant);
		addConstant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConstantHandler.addConstant(new Constant(SettingHandler.getNewID(IDType.Constant), "New Constant", Collections.synchronizedList(new ArrayList<String>()), new ConcurrentHashMap<String, Value>()));
				populateConstants();
			}
		});
		y += 40;
		constantsPanel.setPreferredSize(new Dimension(986, y));
		revalidate();
		repaint();
	}
	
	public void valuesMode(ConcurrentHashMap<String, Value> values, List<String> order, ConstantGUIPanel parent) {
		while (valuesPanel.getComponentCount() > 0) {
			valuesPanel.remove(0);
		}
		int x  = 10;
		int y = 10;
		for (String valueID : order) {
			ValueGUIPanel panel = new ValueGUIPanel();
			panel.init(values.get(valueID), parent);
			assignPos(panel, x, y);
			valuesPanel.add(panel);
			y += 34;
		}
		JButton addValue = new JButton("Add Value");
		addValue.setBounds(x + 5, y + 10, 80, 20);
		valuesPanel.add(addValue);
		y += 40;
		addValue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.addValue(new Value(SettingHandler.getNewID(IDType.Value), "", false, false, false));
			}
		});
		Main.frame.constantsPane.setVisible(false);
		Main.frame.constantsPanel.setVisible(false);
		Main.frame.valuesPane.setVisible(true);
		Main.frame.valuesPanel.setVisible(true);
		revalidate();
		repaint();
	}
	
	public void populateGroups() {
		while (groupsPanel.getComponentCount() > 0) {
			groupsPanel.remove(0);
		}
		int x = 10;
		int y = 10;
		List<GroupGUIPanel> panels = GroupHandler.getGroupPanels();
		for (GroupGUIPanel panel : panels) {
			assignPos(panel, x, y);
			groupsPanel.add(panel);
			y += 100;
		}
		JButton addGroup = new JButton("Add Group");
		addGroup.setBounds(x + 5, y + 10, 100, 20);
		groupsPanel.add(addGroup);
		addGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GroupHandler.addGroup("New Group", SettingHandler.getNewID(IDType.Group));
				populateGroups();
			}
		});
		y += 40;
		groupsPanel.setPreferredSize(new Dimension(986, y));
		revalidate();
		repaint();
	}
	
	public void listenersMode(ListenerHandler handler) {
		while (listenersPanel.getComponentCount() > 0) {
			listenersPanel.remove(0);
		}
		int x = 10;
		int y = 10;
		List<ListenerGUIPanel> panels = handler.getListenerPanels();
		for (ListenerGUIPanel panel : panels) {
			assignPos(panel, x, y);
			listenersPanel.add(panel);
			y += 34;
		}
		JButton addListener = new JButton("Add Listener");
		addListener.setBounds(x + 5, y + 10, 150, 20);
		listenersPanel.add(addListener);
		y += 40;
		addListener.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handler.addListener(new Listener("80", "New Listener", handler.getGroupID(), handler.getGroupName(), SettingHandler.getNewID(IDType.Listener), false));
				Main.frame.listenersMode(handler);
			}
		});
		listenersPanel.setPreferredSize(new Dimension(986, y));
		listenersPanel.setVisible(true);
		listenersPane.setVisible(true);
		respondersPanel.setVisible(false);
		respondersPane.setVisible(false);
		groupsPanel.setVisible(false);
		groupsPanel.setVisible(false);
		revalidate();
		repaint();
	}
	public void respondersMode(ResponderHandler handler) {
		while (respondersPanel.getComponentCount() > 0) {
			respondersPanel.remove(0);
		}
		int x = 10;
		int y = 10;
		List<ResponderGUIPanel> panels = handler.getResponderPanels();
		for (ResponderGUIPanel panel : panels) {
			assignPos(panel, x, y);
			respondersPanel.add(panel);
			y += 210;
		}
		JButton addResponder = new JButton("Add Responder");
		addResponder.setBounds(x + 5, y + 10, 150, 20);
		respondersPanel.add(addResponder);
		y += 40;
		addResponder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String url = ConstantHandler.getRConstant();
				if (url != null) {
					String id = SettingHandler.getNewID(IDType.Responder);
				handler.addResponder(new Responder(id, "New Responder", false, handler.getGroupID(), handler.getGroupName(), new Header("auto", url, "", "", Collections.synchronizedList(new ArrayList<String>()), id, "New Responder"), new Body(Collections.synchronizedList(new ArrayList<String>()), "")));
				Main.frame.respondersMode(handler);
				} else {
					Main.popupMessage("Error - you need at least one constant to represent a url");
				}
			}
		});
		respondersPanel.setPreferredSize(new Dimension(986, y));
		respondersPanel.setVisible(true);
		respondersPane.setVisible(true);
		listenersPanel.setVisible(false);
		listenersPane.setVisible(false);
		groupsPanel.setVisible(false);
		groupsPanel.setVisible(false);
		revalidate();
		repaint();
	}
}
