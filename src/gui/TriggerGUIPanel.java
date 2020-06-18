package gui;

import javax.swing.JPanel;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.Insets;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.ListSelectionModel;

import trigger.Trigger;
import trigger.TriggerHandler;
import trigger.TriggerType;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import cc.Pair;
import group.GroupHandler;
import parser.ParserHandler;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class TriggerGUIPanel extends JPanel {
	
	private Trigger trigger;
	private boolean threadStatus;
	private JTextField nameValue;
	private JComboBox<String> typeValue;
	private JSpinner cooldownValue;
	private JButton changeTriggerStatus;
	private JButton triggerTrigger;
	private JButton saveName;
	private JButton resetName;
	private JButton resetType;
	private JButton saveType;
	private JList responderList;
	private JList triggeredByList;
	private JButton addResponder;
	private JButton removeResponder;
	private JButton addTriggerElement;
	private JButton removeTriggerElement;
	

	private List<Component> subComponents = Collections.synchronizedList(new ArrayList<Component>());
	private JButton deleteButton;
	
	
	public TriggerGUIPanel() {
		setBackground(Color.DARK_GRAY);
		setBounds(new Rectangle(0, 0, 945, 100));
		setLayout(null);
		
		changeTriggerStatus = new JButton("Status");
		changeTriggerStatus.setBounds(823, 70, 112, 20);
		add(changeTriggerStatus);
		
		triggerTrigger = new JButton("Trigger");
		triggerTrigger.setBounds(823, 39, 112, 20);
		add(triggerTrigger);
		
		nameValue = new JTextField();
		nameValue.setBounds(10, 35, 200, 20);
		add(nameValue);
		nameValue.setColumns(10);
		
		typeValue = new JComboBox<String>();
		typeValue.setBounds(10, 70, 200, 20);
		add(typeValue);
		
		saveName = new JButton("Save");
		saveName.setMargin(new Insets(2, 2, 2, 2));
		saveName.setBounds(220, 34, 50, 20);
		add(saveName);
		
		resetName = new JButton("Reset");
		resetName.setMargin(new Insets(2, 2, 2, 2));
		resetName.setBounds(275, 34, 50, 20);
		add(resetName);
		
		resetType = new JButton("Reset");
		resetType.setMargin(new Insets(2, 2, 2, 2));
		resetType.setBounds(275, 70, 50, 20);
		add(resetType);
		
		saveType = new JButton("Save");
		saveType.setMargin(new Insets(2, 2, 2, 2));
		saveType.setBounds(220, 70, 50, 20);
		add(saveType);
		
		deleteButton = new JButton("Delete Trigger");
		deleteButton.setBounds(220, 10, 105, 20);
		add(deleteButton);

		JScrollPane responderScroll = new JScrollPane();
		responderScroll.setBorder(null);
		responderScroll.setBounds(335, 10, 230, 50);
		add(responderScroll);
		
		responderList = new JList();
		responderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		responderList.setForeground(Color.WHITE);
		responderList.setBackground(Color.GRAY);
		responderList.setModel(new DefaultListModel<ListArrayElement>());
		responderScroll.setViewportView(responderList);
		
		JLabel triggerLabel = new JLabel("Trigger");
		triggerLabel.setForeground(Color.WHITE);
		triggerLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		triggerLabel.setBounds(10, 10, 200, 20);
		add(triggerLabel);
		
		JScrollPane triggeredByScroll = new JScrollPane();
		triggeredByScroll.setBorder(null);
		triggeredByScroll.setBounds(580, 10, 230, 50);
		add(triggeredByScroll);
		
		triggeredByList = new JList();
		triggeredByList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		triggeredByList.setForeground(Color.WHITE);
		triggeredByList.setBackground(Color.GRAY);
		triggeredByList.setModel(new DefaultListModel<ListElement>());
		triggeredByScroll.setViewportView(triggeredByList);
		
		addResponder = new JButton("Add Responder");
		addResponder.setBounds(335, 70, 115, 20);
		add(addResponder);
		
		removeResponder = new JButton("Remove Responder");
		removeResponder.setMargin(new Insets(2, 5, 2, 5));
		removeResponder.setBounds(455, 70, 110, 20);
		add(removeResponder);
		
		addTriggerElement = new JButton("Add Element");
		addTriggerElement.setBounds(580, 70, 115, 20);
		add(addTriggerElement);
		
		removeTriggerElement = new JButton("Remove Element");
		removeTriggerElement.setMargin(new Insets(2, 5, 2, 5));
		removeTriggerElement.setBounds(700, 70, 110, 20);
		add(removeTriggerElement);
		
		cooldownValue = new JSpinner();
		cooldownValue.setModel(new SpinnerNumberModel(5, 5, 1000, 1));
		cooldownValue.setBounds(823, 8, 112, 20);
		add(cooldownValue);
		
		saveName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trigger.setTriggerName(nameValue.getText());
			}
		});

		resetName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nameValue.setText(trigger.getTriggerName());
			}
		});
		
		saveType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (trigger.getType() == TriggerType.Responder && TriggerType.valueOf((String) typeValue.getSelectedItem()) == TriggerType.Listener) {
					trigger.getTriggeredBy().clear();
				} else if (trigger.getType() == TriggerType.Listener && TriggerType.valueOf((String) typeValue.getSelectedItem()) == TriggerType.Responder) {
					trigger.getTriggeredBy().clear();
				}
				trigger.setType(TriggerType.valueOf((String) typeValue.getSelectedItem()));
				listenerPopulate();
			}
		});
		
		resetType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typeValue.setSelectedItem(trigger.getType().name());
			}
		});
		
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.deleteTrigger(trigger.getTriggerID());
			}
		});

		addResponder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListElement parser = ParamSelector.getSelection(Main.frame, ParserHandler.getParserElements(), "Parser");
				if (parser == null) {
					return;
				}
				ListElement responder = ParamSelector.getSelection(Main.frame, GroupHandler.getResponderElements(), "Responder");
				if (responder == null) {
					return;
				}
				trigger.getResponderIDs().add(new Pair<String, String>(parser.getID(), responder.getID()));
				listenerPopulate();
			}
		});
		removeResponder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = responderList.getSelectedIndex();
				trigger.getResponderIDs().remove(index);
			}
		});
		addTriggerElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (trigger.getType() == TriggerType.Responder) {
					ListElement responder = ParamSelector.getSelection(Main.frame, GroupHandler.getResponderElements(), "Responder");
					if (responder != null) {
						trigger.getTriggeredBy().add(responder.getID());
						listenerPopulate();
					}
				} else if (trigger.getType() == TriggerType.Listener) {
					ListElement listener = ParamSelector.getSelection(Main.frame, GroupHandler.getListenerElements(), "Listener");
					
					if (listener != null) {
						trigger.getTriggeredBy().add(listener.getID());
						listenerPopulate();
					}
				}
			}
		});
		changeTriggerStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (threadStatus) {
					TriggerHandler.stopTrigger(trigger.getTriggerID());
					threadStatus = false;
				} else {
					TriggerHandler.runTrigger(trigger.getTriggerID());
					threadStatus = true;
				}
				listenerPopulate();
			}
		});
		triggerTrigger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trigger.setTrigger();
			}
		});
		cooldownValue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				trigger.setCooldown((Integer)cooldownValue.getValue());
			}
		});
				
		this.subComponents.add(this.nameValue);
		this.subComponents.add(this.typeValue);
		this.subComponents.add(saveName);
		this.subComponents.add(resetName);
		this.subComponents.add(saveType);
		this.subComponents.add(resetType);
		this.subComponents.add(addResponder);
		this.subComponents.add(removeResponder);
		this.subComponents.add(responderList);
		this.subComponents.add(triggeredByList);
		this.subComponents.add(addTriggerElement);
		this.subComponents.add(removeTriggerElement);
		this.subComponents.add(this.cooldownValue);
		this.subComponents.add(this.deleteButton);
		
	}
	
	public void init(Trigger trigger, boolean threadStatus) {
		this.trigger = trigger;
		this.threadStatus = threadStatus;
		populate();
	}
	
	private void setAll(boolean state) {
		for (Component component : this.subComponents) {
			component.setEnabled(state);
		}
	}
	
	private void manualState() {
		triggeredByList.setEnabled(false);
		addTriggerElement.setEnabled(false);
		removeTriggerElement.setEnabled(false);
		cooldownValue.setEnabled(false);
	}
	
	private void timerState() {
		triggeredByList.setEnabled(false);
		addTriggerElement.setEnabled(false);
		removeTriggerElement.setEnabled(false);
		triggerTrigger.setEnabled(false);
	}
	
	private void triggeredByState() {
		triggerTrigger.setEnabled(false);
		cooldownValue.setEnabled(false);
	}
	
	private void listenerPopulate() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				populate();
			}
		});
	}
	
	private void populate() {
		setAll(true);
		this.typeValue.removeAllItems();
		this.typeValue.addItem("Manual");
		this.typeValue.addItem("Listener");
		this.typeValue.addItem("Responder");
		this.typeValue.addItem("Timer");
		
		TriggerType type = trigger.getType();
		this.typeValue.setSelectedItem(type.name());
		if (type == TriggerType.Manual) {
			manualState();
		} else if (type == TriggerType.Timer) {
			timerState();
			this.cooldownValue.setValue(trigger.getCooldown());
		} else if (type == TriggerType.Listener || type == TriggerType.Responder) {
			triggeredByState();
			triggeredByList.clearSelection();
			List<String> listenerIDs = trigger.getTriggeredBy();
			DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) triggeredByList.getModel();
			model.clear();
			HashMap<String, String> triggeredByNames = TriggerHandler.getTriggeredByNameList(trigger.getTriggerID());
			for (String elementID : listenerIDs) {
				model.addElement(new ListElement(elementID, triggeredByNames.get(elementID)));
			}
		}
		
		nameValue.setText(trigger.getTriggerName());
		typeValue.setSelectedItem(type);
		List<Pair<Pair<String, String>, Pair<String, String>>> responderIDsList = TriggerHandler.getRespondersByList(trigger.getTriggerID());
		DefaultListModel<ListArrayElement> model = (DefaultListModel<ListArrayElement>) responderList.getModel();
		model.clear();
		for (Pair<Pair<String, String>, Pair<String, String>> kvp : responderIDsList) {
			model.addElement(new ListArrayElement(new String[] {kvp.getKey().getKey(), kvp.getValue().getKey()}, new String[] {kvp.getKey().getValue(), kvp.getValue().getValue()}));
		}
		
		
		if (this.threadStatus) {
			setAll(false);
			changeTriggerStatus.setText("Stop");
		} else {
			changeTriggerStatus.setText("Start");
		}
		
	}
}
