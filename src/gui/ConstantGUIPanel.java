package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import constant.Constant;
import constant.ConstantHandler;
import constant.Value;
import settings.SettingHandler;

import javax.swing.DefaultListModel;
import javax.swing.JButton;

public class ConstantGUIPanel extends JPanel {
	
	private JTextField nameValue;
	private JList valuesList;
	private JButton saveName;
	private JButton resetName;
	private JButton delete;
	private JButton up;
	private JButton down;
	private JButton manageValues;
	
	private Constant constant;
	private ConstantGUIPanel panel;
	
	public ConstantGUIPanel() {
		setBackground(Color.DARK_GRAY);
		setBounds(new Rectangle(0, 0, 945, 100));
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Constant");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(10, 10, 46, 13);
		add(lblNewLabel);
		
		nameValue = new JTextField();
		nameValue.setColumns(10);
		nameValue.setBounds(10, 40, 150, 19);
		add(nameValue);
		
		saveName = new JButton("Save");
		saveName.setBounds(10, 70, 70, 21);
		add(saveName);
		
		resetName = new JButton("Reset");
		resetName.setBounds(90, 70, 70, 21);
		add(resetName);
		
		JScrollPane constantsScroll = new JScrollPane();
		constantsScroll.setBorder(null);
		constantsScroll.setBounds(170, 9, 765, 60);
		add(constantsScroll);

		valuesList = new JList();
		valuesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		valuesList.setForeground(Color.WHITE);
		valuesList.setBackground(Color.GRAY);
		valuesList.setModel(new DefaultListModel<ListElement>());
		constantsScroll.setViewportView(valuesList);
		
		delete = new JButton("Delete");
		delete.setBounds(75, 6, 85, 21);
		add(delete);
		
		up = new JButton("Up");
		up.setBounds(170, 74, 246, 21);
		add(up);
		
		down = new JButton("Down");
		down.setBounds(427, 74, 246, 21);
		add(down);
		
		manageValues = new JButton("Manager Values");
		manageValues.setBounds(685, 74, 250, 21);
		add(manageValues);
		
		saveName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (SettingHandler.matchesRegex(SettingHandler.REGEXNAME, nameValue.getText())) {
					constant.setName(nameValue.getText());
				} else {
					Main.popupMessage("Error - The value does not match the regex " + SettingHandler.REGEXNAME);
				}
			}
		});
		
		resetName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nameValue.setText(constant.getName());
			}
		});
		
		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) valuesList.getModel();
				if (valuesList.getSelectedIndex() != -1 && valuesList.getSelectedIndex() != 0) {
					constant.changeValuePosition(model.get(valuesList.getSelectedIndex()).getID(), valuesList.getSelectedIndex() - 1);
					populate();
				}
			}
		});
		
		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) valuesList.getModel();
				if (valuesList.getSelectedIndex() != -1 && valuesList.getSelectedIndex() < model.size() - 1) {
					constant.changeValuePosition(model.get(valuesList.getSelectedIndex()).getID(), valuesList.getSelectedIndex() + 1);
					populate();
				}
			}
		});
		
		manageValues.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.frame.valuesMode(constant.getValues(), constant.getOrder(), panel);
			}
		});
		
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConstantHandler.removeConstant(constant.getId());
				Main.frame.populateConstants();
			}
		});
	}
	
	public void init(Constant constant) {
		this.constant = constant;
		this.panel = this;
		populate();
	}
	
	public void removeValue(String valueID) {
		constant.removeValue(valueID);
		Main.frame.valuesMode(constant.getValues(), constant.getOrder(), this.panel);;
	}
	
	public void addValue(Value value) {
		constant.addValue(value.getId(), value);
		Main.frame.valuesMode(constant.getValues(), constant.getOrder(), this.panel);
	}
	
	public String getID() {
		return this.constant.getId();
	}
	
	private void populate() {
		
		nameValue.setText(constant.getName());
		ConcurrentHashMap<String, Value> values = constant.getValues();
		List<String> order = constant.getOrder();
		DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) valuesList.getModel();
		model.clear();
		for (String value : order) {
			model.addElement(new ListElement(value, values.get(value).getValueString()));
		}
	}

}
