package gui;

import javax.swing.*;

import parser.CustomParser;
import parser.Parser;
import parser.ParserHandler;
import parser.Rule;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


//This class does only support Parser Rules and xmlhandler/jsonhanlder trace. To create an own Rule GUI implementation create a new panel inside of RuleGUIPanels main Panel and add a the proper printList method to your rule class
public class ParserGUIPanel extends JPanel {
	
	private JTextField nameValue;
	private JButton resetName;
	private JButton saveName;
	private JList rulesList;
	private JButton ruleUp;
	private JButton ruleDown;
	private JButton manageRules;
	private JList assignersList;
	private JButton addAssigner;
	private JButton removeAssigner;
	private JButton assignerUp;
	private JButton assignerDown;
	private JButton deleteParser;
	private CustomParser parser;
	
	public ParserGUIPanel() {
		setBackground(Color.DARK_GRAY);
		setBounds(new Rectangle(0, 0, 945, 130));
		setLayout(null);	
		
		nameValue = new JTextField();
		nameValue.setColumns(10);
		nameValue.setBounds(10, 40, 200, 20);
		add(nameValue);
		
		saveName = new JButton("Save");
		saveName.setMargin(new Insets(2, 2, 2, 2));
		saveName.setBounds(10, 70, 95, 20);
		add(saveName);
		
		resetName = new JButton("Reset");
		resetName.setMargin(new Insets(2, 2, 2, 2));
		resetName.setBounds(115, 70, 95, 20);
		add(resetName);
		
		JLabel label = new JLabel("Parser");
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label.setBounds(10, 10, 200, 20);
		add(label);
		
		rulesList = new JList();
		rulesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rulesList.setForeground(Color.WHITE);
		rulesList.setBackground(Color.GRAY);
		rulesList.setModel(new DefaultListModel<ListElement>());
		
		JScrollPane rulesPane = new JScrollPane();
		rulesPane.setBounds(220, 10, 400, 80);
		rulesPane.setBorder(null);
		rulesPane.setViewportView(rulesList);
		add(rulesPane);
		
		ruleUp = new JButton("Move Up");
		ruleUp.setMargin(new Insets(2, 2, 2, 2));
		ruleUp.setBounds(220, 98, 127, 20);
		add(ruleUp);
		
		ruleDown = new JButton("Move Down");
		ruleDown.setMargin(new Insets(2, 2, 2, 2));
		ruleDown.setBounds(357, 98, 127, 20);
		add(ruleDown);
		
		manageRules = new JButton("Manage Rules");
		manageRules.setMargin(new Insets(2, 2, 2, 2));
		manageRules.setBounds(493, 98, 127, 20);
		add(manageRules);
		
		
		assignersList = new JList();
		assignersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		assignersList.setForeground(Color.WHITE);
		assignersList.setBackground(Color.GRAY);
		assignersList.setModel(new DefaultListModel<ListElement>());
		
		JScrollPane assignersPane = new JScrollPane();
		assignersPane.setBounds(630, 10, 305, 80);
		assignersPane.setBorder(null);
		assignersPane.setViewportView(assignersList);
		add(assignersPane);
		
		addAssigner = new JButton("Add");
		addAssigner.setMargin(new Insets(2, 2, 2, 2));
		addAssigner.setBounds(630, 98, 70, 20);
		add(addAssigner);
		
		removeAssigner = new JButton("Remove");
		removeAssigner.setMargin(new Insets(2, 2, 2, 2));
		removeAssigner.setBounds(710, 98, 75, 20);
		add(removeAssigner);
		
		assignerUp = new JButton("Move Up");
		assignerUp.setMargin(new Insets(2, 2, 2, 2));
		assignerUp.setBounds(795, 98, 65, 20);
		add(assignerUp);
		
		assignerDown = new JButton("Move Up");
		assignerDown.setMargin(new Insets(2, 2, 2, 2));
		assignerDown.setBounds(870, 98, 65, 20);
		add(assignerDown);
		
		deleteParser = new JButton("Delete Parser");
		deleteParser.setBounds(10, 98, 200, 21);
		add(deleteParser);
		
		saveName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parser.setName(nameValue.getText());
			}
		});
		
		resetName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nameValue.setText(parser.getName());
			}
		});
		
		deleteParser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.deleteParser(parser.getID());
			}
		});
		
		ruleUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) rulesList.getModel();
				if (rulesList.getSelectedIndex() != 0) {
					parser.changeRulePosition(model.get(rulesList.getSelectedIndex()).getID(), rulesList.getSelectedIndex() - 1);
					populate();
				}
			}
		});
		
		ruleDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) rulesList.getModel();
				if (rulesList.getSelectedIndex() < model.size() - 1) {
					parser.changeRulePosition(model.get(rulesList.getSelectedIndex()).getID(), rulesList.getSelectedIndex() + 1);
					populate();
				}
			}
		});
		
		assignerUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (assignersList.getSelectedIndex() != 0) {
					parser.changeAssignerPosition(assignersList.getSelectedIndex(), assignersList.getSelectedIndex() - 1);
					populate();
				}
			}
		});
		
		assignerDown.addActionListener(new ActionListener() {
			DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) assignersList.getModel();
			public void actionPerformed(ActionEvent e) {
				if (assignersList.getSelectedIndex() < model.size() - 1) {
					parser.changeAssignerPosition(assignersList.getSelectedIndex(), assignersList.getSelectedIndex() + 1);
					populate();
				}
			}
		});
	}
	
	public void init(CustomParser parser) {
		this.parser = parser;
		populate();
	}
	
	public void populate() {
		nameValue.setText(parser.getName());
		
		ConcurrentHashMap<String, Rule> rules = parser.getElements();
		List<String> ruleOrder = parser.getOrder();
		DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) rulesList.getModel();
		model.clear();
		for (String ruleID : ruleOrder) {
			model.addElement(new ListElement(ruleID, rules.get(ruleID).printRuleLRP()));
		}
		
		List<String> assigners = parser.getIndexAssigners();
		HashMap<String, String> assignerNames = ParserHandler.getAssignerNameList(parser.getID());
		DefaultListModel<ListElement> model1 = (DefaultListModel<ListElement>) assignersList.getModel();
		model1.clear();
		for (String assignerID : assigners) {
			model1.addElement(new ListElement(assignerID, assignerNames.get(assignerID)));
		}
	}
	
	
}
