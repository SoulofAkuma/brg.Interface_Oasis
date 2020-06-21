package gui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

import group.GroupHandler;
import group.listener.Listener;
import settings.SettingHandler;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class ListenerGUIPanel extends JPanel {
	
	private JTextField nameValue;
	private JButton changeState;
	private JButton delete;
	private JCheckBox log;
	private JSpinner port;
	private JButton resetName;
	private JButton saveName;
	
	private Listener listener;
	
	public ListenerGUIPanel() {
		setBounds(new Rectangle(0, 0, 945, 34));
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Listener");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(10, 10, 46, 13);
		add(lblNewLabel);
		
		nameValue = new JTextField();
		nameValue.setBounds(66, 6, 120, 20);
		add(nameValue);
		nameValue.setColumns(10);
		
		saveName = new JButton("Save");
		saveName.setBounds(196, 6, 85, 21);
		add(saveName);
		
		resetName = new JButton("Reset");
		resetName.setBounds(291, 6, 85, 21);
		add(resetName);
		
		port = new JSpinner();
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(port, "#");
		port.setEditor(editor);
		port.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				listener.setPort((Integer)port.getValue());
			}
		});
		port.setBounds(386, 7, 80, 20);
		add(port);
		
		log = new JCheckBox("Log");
		log.setBackground(Color.DARK_GRAY);
		log.setFont(new Font("Tahoma", Font.PLAIN, 11));
		log.setForeground(Color.WHITE);
		log.setBounds(472, 6, 95, 21);
		add(log);
		
		delete = new JButton("Delete");
		delete.setBounds(815, 6, 120, 21);
		add(delete);
		
		changeState = new JButton("State");
		changeState.setBounds(685, 6, 120, 21);
		add(changeState);
		
		saveName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (SettingHandler.matchesRegex(SettingHandler.REGEXNAME, nameValue.getText())) {
					listener.setName(nameValue.getText());
				} else {
					Main.popupMessage("Error - The name does not match the Regex " + SettingHandler.REGEXNAME);
				}
			}
		});
		
		resetName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nameValue.setText(listener.getName());
			}
		});
		
		log.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.setLog(log.isSelected());
			}
		});
		
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GroupHandler.getListenerHandler(GroupHandler.ltgID(listener.getListenerID())).removeListener(listener.getListenerID());
				Main.frame.listenersMode(GroupHandler.getListenerHandler(GroupHandler.ltgID(listener.getListenerID())));
			}
		});
		
		changeState.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listener.isActive()) {
					GroupHandler.getListenerHandler(GroupHandler.ltgID(listener.getListenerID())).stopListener(listener.getListenerID());
				} else {
					GroupHandler.getListenerHandler(GroupHandler.ltgID(listener.getListenerID())).runListener(listener.getListenerID());
				}
				populate();
			}
		});
	}
	
	public void init(Listener listener) {
		this.listener = listener;
		populate();
	}
	
	private void setAll(boolean state) {
		saveName.setEnabled(state);
		resetName.setEnabled(state);
		nameValue.setEnabled(state);
		port.setEnabled(state);
		log.setEnabled(state);
		delete.setEnabled(state);
	}
	
	public void populate() {
		
		nameValue.setText(listener.getName());
		port.setValue((Integer)listener.getPort());
		log.setSelected(listener.getLog());
		
		if (listener.isActive()) {
			setAll(false);
			changeState.setText("Stop");
		} else {
			setAll(true);
			changeState.setText("Start");
		}
		
	}
}
