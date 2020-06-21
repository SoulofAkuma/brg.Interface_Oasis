package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cc.Pair;
import parser.AddHeaderVal;
import parser.Cut;
import parser.Discard;
import parser.Isolate;
import parser.Replace;
import parser.Rule;
import parser.Split;
import settings.SettingHandler;

public class RuleGUIPanel extends JPanel {
	
	private JPanel addHeaderValPanel;
	private JPanel cutPanel;
	private JPanel discardPanel;
	private JPanel isolatePanel;
	private JPanel replacePanel;
	private JPanel splitPanel;
	private JPanel xmlTracePanel;
	private JPanel jsonTracePanel;
	
	private int height = 0;
	private Rule rule;
	private String ruleID;
	private ParserGUIPanel parent;
	
	private List<Component> rulePanels = Collections.synchronizedList(new ArrayList<Component>());
	private JTextField ahvFind;
	private JTextField cutFind;
	private JButton cutSaveFind;
	private JButton cutResetFind;
	private JTextField discardFind;
	private JButton discardSaveFind;
	private JButton discardResetFind;
	private JCheckBox discardRegex;
	private JCheckBox discardInverted;
	private JCheckBox discardUseHeader;
	private JButton discardDelete;
	private JTextField isolateFind;
	private JButton isolateSaveFind;
	private JButton isolateResetFind;
	private JCheckBox isolateUseHeader;
	private JButton isolateDelete;
	private JTextField replaceFind;
	private JButton replaceSaveFind;
	private JButton replaceResetFind;
	private JButton replaceDelete;
	private JTextField replaceReplace;
	private JButton replaceSaveReplace;
	private JButton replaceResetReplace;
	private JTextField splitFind;
	private JButton splitSaveFind;
	private JButton splitResetFind;
	private JSpinner splitN;
	private JCheckBox splitRegex;
	private JCheckBox splitUseHeader;
	private JButton splitDelete;
	private JTextField xmlTraceDef;
	private JTextField jsonTraceDef;
	private JButton jsonTraceSaveDef;
	private JButton jsonTraceResetDef;
	private JButton jsonTraceAddElement;
	private JButton jsonTraceRemoveElement;
	private JButton jsonTraceUp;
	private JButton jsonTraceDown;
	private JList jsonTracePath;
	private JButton xmlTraceSaveDef;
	private JButton xmlTraceResetDef;
	private JScrollPane scrollPane;
	private JList xmlTraceNodes;
	private JButton xmlTraceAddNode;
	private JButton xmlTraceRemoveNode;
	private JButton xmlTraceUp;
	private JButton xmlTraceDown;
	private JSpinner cutN;
	private JCheckBox cutRegex;
	private JCheckBox cutKeep;
	private JCheckBox cutReEval;
	private JCheckBox cutUseHeader;
	private JButton cutDelete;
	private JCheckBox discardS;
	private JCheckBox discardP;
	private JCheckBox discardA;
	private JCheckBox discardZ;
	private JButton ahvSaveFind;
	private JButton ahvResetFind;
	private JButton ahvDelete;
	private JCheckBox replaceRegex;
	private JCheckBox replaceUseHeader;
	private JButton jsonTraceDelete;
	private JButton xmlTraceDelete;
	
	public RuleGUIPanel() {
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		setBounds(new Rectangle(0, 0, 945, 100));
		
		splitPanel = new JPanel();
		splitPanel.setBackground(Color.DARK_GRAY);
		splitPanel.setBounds(0, 0, 945, 34);
		add(splitPanel);
		splitPanel.setLayout(null);
		rulePanels.add(splitPanel);
		
		JLabel label_9 = new JLabel("Split");
		label_9.setForeground(Color.WHITE);
		label_9.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_9.setBounds(10, 8, 31, 13);
		splitPanel.add(label_9);
		
		JLabel label_10 = new JLabel("Find:");
		label_10.setForeground(Color.WHITE);
		label_10.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_10.setBounds(51, 8, 30, 13);
		splitPanel.add(label_10);
		
		splitFind = new JTextField();
		splitFind.setColumns(10);
		splitFind.setBounds(77, 6, 96, 19);
		splitPanel.add(splitFind);
		
		splitSaveFind = new JButton("Save");
		splitSaveFind.setBounds(183, 6, 85, 21);
		splitPanel.add(splitSaveFind);
		
		splitResetFind = new JButton("Reset");
		splitResetFind.setBounds(278, 6, 85, 21);
		splitPanel.add(splitResetFind);
		
		JLabel label_11 = new JLabel("n:");
		label_11.setForeground(Color.WHITE);
		label_11.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_11.setBounds(373, 8, 21, 13);
		splitPanel.add(label_11);
		
		splitN = new JSpinner();
		splitN.setBounds(383, 6, 30, 20);
		splitPanel.add(splitN);
		
		splitRegex = new JCheckBox("Regex");
		splitRegex.setForeground(Color.WHITE);
		splitRegex.setBackground(Color.DARK_GRAY);
		splitRegex.setBounds(419, 6, 65, 21);
		splitPanel.add(splitRegex);
		
		splitUseHeader = new JCheckBox("Use Header");
		splitUseHeader.setForeground(Color.WHITE);
		splitUseHeader.setBackground(Color.DARK_GRAY);
		splitUseHeader.setBounds(486, 6, 96, 21);
		splitPanel.add(splitUseHeader);
		
		splitDelete = new JButton("Delete Rule");
		splitDelete.setBounds(798, 6, 137, 21);
		splitPanel.add(splitDelete);
		
		splitSaveFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Split split = (Split) rule;
				split.setFind(splitFind.getText());
			}
		});
		
		splitResetFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Split split = (Split) rule;
				splitFind.setText(split.getFind());
			}
		});
		
		splitRegex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Split split = (Split) rule;
				split.setRegex(splitRegex.isSelected());
			}
		});
		
		splitUseHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Split split = (Split) rule;
				split.setUseHeader(splitUseHeader.isSelected());
			}
		});
		
		splitN.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Split split = (Split) rule;
				split.setN((Integer) splitN.getValue());
			}
		});
		
		splitDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.removeRule(ruleID);
			}
		});
		
		xmlTracePanel = new JPanel();
		xmlTracePanel.setBackground(Color.DARK_GRAY);
		xmlTracePanel.setBounds(0, 0, 945, 100);
		add(xmlTracePanel);
		xmlTracePanel.setLayout(null);
		rulePanels.add(xmlTracePanel);
		
		JLabel lblNewLabel_2 = new JLabel("XMLTrace");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setBounds(10, 10, 54, 13);
		xmlTracePanel.add(lblNewLabel_2);
		
		xmlTraceDef = new JTextField();
		xmlTraceDef.setColumns(10);
		xmlTraceDef.setBounds(10, 33, 180, 19);
		xmlTracePanel.add(xmlTraceDef);
		
		xmlTraceSaveDef = new JButton("Save");
		xmlTraceSaveDef.setBounds(10, 62, 85, 21);
		xmlTracePanel.add(xmlTraceSaveDef);
		
		xmlTraceResetDef = new JButton("Reset");
		xmlTraceResetDef.setBounds(105, 62, 85, 21);
		xmlTracePanel.add(xmlTraceResetDef);
		
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setBounds(200, 10, 575, 73);
		xmlTracePanel.add(scrollPane);
		
		xmlTraceNodes = new JList();
		xmlTraceNodes.setModel(new DefaultListModel<ListElement>());
		xmlTraceNodes.setForeground(Color.WHITE);
		xmlTraceNodes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		xmlTraceNodes.setBackground(Color.GRAY);
		scrollPane.setViewportView(xmlTraceNodes);
		
		xmlTraceAddNode = new JButton("Add Node");
		xmlTraceAddNode.setBounds(785, 10, 150, 21);
		xmlTracePanel.add(xmlTraceAddNode);
		
		xmlTraceRemoveNode = new JButton("Remove Node");
		xmlTraceRemoveNode.setBounds(785, 38, 150, 21);
		xmlTracePanel.add(xmlTraceRemoveNode);
		
		xmlTraceUp = new JButton("Up");
		xmlTraceUp.setBounds(785, 66, 70, 21);
		xmlTracePanel.add(xmlTraceUp);
		
		xmlTraceDown = new JButton("Down");
		xmlTraceDown.setBounds(865, 66, 70, 21);
		xmlTracePanel.add(xmlTraceDown);
		
		xmlTraceDelete = new JButton("Delete Rule");
		xmlTraceDelete.setBounds(80, 6, 110, 21);
		xmlTracePanel.add(xmlTraceDelete);
		
		xmlTraceSaveDef.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xmlhandler.Trace xmltrace = (xmlhandler.Trace) rule;
				xmltrace.setDefVal(xmlTraceDef.getText());
			}
		});
		
		xmlTraceResetDef.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xmlhandler.Trace xmltrace = (xmlhandler.Trace) rule;
				xmlTraceDef.setText(xmltrace.getDefVal());
			}
		});
		
		xmlTraceAddNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xmlhandler.Trace xmltrace = (xmlhandler.Trace) rule;
				ListElement typeReturn = ParamSelector.getSelection(Main.frame, xmlhandler.Trace.getNodeTypes(), "Node Type");
				String selecting = "";
				boolean checkNum = false;
				if (typeReturn != null) {
					if (typeReturn.getID().equals("0")) {
						selecting = "ElementName";
					} else if (typeReturn.getID().equals("1")) {
						selecting = "AttributeName";
					} else if (typeReturn.getID().equals("2")) {
						selecting = "ElementIndex";
						checkNum = true;
					} else if (typeReturn.getID().equals("3")) {
						selecting = "ElementName:AttributeName:AttributeValue";
					}
				} else {
					return;
				}
				String value = StringInput.getSelection(Main.frame, selecting);
				if (typeReturn == null || value == null) {
					return;
				} else if (!SettingHandler.matchesRegex("[0-9]+", value) && checkNum) {
					Main.popupMessage("Error - The index must be a number");
					return;
				}
				xmltrace.getNodes().add(new Pair<Short, String>(Short.parseShort(typeReturn.getID()), value));
				populate();
			}
		});
		
		xmlTraceRemoveNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xmlhandler.Trace xmltrace = (xmlhandler.Trace) rule;
				if (xmlTraceNodes.getSelectedIndex() != -1) {
					xmltrace.getNodes().remove(xmlTraceNodes.getSelectedIndex());
					populate();
				}
			}
		});
		
		xmlTraceUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xmlhandler.Trace xmltrace = (xmlhandler.Trace) rule;
				if (xmlTraceNodes.getSelectedIndex() > 0) {
					Pair<Short, String> oldVal = xmltrace.getNodes().get(xmlTraceNodes.getSelectedIndex());
					xmltrace.getNodes().remove(xmlTraceNodes.getSelectedIndex());
					xmltrace.getNodes().add(xmlTraceNodes.getSelectedIndex() - 1, oldVal);
					populate();
				}
			}
		});
		
		xmlTraceDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xmlhandler.Trace xmltrace = (xmlhandler.Trace) rule;
				if (xmlTraceNodes.getSelectedIndex() < xmltrace.getNodes().size() - 1 && xmlTraceNodes.getSelectedIndex() != -1) {
					Pair<Short, String> oldVal = xmltrace.getNodes().get(xmlTraceNodes.getSelectedIndex());
					xmltrace.getNodes().remove(xmlTraceNodes.getSelectedIndex());
					xmltrace.getNodes().add(xmlTraceNodes.getSelectedIndex() + 1, oldVal);
					populate();
				}
			}
		});
		
		discardPanel = new JPanel();
		discardPanel.setBackground(Color.DARK_GRAY);
		discardPanel.setBounds(0, 0, 945, 34);
		add(discardPanel);
		discardPanel.setLayout(null);
		
		rulePanels.add(discardPanel);
		
		JLabel label_2 = new JLabel("Discard");
		label_2.setForeground(Color.WHITE);
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_2.setBounds(10, 10, 36, 13);
		discardPanel.add(label_2);
		
		JLabel label_3 = new JLabel("Find:");
		label_3.setForeground(Color.WHITE);
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_3.setBounds(63, 10, 30, 13);
		discardPanel.add(label_3);
		
		discardFind = new JTextField();
		discardFind.setColumns(6);
		discardFind.setBounds(89, 6, 96, 19);
		discardPanel.add(discardFind);
		
		discardSaveFind = new JButton("Save");
		discardSaveFind.setBounds(195, 6, 85, 21);
		discardPanel.add(discardSaveFind);
		
		discardResetFind = new JButton("Reset");
		discardResetFind.setBounds(290, 6, 85, 21);
		discardPanel.add(discardResetFind);
		
		discardRegex = new JCheckBox("Regex");
		discardRegex.setForeground(Color.WHITE);
		discardRegex.setBackground(Color.DARK_GRAY);
		discardRegex.setBounds(381, 6, 58, 21);
		discardPanel.add(discardRegex);
		
		discardInverted = new JCheckBox("Inverted");
		discardInverted.setForeground(Color.WHITE);
		discardInverted.setBackground(Color.DARK_GRAY);
		discardInverted.setBounds(441, 6, 65, 21);
		discardPanel.add(discardInverted);
		
		discardUseHeader = new JCheckBox("Use Header");
		discardUseHeader.setForeground(Color.WHITE);
		discardUseHeader.setBackground(Color.DARK_GRAY);
		discardUseHeader.setBounds(660, 6, 79, 21);
		discardPanel.add(discardUseHeader);
		
		discardDelete = new JButton("Delete Rule");
		discardDelete.setBounds(798, 6, 137, 21);
		discardPanel.add(discardDelete);
		
		discardS = new JCheckBox("s");
		discardS.setForeground(Color.WHITE);
		discardS.setBackground(Color.DARK_GRAY);
		discardS.setBounds(508, 6, 30, 21);
		discardPanel.add(discardS);
		
		discardP = new JCheckBox("p");
		discardP.setForeground(Color.WHITE);
		discardP.setBackground(Color.DARK_GRAY);
		discardP.setBounds(540, 6, 36, 21);
		discardPanel.add(discardP);
		
		discardA = new JCheckBox("a");
		discardA.setForeground(Color.WHITE);
		discardA.setBackground(Color.DARK_GRAY);
		discardA.setBounds(578, 6, 36, 21);
		discardPanel.add(discardA);
		
		discardZ = new JCheckBox("z");
		discardZ.setForeground(Color.WHITE);
		discardZ.setBackground(Color.DARK_GRAY);
		discardZ.setBounds(616, 6, 42, 21);
		discardPanel.add(discardZ);
		
		discardSaveFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discard discard = (Discard) rule;
				discard.setFind(discardFind.getText());
			}
		});
		
		discardResetFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discard discard = (Discard) rule;
				discardFind.setText(discard.getFind());
			}
		});
		
		discardRegex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discard discard = (Discard) rule;
				discard.setRegex(discardRegex.isSelected());
			}
		});
		
		discardInverted.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discard discard = (Discard) rule;
				discard.setInverted(discardInverted.isSelected());
			}
		});
		
		discardUseHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discard discard = (Discard) rule;
				discard.setUseHeader(discardUseHeader.isSelected());
			}
		});
		
		discardS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discard discard = (Discard) rule;
				discard.changeFlagState("s");
				populate();
			}
		});
		
		discardP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discard discard = (Discard) rule;
				discard.changeFlagState("p");
				populate();
			}
		});
		
		discardZ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discard discard = (Discard) rule;
				discard.changeFlagState("z");
				populate();
			}
		});
		
		discardA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discard discard = (Discard) rule;
				discard.changeFlagState("a");
				populate();
			}
		});
		
		discardDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.removeRule(ruleID);
			}
		});
		
		jsonTracePanel = new JPanel();
		jsonTracePanel.setBackground(Color.DARK_GRAY);
		jsonTracePanel.setBounds(0, 0, 945, 100);
		add(jsonTracePanel);
		jsonTracePanel.setLayout(null);
		rulePanels.add(jsonTracePanel);
		
		JLabel label_12 = new JLabel("JSONTrace");
		label_12.setForeground(Color.WHITE);
		label_12.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_12.setBounds(10, 13, 62, 13);
		jsonTracePanel.add(label_12);
		
		jsonTraceDef = new JTextField();
		jsonTraceDef.setColumns(10);
		jsonTraceDef.setBounds(10, 36, 180, 19);
		jsonTracePanel.add(jsonTraceDef);
		
		jsonTraceSaveDef = new JButton("Save");
		jsonTraceSaveDef.setBounds(10, 65, 85, 21);
		jsonTracePanel.add(jsonTraceSaveDef);
		
		jsonTraceResetDef = new JButton("Reset");
		jsonTraceResetDef.setBounds(105, 65, 85, 21);
		jsonTracePanel.add(jsonTraceResetDef);
		
		jsonTraceAddElement = new JButton("Add Element");
		jsonTraceAddElement.setBounds(785, 13, 150, 21);
		jsonTracePanel.add(jsonTraceAddElement);
		
		jsonTraceRemoveElement = new JButton("Remove Element");
		jsonTraceRemoveElement.setBounds(785, 41, 150, 21);
		jsonTracePanel.add(jsonTraceRemoveElement);
		
		jsonTraceUp = new JButton("Up");
		jsonTraceUp.setBounds(785, 69, 70, 21);
		jsonTracePanel.add(jsonTraceUp);
		
		jsonTraceDown = new JButton("Down");
		jsonTraceDown.setBounds(865, 69, 70, 21);
		jsonTracePanel.add(jsonTraceDown);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder(null);
		scrollPane_1.setBounds(200, 13, 575, 73);
		jsonTracePanel.add(scrollPane_1);
		
		jsonTracePath = new JList();
		jsonTracePath.setModel(new DefaultListModel<ListElement>());
		jsonTracePath.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jsonTracePath.setForeground(Color.WHITE);
		jsonTracePath.setBackground(Color.GRAY);
		scrollPane_1.setViewportView(jsonTracePath);
		
		jsonTraceDelete = new JButton("Delete Rule");
		jsonTraceDelete.setBounds(80, 9, 110, 21);
		jsonTracePanel.add(jsonTraceDelete);
		
		replacePanel = new JPanel();
		replacePanel.setBackground(Color.DARK_GRAY);
		replacePanel.setBounds(0, 0, 945, 34);
		add(replacePanel);
		replacePanel.setLayout(null);
		rulePanels.add(replacePanel);
		
		JLabel label_6 = new JLabel("Replace");
		label_6.setForeground(Color.WHITE);
		label_6.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_6.setBounds(10, 8, 40, 13);
		replacePanel.add(label_6);
		
		JLabel label_7 = new JLabel("Find:");
		label_7.setForeground(Color.WHITE);
		label_7.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_7.setBounds(60, 8, 30, 13);
		replacePanel.add(label_7);
		
		replaceFind = new JTextField();
		replaceFind.setColumns(10);
		replaceFind.setBounds(86, 6, 96, 19);
		replacePanel.add(replaceFind);
		
		replaceSaveFind = new JButton("Save");
		replaceSaveFind.setBounds(192, 6, 60, 21);
		replacePanel.add(replaceSaveFind);
		
		replaceResetFind = new JButton("Reset");
		replaceResetFind.setBounds(262, 6, 60, 21);
		replacePanel.add(replaceResetFind);
		
		replaceDelete = new JButton("Delete Rule");
		replaceDelete.setBounds(798, 6, 137, 21);
		replacePanel.add(replaceDelete);
		
		JLabel label_8 = new JLabel("Replace:");
		label_8.setForeground(Color.WHITE);
		label_8.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_8.setBounds(332, 8, 53, 13);
		replacePanel.add(label_8);
		
		replaceReplace = new JTextField();
		replaceReplace.setColumns(10);
		replaceReplace.setBounds(377, 6, 96, 19);
		replacePanel.add(replaceReplace);
		
		replaceSaveReplace = new JButton("Save");
		replaceSaveReplace.setBounds(483, 6, 60, 21);
		replacePanel.add(replaceSaveReplace);
		
		replaceResetReplace = new JButton("Reset");
		replaceResetReplace.setBounds(553, 6, 85, 21);
		replacePanel.add(replaceResetReplace);
		
		replaceRegex = new JCheckBox("Regex");
		replaceRegex.setForeground(Color.WHITE);
		replaceRegex.setBackground(Color.DARK_GRAY);
		replaceRegex.setBounds(644, 6, 64, 21);
		replacePanel.add(replaceRegex);
		
		replaceUseHeader = new JCheckBox("Use Header");
		replaceUseHeader.setForeground(Color.WHITE);
		replaceUseHeader.setBackground(Color.DARK_GRAY);
		replaceUseHeader.setBounds(710, 6, 82, 21);
		replacePanel.add(replaceUseHeader);
		
		cutPanel = new JPanel();
		cutPanel.setBackground(Color.DARK_GRAY);
		cutPanel.setBounds(0, 0, 945, 34);
		add(cutPanel);
		cutPanel.setLayout(null);
		rulePanels.add(cutPanel);
		
		JLabel lblNewLabel = new JLabel("Cut");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(10, 10, 21, 13);
		cutPanel.add(lblNewLabel);
		
		JLabel label = new JLabel("Find:");
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label.setBounds(41, 10, 30, 13);
		cutPanel.add(label);
		
		cutFind = new JTextField();
		cutFind.setBounds(67, 7, 96, 19);
		cutPanel.add(cutFind);
		cutFind.setColumns(10);
		
		cutSaveFind = new JButton("Save");
		cutSaveFind.setBounds(173, 6, 85, 21);
		cutPanel.add(cutSaveFind);
		
		cutResetFind = new JButton("Reset");
		cutResetFind.setBounds(268, 6, 85, 21);
		cutPanel.add(cutResetFind);
		
		cutN = new JSpinner();
		cutN.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		cutN.setBounds(373, 7, 30, 20);
		cutPanel.add(cutN);
		
		JLabel label_1 = new JLabel("n:");
		label_1.setForeground(Color.WHITE);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_1.setBounds(363, 10, 21, 13);
		cutPanel.add(label_1);
		
		cutRegex = new JCheckBox("Regex");
		cutRegex.setForeground(Color.WHITE);
		cutRegex.setBackground(Color.DARK_GRAY);
		cutRegex.setBounds(409, 6, 65, 21);
		cutPanel.add(cutRegex);
		
		cutKeep = new JCheckBox("Keep");
		cutKeep.setForeground(Color.WHITE);
		cutKeep.setBackground(Color.DARK_GRAY);
		cutKeep.setBounds(476, 6, 65, 21);
		cutPanel.add(cutKeep);
		
		cutReEval = new JCheckBox("Re-evaluate");
		cutReEval.setForeground(Color.WHITE);
		cutReEval.setBackground(Color.DARK_GRAY);
		cutReEval.setBounds(543, 6, 89, 21);
		cutPanel.add(cutReEval);
		
		cutUseHeader = new JCheckBox("Use Header");
		cutUseHeader.setForeground(Color.WHITE);
		cutUseHeader.setBackground(Color.DARK_GRAY);
		cutUseHeader.setBounds(634, 6, 96, 21);
		cutPanel.add(cutUseHeader);
		
		cutDelete = new JButton("Delete Rule");
		cutDelete.setBounds(798, 6, 137, 21);
		cutPanel.add(cutDelete);
		
		addHeaderValPanel = new JPanel();
		addHeaderValPanel.setForeground(Color.WHITE);
		addHeaderValPanel.setBackground(Color.DARK_GRAY);
		addHeaderValPanel.setBounds(0, 0, 945, 34);
		add(addHeaderValPanel);
		addHeaderValPanel.setLayout(null);
		
		rulePanels.add(addHeaderValPanel);
		
		JLabel newLabel = new JLabel("AddHeaderVal");
		newLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		newLabel.setForeground(Color.WHITE);
		newLabel.setBounds(10, 10, 83, 13);
		addHeaderValPanel.add(newLabel);
		
		ahvFind = new JTextField();
		ahvFind.setBounds(187, 7, 96, 19);
		addHeaderValPanel.add(ahvFind);
		ahvFind.setColumns(10);
		
		ahvSaveFind = new JButton("Save");
		ahvSaveFind.setBounds(293, 6, 85, 21);
		addHeaderValPanel.add(ahvSaveFind);
		
		ahvResetFind = new JButton("Reset");
		ahvResetFind.setBounds(388, 6, 85, 21);
		addHeaderValPanel.add(ahvResetFind);
		
		ahvDelete = new JButton("Delete Rule");
		ahvDelete.setBounds(799, 6, 136, 21);
		addHeaderValPanel.add(ahvDelete);
		
		JLabel lblNewLabel_1 = new JLabel("Parameter Key:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(103, 10, 83, 13);
		addHeaderValPanel.add(lblNewLabel_1);
		
		isolatePanel = new JPanel();
		isolatePanel.setBackground(Color.DARK_GRAY);
		isolatePanel.setBounds(0, 0, 945, 34);
		add(isolatePanel);
		isolatePanel.setLayout(null);
		rulePanels.add(isolatePanel);
		
		JLabel label_4_1 = new JLabel("Isolate");
		label_4_1.setForeground(Color.WHITE);
		label_4_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_4_1.setBounds(10, 10, 39, 13);
		isolatePanel.add(label_4_1);
		
		JLabel label_5_1 = new JLabel("Regex:");
		label_5_1.setForeground(Color.WHITE);
		label_5_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_5_1.setBounds(59, 10, 39, 13);
		isolatePanel.add(label_5_1);
		
		isolateFind = new JTextField();
		isolateFind.setColumns(10);
		isolateFind.setBounds(108, 6, 96, 19);
		isolatePanel.add(isolateFind);
		
		isolateSaveFind = new JButton("Save");
		isolateSaveFind.setBounds(214, 6, 85, 21);
		isolatePanel.add(isolateSaveFind);
		
		isolateResetFind = new JButton("Reset");
		isolateResetFind.setBounds(309, 6, 85, 21);
		isolatePanel.add(isolateResetFind);
		
		isolateUseHeader = new JCheckBox("Use Header");
		isolateUseHeader.setForeground(Color.WHITE);
		isolateUseHeader.setBackground(Color.DARK_GRAY);
		isolateUseHeader.setBounds(400, 6, 96, 21);
		isolatePanel.add(isolateUseHeader);
		
		isolateDelete = new JButton("Delete Rule");
		isolateDelete.setBounds(798, 6, 137, 21);
		isolatePanel.add(isolateDelete);
		
		ahvSaveFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddHeaderVal ahv = (AddHeaderVal) rule;
				ahv.setFind(ahvFind.getText());
			}
		});
		
		ahvResetFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddHeaderVal ahv = (AddHeaderVal) rule;
				ahvFind.setText(ahv.getFind());
			}
		});
		
		ahvDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.removeRule(ruleID);
			}
		});
		
		cutSaveFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cut cut = (Cut) rule;
				cut.setFind(cutFind.getText());
			}
		});
		
		cutResetFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cut cut = (Cut) rule;
				cutFind.setText(cut.getFind());
			}
		});
		
		cutN.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Cut cut = (Cut) rule;
				cut.setN((Integer)cutN.getValue());
			}
		});
		
		cutRegex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cut cut = (Cut) rule;
				cut.setRegex(cutRegex.isSelected());
			}
		});
		
		cutKeep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cut cut = (Cut) rule;
				cut.setKeep(cutKeep.isSelected());
			}
		});
		
		cutReEval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cut cut = (Cut) rule;
				cut.setReEval(cutReEval.isSelected());
			}
		});
		
		cutUseHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cut cut = (Cut) rule;
				cut.setUseHeader(cutUseHeader.isSelected());
			}
		});
		
		cutDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.removeRule(ruleID);
			}
		});
		
		isolateSaveFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Isolate isolate = (Isolate) rule;
				isolate.setFind(isolateFind.getText());
			}
		});
		
		isolateResetFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Isolate isolate = (Isolate) rule;
				isolateFind.setText(isolate.getFind());
			}
		});
		
		isolateUseHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Isolate isolate = (Isolate) rule;
				isolate.setUseHeader(isolateUseHeader.isSelected());
			}
		});
		
		isolateDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.removeRule(ruleID);
			}
		});
		
		replaceSaveFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Replace replace = (Replace) rule;
				replace.setFind(replaceFind.getText());
			}
		});
		
		replaceResetFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Replace replace = (Replace) rule;
				replaceFind.setText(replace.getFind());
			}
		});
		
		replaceSaveReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Replace replace = (Replace) rule;
				replace.setReplace(replaceReplace.getText());
			}
		});
		
		replaceResetReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Replace replace = (Replace) rule;
				replaceReplace.setText(replace.getReplace());
			}
		});
		
		replaceRegex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Replace replace = (Replace) rule;
				replace.setRegex(replaceRegex.isSelected());
			}
		});
		
		replaceUseHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Replace replace = (Replace) rule;
				replace.setUseHeader(replaceUseHeader.isSelected());
			}
		});
		
		replaceDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.removeRule(ruleID);
			}
		});
		
		jsonTraceSaveDef.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jsonhandler.Trace jsontrace = (jsonhandler.Trace) rule;
				jsontrace.setDefVal(jsonTraceDef.getText());
			}
		});
		
		jsonTraceResetDef.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jsonhandler.Trace jsontrace = (jsonhandler.Trace) rule;
				jsonTraceDef.setText(jsontrace.getDefVal());
			}
		});
		
		jsonTraceAddElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jsonhandler.Trace jsontrace = (jsonhandler.Trace) rule;
				ListElement typeReturn = ParamSelector.getSelection(Main.frame, jsonhandler.Trace.getElementTypes(), "Node Type");
				String selecting = "";
				boolean checkNum = false;
				if (typeReturn != null) {
					if (typeReturn.getID().equals("2")) {
						selecting = "ElementName=ElementValue";
					} else if (typeReturn.getID().equals("3")) {
						selecting = "ElementName=ElementValueRegex";
					} else if (typeReturn.getID().equals("1")) {
						checkNum = true;
						selecting = "ArrayIndex";
					} else if (typeReturn.getID().equals("0")) {
						selecting = "ElementName";
					}
				} else {
					return;
				}
				String value = StringInput.getSelection(Main.frame, selecting);
				if (typeReturn == null || value == null) {
					return;
				} else if (!SettingHandler.matchesRegex("[0-9]+", value) && checkNum) {
					Main.popupMessage("Error - The index must be a number");
					return;
				}
				jsontrace.getPath().add(new Pair<Integer, String>(Integer.parseInt(typeReturn.getID()), value));
				populate();
			}
		});
		
		jsonTraceRemoveElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jsonhandler.Trace jsontrace = (jsonhandler.Trace) rule;
				if (jsonTracePath.getSelectedIndex() != -1) {
					jsontrace.getPath().remove(jsonTracePath.getSelectedIndex());
					populate();
				}
			}
		});
		
		jsonTraceUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jsonhandler.Trace jsontrace = (jsonhandler.Trace) rule;
				if (jsonTracePath.getSelectedIndex() > 0) {
					Pair<Integer, String> oldVal = jsontrace.getPath().get(jsonTracePath.getSelectedIndex());
					jsontrace.getPath().remove(jsonTracePath.getSelectedIndex());
					jsontrace.getPath().add(jsonTracePath.getSelectedIndex() - 1, oldVal);
					populate();
				}
			}
		});
		
		jsonTraceDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jsonhandler.Trace jsontrace = (jsonhandler.Trace) rule;
				if (jsonTracePath.getSelectedIndex() < jsontrace.getPath().size() - 1 && jsonTracePath.getSelectedIndex() != -1) {
					Pair<Integer, String> oldVal = jsontrace.getPath().get(jsonTracePath.getSelectedIndex());
					jsontrace.getPath().remove(jsonTracePath.getSelectedIndex());
					jsontrace.getPath().add(jsonTracePath.getSelectedIndex() + 1, oldVal);
					populate();
				}
			}
		});

		jsonTraceDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					parent.removeRule(ruleID);
			}
		});
		
		xmlTraceDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.removeRule(ruleID);
			}
		});
	}
	
	public int init(Rule rule, String ruleID, ParserGUIPanel parent) {
		this.ruleID = ruleID;
		this.rule = rule;
		this.parent = parent;
		populate();
		return this.height;
	}
	
	private void hideAll() {
		for (Component c : this.rulePanels) {
			c.setVisible(false);
		}
	}
	
	private void setHeight(int height) {
		this.setBounds(new Rectangle(945, height));
		this.height = height;
	}
	
	public void populate() {
		
		
		hideAll();
		if (rule instanceof AddHeaderVal) {
			AddHeaderVal ahv = (AddHeaderVal) rule;
			addHeaderValPanel.setVisible(true);
			ahvFind.setText(ahv.getFind());
			if (this.height == 0) {
				setHeight(34);
			}
		} else if (rule instanceof Cut) {
			Cut cut = (Cut) this.rule;
			cutPanel.setVisible(true);
			cutFind.setText(cut.getFind());
			cutN.setValue((Integer)cut.getN());
			cutKeep.setSelected(cut.isRegex());
			cutRegex.setSelected(cut.isKeep());
			cutReEval.setSelected(cut.isReEval());
			cutUseHeader.setSelected(cut.isUseHeader());
			if (this.height == 0) {
				setHeight(34);
			}
		} else if (rule instanceof Discard) {
			Discard discard = (Discard) rule;
			discardPanel.setVisible(true);
			discardInverted.setSelected(discard.isInverted());
			discardRegex.setSelected(discard.isRegex());
			discardUseHeader.setSelected(discard.isUseHeader());
			discardS.setSelected(discard.getFlags().contains("s"));
			discardP.setSelected(discard.getFlags().contains("p"));
			discardZ.setSelected(discard.getFlags().contains("z"));
			discardA.setSelected(discard.getFlags().contains("a"));
			if (discardS.isSelected()) {
				discardP.setEnabled(false);
				discardS.setEnabled(true);
			} else if (discardP.isSelected()) {
				discardP.setEnabled(true);
				discardS.setEnabled(false);
			} else {
				discardP.setEnabled(true);
				discardS.setEnabled(true);
			}
			if (discardZ.isSelected()) {
				discardZ.setEnabled(true);
				discardA.setEnabled(false);
			} else if (discardA.isSelected()) {
				discardZ.setEnabled(false);
				discardA.setEnabled(true);
			} else {
				discardZ.setEnabled(true);
				discardA.setEnabled(true);
			}
			if (this.height == 0) {
				setHeight(34);
			}
		} else if (rule instanceof Isolate) {
			Isolate isolate = (Isolate) rule;
			isolatePanel.setVisible(true);
			isolateFind.setText(isolate.getFind());
			isolateUseHeader.setSelected(isolate.isUseHeader());
			if (this.height == 0) {
				setHeight(34);
			}
		} else if (rule instanceof Replace) {
			Replace replace = (Replace) rule;
			replacePanel.setVisible(true);
			replaceFind.setText(replace.getFind());
			replaceReplace.setText(replace.getReplace());
			replaceRegex.setSelected(replace.isRegex());
			if (this.height == 0) {
				setHeight(34);
			}
		} else if (rule instanceof Split) {
			Split split = (Split) rule;
			splitPanel.setVisible(true);
			splitFind.setText(split.getFind());
			splitN.setValue(split.getN());
			splitRegex.setSelected(split.isRegex());
			splitUseHeader.setSelected(split.isUseHeader());
			if (this.height == 0) {
				setHeight(34);
			}
		} else if (rule instanceof xmlhandler.Trace) {
			xmlhandler.Trace xmltrace = (xmlhandler.Trace) rule;
			xmlTracePanel.setVisible(true);
			xmlTraceDef.setText(xmltrace.getDefVal());
			DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) xmlTraceNodes.getModel();
			model.clear();
			synchronized (xmltrace.getNodes()) {
				for (Pair<Short, String> node : xmltrace.getNodes()) {
					model.addElement(new ListElement(null, xmlhandler.Trace.cToStr(node.getKey()) + ", " + node.getValue()));
				}
			}
			if (this.height == 0) {
				setHeight(100);
			}
		} else if (rule instanceof jsonhandler.Trace) {
			jsonhandler.Trace jsontrace = (jsonhandler.Trace) rule;
			jsonTracePanel.setVisible(true);
			jsonTraceDef.setText(jsontrace.getDefVal());
			DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) jsonTracePath.getModel();
			model.clear();
			synchronized (jsontrace.getPath()) {
				for (Pair<Integer, String> element : jsontrace.getPath()) {
					model.addElement(new ListElement(null, jsonhandler.Trace.cToStr(element.getKey()) + ", " + element.getValue()));
				}
			}
			if (this.height == 0) {
				setHeight(100);
			}
		}
		this.repaint();
		this.validate();
	}
}
