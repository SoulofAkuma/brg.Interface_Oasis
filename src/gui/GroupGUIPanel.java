package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cc.Pair;
import group.GroupHandler;
import group.listener.ListenerHandler;
import group.responder.ResponderHandler;
import settings.SettingHandler;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;

public class GroupGUIPanel extends JPanel {
	
	private JList listenersList;
	private JList respondersList;
	private JButton saveName;
	private JButton resetName;
	private JButton delete;
	private JTextField nameValue;
	private JButton manageListeners;
	private JButton manageResponders;
	
	private Pair<ListenerHandler, ResponderHandler> group;
	private String groupID;
	
	public GroupGUIPanel() {
		setBackground(Color.DARK_GRAY);
		setBounds(new Rectangle(0, 0, 945, 100));
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Group");
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
		
		delete = new JButton("Delete");
		delete.setBounds(75, 6, 85, 21);
		add(delete);
		
		JScrollPane listenersScroll = new JScrollPane();
		listenersScroll.setBorder(null);
		listenersScroll.setBounds(170, 9, 380, 55);
		add(listenersScroll);

		listenersList = new JList();
		listenersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listenersList.setForeground(Color.WHITE);
		listenersList.setBackground(Color.GRAY);
		listenersList.setModel(new DefaultListModel<ListElement>());
		listenersScroll.setViewportView(listenersList);
		
		JScrollPane respondersScroll = new JScrollPane();
		respondersScroll.setBorder(null);
		respondersScroll.setBounds(560, 9, 380, 55);
		add(respondersScroll);

		respondersList = new JList();
		respondersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		respondersList.setForeground(Color.WHITE);
		respondersList.setBackground(Color.GRAY);
		respondersList.setModel(new DefaultListModel<ListElement>());
		respondersScroll.setViewportView(respondersList);
		
		manageListeners = new JButton("Manage Listeners");
		manageListeners.setBounds(170, 70, 380, 21);
		add(manageListeners);
		
		manageResponders = new JButton("Manage Responders");
		manageResponders.setBounds(560, 70, 380, 21);
		add(manageResponders);
		
		saveName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (SettingHandler.matchesRegex(SettingHandler.REGEXNAME, nameValue.getText())) {
					GroupHandler.changeGroupName(groupID, nameValue.getText());
				} else {
					Main.popupMessage("Error - The name does not match the Regex " + SettingHandler.REGEXNAME);
				}
			}
		});
		
		resetName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nameValue.setText(GroupHandler.getGroupName(groupID));
			}
		});
		
		manageListeners.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.frame.listenersMode(group.getKey());
			}
		});
		
		manageResponders.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.frame.respondersMode(group.getValue());
			}
		});
		
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GroupHandler.removeGroup(groupID);
				Main.frame.populateGroups();
			}
		});
	}

	public void init(Pair<ListenerHandler, ResponderHandler> group, String groupID) {
		this.group = group;
		this.groupID = groupID;
		populate();
	}
	
	public void populate() {
		
		nameValue.setText(GroupHandler.getGroupName(groupID));
		
		DefaultListModel<ListElement> listenerModel = (DefaultListModel<ListElement>) listenersList.getModel();
		listenerModel.clear();
		listenerModel.addAll(group.getKey().getListenerElementsDetail());
		
		DefaultListModel<ListElement> respondersModel = (DefaultListModel<ListElement>) respondersList.getModel();
		respondersModel.clear();
		respondersModel.addAll(group.getValue().getResponderElementsDetail());
	}
}
