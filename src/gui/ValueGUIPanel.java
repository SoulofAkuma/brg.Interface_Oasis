package gui;

import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

import constant.ConstantHandler;
import constant.Value;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class ValueGUIPanel extends JPanel {
	
	private Value value;
	private ConstantGUIPanel parent;
	
	private JTextField valueValue;
	private JButton save;
	private JButton reset;
	private JButton delete;
	private JCheckBox useHeader;
	private JCheckBox isBackReference;
	private JCheckBox isKey;
	private JLabel backReference;
	private JButton selectReference;
	
	public ValueGUIPanel() {
		setBackground(Color.DARK_GRAY);
		setBounds(new Rectangle(0, 0, 945, 34));
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Value");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(10, 10, 37, 13);
		add(lblNewLabel);
		
		valueValue = new JTextField();
		valueValue.setBounds(57, 7, 200, 19);
		add(valueValue);
		valueValue.setColumns(10);
		
		save = new JButton("Save");
		save.setBounds(267, 6, 85, 21);
		add(save);
		
		reset = new JButton("Reset");
		reset.setBounds(362, 6, 85, 21);
		add(reset);
		
		useHeader = new JCheckBox("Use Header");
		useHeader.setFont(new Font("Tahoma", Font.PLAIN, 11));
		useHeader.setForeground(Color.WHITE);
		useHeader.setBackground(Color.DARK_GRAY);
		useHeader.setBounds(453, 6, 95, 21);
		add(useHeader);
		
		delete = new JButton("Delete");
		delete.setBounds(808, 6, 120, 21);
		add(delete);
		
		isBackReference = new JCheckBox("Backreference");
		isBackReference.setForeground(Color.WHITE);
		isBackReference.setFont(new Font("Tahoma", Font.PLAIN, 11));
		isBackReference.setBackground(Color.DARK_GRAY);
		isBackReference.setBounds(550, 6, 113, 21);
		add(isBackReference);
		
		isKey = new JCheckBox("Is Key");
		isKey.setForeground(Color.WHITE);
		isKey.setFont(new Font("Tahoma", Font.PLAIN, 11));
		isKey.setBackground(Color.DARK_GRAY);
		isKey.setBounds(665, 6, 95, 21);
		add(isKey);
		
		backReference = new JLabel();
		backReference.setBounds(57, 6, 200, 21);
		add(backReference);
		
		selectReference = new JButton("Select Refernce");
		selectReference.setBounds(267, 6, 180, 21);
		add(selectReference);
		
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				value.setValue(valueValue.getText());
			}
		});
		
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueValue.setText(value.getValue());
			}
		});
		
		useHeader.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				value.setUseHeader(useHeader.isSelected());
				populate();
			}
		});
		
		isBackReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				value.setValue("");
				value.setBackReference(isBackReference.isSelected());
				populate();
			}
		});
		
		isKey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				value.setKey(isKey.isSelected());
				populate();
			}
		});
		
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.removeValue(value.getId());
			}
		});
		
		selectReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ListElement[] relements = ConstantHandler.getConstantElements();
				ArrayList<ListElement> elements = new ArrayList<ListElement>();
				for (ListElement element : relements) {
					if (element.getID() != parent.getID()) {
						elements.add(element);
					}
				}
				ListElement input = ParamSelector.getSelection(Main.frame, elements.toArray(new ListElement[elements.size()]), "Reference Constant");
				if (input != null) {
					value.setValue(input.getID());
					backReference.setText(input.toString());
				}
			}
		});
	}
	
	public void init(Value value, ConstantGUIPanel parent) {
		this.value = value;
		this.parent = parent;
		populate();
	}
	
	public void populate() {
		
		isBackReference.setSelected(value.isBackReference());
		useHeader.setSelected(value.isUseHeader());
		isKey.setSelected(value.isKey());
		
		if (value.isBackReference()) {
			backReference.setVisible(true);
			selectReference.setVisible(true);
			valueValue.setVisible(false);
			save.setVisible(false);
			reset.setVisible(false);
			useHeader.setEnabled(false);
			isBackReference.setEnabled(true);
			isKey.setEnabled(false);
			backReference.setText(ConstantHandler.getConstantNames().get(value.getValue()));
		} else {
			backReference.setVisible(false);
			selectReference.setVisible(false);
			valueValue.setVisible(true);
			save.setVisible(true);
			reset.setVisible(true);
			useHeader.setEnabled(true);
			isKey.setEnabled(true);
			if (value.isKey()) {
				isBackReference.setEnabled(false);
			} else {
				isBackReference.setEnabled(true);
			}
			valueValue.setText(value.getValue());
		}
		
	}
}
