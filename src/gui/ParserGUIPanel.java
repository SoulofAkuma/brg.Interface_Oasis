package gui;

import javax.swing.*;

import parser.CustomParser;
import parser.Parser;
import parser.ParserHandler;
import parser.Rule;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ParserGUIPanel extends JPanel {
	
	private JTextField nameValue;
	private JButton resetName;
	private JButton saveName;
	private JList rulesList;
	private JButton upRule;
	private JButton downRule;
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
		setBounds(new Rectangle(0, 0, 945, 100));
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
		rulesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		rulesList.setForeground(Color.WHITE);
		rulesList.setBackground(Color.GRAY);
		rulesList.setModel(new DefaultListModel<ListArrayElement>());
		
		JScrollPane rulesPane = new JScrollPane(rulesList);
		rulesPane.setBounds(220, 10, 400, 50);
		rulesPane.setBorder(null);
		rulesList.setPreferredSize(new Dimension(400,50));
		add(rulesPane);
		
		upRule = new JButton("Move Up");
		upRule.setMargin(new Insets(2, 2, 2, 2));
		upRule.setBounds(220, 70, 127, 20);
		add(upRule);
		
		downRule = new JButton("Move Down");
		downRule.setMargin(new Insets(2, 2, 2, 2));
		downRule.setBounds(357, 70, 127, 20);
		add(downRule);
		
		manageRules = new JButton("Manage Rules");
		manageRules.setMargin(new Insets(2, 2, 2, 2));
		manageRules.setBounds(493, 70, 127, 20);
		add(manageRules);
		
		
		assignersList = new JList();
		assignersList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		assignersList.setForeground(Color.WHITE);
		assignersList.setBackground(Color.GRAY);
		assignersList.setModel(new DefaultListModel<ListElement>());
		
		JScrollPane assignersPane = new JScrollPane(assignersList);
		assignersPane.setBounds(630, 10, 305, 50);
		assignersPane.setBorder(null);
		assignersList.setPreferredSize(new Dimension(305, 50));
		add(assignersPane);
		
		addAssigner = new JButton("Add");
		addAssigner.setMargin(new Insets(2, 2, 2, 2));
		addAssigner.setBounds(630, 70, 70, 20);
		add(addAssigner);
		
		removeAssigner = new JButton("Remove");
		removeAssigner.setMargin(new Insets(2, 2, 2, 2));
		removeAssigner.setBounds(710, 70, 75, 20);
		add(removeAssigner);
		
		assignerUp = new JButton("Move Up");
		assignerUp.setMargin(new Insets(2, 2, 2, 2));
		assignerUp.setBounds(795, 70, 65, 20);
		add(assignerUp);
		
		assignerDown = new JButton("Move Up");
		assignerDown.setMargin(new Insets(2, 2, 2, 2));
		assignerDown.setBounds(870, 70, 65, 20);
		add(assignerDown);
		
		deleteParser = new JButton("Delete Parser");
		deleteParser.setBounds(77, 11, 133, 21);
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
		
	}
	
	public void init(CustomParser parser) {
		this.parser = parser;
		populate();
	}
	
	public void populate() {
		nameValue.setText(parser.getName());
		ConcurrentHashMap<String, Rule> rules = parser.getElements();
		for (Entry<String, Rule> rule : rules.entrySet()) {
			HashMap<String, String> ruleAtt = 
		}
	}
	
	
}
