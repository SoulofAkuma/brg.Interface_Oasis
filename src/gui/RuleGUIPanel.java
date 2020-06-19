package gui;

import javax.swing.JPanel;

import parser.Rule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;

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
	
	private List<Component> rulePanels = Collections.synchronizedList(new ArrayList<Component>());
	private JTextField ahvFind;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel label;
	private JTextField cutFind;
	private JButton cutSaveFind;
	private JButton cutResetFind;
	
	public RuleGUIPanel() {
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		setBounds(new Rectangle(0, 0, 945, 100));
		
		cutPanel = new JPanel();
		cutPanel.setBackground(Color.DARK_GRAY);
		cutPanel.setBounds(0, 0, 945, 100);
		add(cutPanel);
		cutPanel.setLayout(null);
		rulePanels.add(cutPanel);
		
		lblNewLabel = new JLabel("Cut");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(10, 10, 21, 13);
		cutPanel.add(lblNewLabel);
		
		label = new JLabel("Find:");
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
		
		JSpinner cutN = new JSpinner();
		cutN.setBounds(373, 7, 30, 20);
		cutPanel.add(cutN);
		
		JLabel label_1 = new JLabel("n:");
		label_1.setForeground(Color.WHITE);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_1.setBounds(363, 10, 21, 13);
		cutPanel.add(label_1);
		
		JCheckBox cutRegex = new JCheckBox("Regex");
		cutRegex.setForeground(Color.WHITE);
		cutRegex.setBackground(Color.DARK_GRAY);
		cutRegex.setBounds(409, 6, 65, 21);
		cutPanel.add(cutRegex);
		
		JCheckBox cutKeep = new JCheckBox("Keep");
		cutKeep.setForeground(Color.WHITE);
		cutKeep.setBackground(Color.DARK_GRAY);
		cutKeep.setBounds(476, 6, 65, 21);
		cutPanel.add(cutKeep);
		
		JCheckBox cutReEval = new JCheckBox("Re-evaluate");
		cutReEval.setForeground(Color.WHITE);
		cutReEval.setBackground(Color.DARK_GRAY);
		cutReEval.setBounds(543, 6, 89, 21);
		cutPanel.add(cutReEval);
		
		JCheckBox cutUseHeader = new JCheckBox("Use Header");
		cutUseHeader.setForeground(Color.WHITE);
		cutUseHeader.setBackground(Color.DARK_GRAY);
		cutUseHeader.setBounds(634, 6, 96, 21);
		cutPanel.add(cutUseHeader);
		
		JButton cutDelete = new JButton("Delete Rule");
		cutDelete.setBounds(717, 6, 85, 21);
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
		
		JButton ahvSaveFind = new JButton("Save");
		ahvSaveFind.setBounds(293, 6, 85, 21);
		addHeaderValPanel.add(ahvSaveFind);
		
		JButton ahvResetFind = new JButton("Reset");
		ahvResetFind.setBounds(388, 6, 85, 21);
		addHeaderValPanel.add(ahvResetFind);
		
		JButton ahvDelete = new JButton("Delete Rule");
		ahvDelete.setBounds(493, 6, 136, 21);
		addHeaderValPanel.add(ahvDelete);
		
		lblNewLabel_1 = new JLabel("Parameter Key:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(103, 10, 83, 13);
		addHeaderValPanel.add(lblNewLabel_1);
		
		discardPanel = new JPanel();
		discardPanel.setBackground(Color.DARK_GRAY);
		discardPanel.setBounds(0, 0, 945, 100);
		add(discardPanel);
		discardPanel.setLayout(null);
		
		isolatePanel = new JPanel();
		isolatePanel.setBackground(Color.DARK_GRAY);
		isolatePanel.setBounds(0, 0, 945, 100);
		add(isolatePanel);
		isolatePanel.setLayout(null);
		
		replacePanel = new JPanel();
		replacePanel.setBackground(Color.DARK_GRAY);
		replacePanel.setBounds(0, 0, 945, 100);
		add(replacePanel);
		replacePanel.setLayout(null);
		
		splitPanel = new JPanel();
		splitPanel.setBackground(Color.DARK_GRAY);
		splitPanel.setBounds(0, 0, 945, 100);
		add(splitPanel);
		splitPanel.setLayout(null);
		
		xmlTracePanel = new JPanel();
		xmlTracePanel.setBackground(Color.DARK_GRAY);
		xmlTracePanel.setBounds(0, 0, 945, 100);
		add(xmlTracePanel);
		xmlTracePanel.setLayout(null);
		
		jsonTracePanel = new JPanel();
		jsonTracePanel.setBackground(Color.DARK_GRAY);
		jsonTracePanel.setBounds(0, 0, 945, 100);
		add(jsonTracePanel);
		jsonTracePanel.setLayout(null);
		rulePanels.add(discardPanel);
		rulePanels.add(isolatePanel);
		rulePanels.add(replacePanel);
		rulePanels.add(splitPanel);
		rulePanels.add(xmlTracePanel);
		rulePanels.add(jsonTracePanel);
		
	}
	
	public void init(Rule rule) {
		this.rule = rule;
	}
	
	private void setType(String rtype) {
		hideAll();
		switch (rtype) {
			case "parser.AddHeaderVal":
				addHeaderValPanel.setVisible(true);
				this.height = 34;
			break;
			case "parser.Cut":
				cutPanel.setVisible(true);
			break;
			case "parser.Discard":
				discardPanel.setVisible(true);
			break;
			case "parser.Isolate":
				isolatePanel.setVisible(true);
			break;
			case "parser.Replace":
				replacePanel.setVisible(true);
			break;
			case "parser.Split":
				splitPanel.setVisible(true);
			break;
			case "xmlhandler.Trace":
				xmlTracePanel.setVisible(true);
			break;
			case "jsonhandler.Trace":
				jsonTracePanel.setVisible(true);
			break;
		}
	}
	
	private void hideAll() {
		for (Component c : this.rulePanels) {
			c.setVisible(false);
		}
	}
}
