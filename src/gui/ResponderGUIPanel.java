package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.text.JTextComponent;

import constant.ConstantHandler;
import group.GroupHandler;
import group.responder.Responder;
import settings.SettingHandler;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class ResponderGUIPanel extends JPanel {
	
	private Responder responder;
	
	private JTextField nameValue;
	private JList customArgsList;
	private JTextField separator;
	private JList constantsList;
	private JLabel url;
	private JButton saveName;
	private JButton resetName;
	private JCheckBox log;
	private JButton selectUrl;
	private JLabel userAgent;
	private JButton selectAgent;
	private JButton removeAgent;
	private JLabel contentType;
	private JButton selectCType;
	private JButton removeCType;
	private JComboBox<String> requestType;
	private JButton addCArg;
	private JButton removeCArg;
	private JButton delete;
	private JButton addConstant;
	private JButton removeConstant;
	private JButton downConstant;
	private JButton upConstant;
	private JButton saveSeparator;
	private JButton resetSeparator;
	
	public ResponderGUIPanel() {
		setBounds(new Rectangle(0, 0, 945, 200));
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		
		url = new JLabel("url");
		url.setFont(new Font("Tahoma", Font.PLAIN, 11));
		url.setForeground(Color.BLACK);
		url.setBackground(Color.WHITE);
		url.setBounds(74, 35, 120, 13);
		add(url);
		
		JLabel lblNewLabel = new JLabel("Responder");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(10, 10, 58, 13);
		add(lblNewLabel);
		
		nameValue = new JTextField();
		nameValue.setColumns(10);
		nameValue.setBounds(74, 6, 120, 20);
		add(nameValue);
		
		saveName = new JButton("Save");
		saveName.setBounds(204, 6, 85, 21);
		add(saveName);
		
		resetName = new JButton("Reset");
		resetName.setBounds(299, 6, 85, 21);
		add(resetName);
		
		log = new JCheckBox("Log");
		log.setForeground(Color.WHITE);
		log.setFont(new Font("Tahoma", Font.PLAIN, 11));
		log.setBackground(Color.DARK_GRAY);
		log.setBounds(390, 6, 95, 21);
		add(log);
		
		JLabel lblNewLabel_1 = new JLabel("Header");
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_1.setBounds(20, 35, 46, 13);
		add(lblNewLabel_1);
		
		selectUrl = new JButton("Select");
		selectUrl.setMargin(new Insets(2, 5, 2, 5));
		selectUrl.setBounds(204, 31, 56, 21);
		add(selectUrl);
		
		userAgent = new JLabel("User Agent");
		userAgent.setForeground(Color.BLACK);
		userAgent.setFont(new Font("Tahoma", Font.PLAIN, 11));
		userAgent.setBackground(Color.WHITE);
		userAgent.setBounds(74, 64, 120, 13);
		add(userAgent);
		
		selectAgent = new JButton("Select");
		selectAgent.setMargin(new Insets(2, 5, 2, 5));
		selectAgent.setBounds(204, 58, 56, 21);
		add(selectAgent);
		
		removeAgent = new JButton("Remove");
		removeAgent.setMargin(new Insets(2, 5, 2, 5));
		removeAgent.setBounds(270, 58, 56, 21);
		add(removeAgent);
		
		contentType = new JLabel("Content Type");
		contentType.setForeground(Color.BLACK);
		contentType.setFont(new Font("Tahoma", Font.PLAIN, 11));
		contentType.setBackground(Color.WHITE);
		contentType.setBounds(74, 93, 120, 13);
		add(contentType);
		
		selectCType = new JButton("Select");
		selectCType.setMargin(new Insets(2, 5, 2, 5));
		selectCType.setBounds(204, 87, 56, 21);
		add(selectCType);
		
		removeCType = new JButton("Remove");
		removeCType.setMargin(new Insets(2, 5, 2, 5));
		removeCType.setBounds(270, 87, 56, 21);
		add(removeCType);
		
		requestType = new JComboBox();
		requestType.setBounds(270, 31, 56, 21);
		add(requestType);
		
		JScrollPane customArgsScroll = new JScrollPane();
		customArgsScroll.setBorder(null);
		customArgsScroll.setBounds(74, 116, 252, 50);
		add(customArgsScroll);

		customArgsList = new JList();
		customArgsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		customArgsList.setForeground(Color.WHITE);
		customArgsList.setBackground(Color.GRAY);
		customArgsList.setModel(new DefaultListModel<ListElement>());
		customArgsScroll.setViewportView(customArgsList);
		
		addCArg = new JButton("Add Arg");
		addCArg.setBounds(74, 173, 120, 21);
		add(addCArg);
		
		removeCArg = new JButton("Remove Arg");
		removeCArg.setBounds(206, 173, 120, 21);
		add(removeCArg);
		
		JLabel label_1 = new JLabel("Body");
		label_1.setForeground(Color.WHITE);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_1.setBounds(336, 35, 46, 13);
		add(label_1);
		
		separator = new JTextField();
		separator.setBounds(445, 32, 96, 19);
		add(separator);
		separator.setColumns(10);
		
		JLabel label_2 = new JLabel("Separator:");
		label_2.setForeground(Color.WHITE);
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_2.setBounds(390, 35, 55, 13);
		add(label_2);
		
		JScrollPane constantsScroll = new JScrollPane();
		constantsScroll.setBorder(null);
		constantsScroll.setBounds(336, 58, 599, 108);
		add(constantsScroll);

		constantsList = new JList();
		constantsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		constantsList.setForeground(Color.WHITE);
		constantsList.setBackground(Color.GRAY);
		constantsList.setModel(new DefaultListModel<ListElement>());
		constantsScroll.setViewportView(constantsList);
		
		delete = new JButton("Delete");
		delete.setFont(new Font("Tahoma", Font.PLAIN, 11));
		delete.setBounds(815, 6, 120, 42);
		add(delete);
		
		addConstant = new JButton("Add Constant");
		addConstant.setBounds(336, 173, 140, 21);
		add(addConstant);
		
		removeConstant = new JButton("Remove Constant");
		removeConstant.setBounds(486, 173, 140, 21);
		add(removeConstant);
		
		downConstant = new JButton("Down");
		downConstant.setBounds(795, 173, 140, 21);
		add(downConstant);
		
		upConstant = new JButton("Up");
		upConstant.setBounds(645, 173, 140, 21);
		add(upConstant);
		
		saveSeparator = new JButton("Save");
		saveSeparator.setBounds(551, 31, 85, 21);
		add(saveSeparator);
		
		resetSeparator = new JButton("Reset");
		resetSeparator.setBounds(646, 31, 85, 21);
		add(resetSeparator);
		
		saveName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (SettingHandler.matchesRegex(SettingHandler.REGEXNAME, nameValue.getText())) {
					responder.setName(nameValue.getText());
				} else {
					Main.popupMessage("Error - The name does not match the Regex " + SettingHandler.REGEXNAME);
				}
			}
		});
		
		resetName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nameValue.setText(responder.getName());
			}
		});
		
		log.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				responder.setLog(log.isSelected());
			}
		});
		
		selectUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ListElement selection = ParamSelector.getSelection(Main.frame, ConstantHandler.getConstantElements(), "URL");
				if (selection != null) {
					responder.getHeader().setUrl(selection.getID());
					url.setText(selection.toString());
				}
			}
		});
		
		requestType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				responder.getHeader().setRequestType((String)requestType.getSelectedItem());
			}
		});
		
		selectAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ListElement selection = ParamSelector.getSelection(Main.frame, ConstantHandler.getConstantElements(), "User-Agent");
				if (selection != null) {
					responder.getHeader().setUserAgent(selection.getID());
					userAgent.setText(selection.toString());
				}
			}
		});
		
		removeAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				responder.getHeader().setUserAgent("");
				userAgent.setText("User-Agent");
			}
		});
		
		selectCType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ListElement selection = ParamSelector.getSelection(Main.frame, ConstantHandler.getConstantElements(), "Content-Type");
				if (selection != null) {
					responder.getHeader().setContentType(selection.getID());
					contentType.setText(selection.toString());
				}
			}
		});
		
		removeCType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				responder.getHeader().setContentType("");
				contentType.setText("Content-Type");
			}
		});
		
		addCArg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ListElement selection = ParamSelector.getSelection(Main.frame, ConstantHandler.getConstantElements(), "Custom Argument");
				if (selection != null) {
					responder.getHeader().getCustomArgs().add(selection.getID());
					populate();
				}
			}
		});
		
		removeCArg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (customArgsList.getSelectedIndex() != -1) {
					responder.getHeader().getCustomArgs().remove(customArgsList.getSelectedIndex());
					populate();
				}
			}
		});
		
		saveSeparator.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				responder.getBody().setSeparator(separator.getText());
			}
		});
		
		resetSeparator.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				separator.setText(responder.getBody().getSeparator());
			}
		});
		
		addConstant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ListElement selection = ParamSelector.getSelection(Main.frame, ConstantHandler.getConstantElements(), "Body Content");
				if (selection != null) {
					responder.getBody().getContent().add(selection.getID());
					populate();
				}
			}
		});
		
		removeConstant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (constantsList.getSelectedIndex() != -1) {
					responder.getBody().getContent().remove(constantsList.getSelectedIndex());
					populate();
				}
			}
		});
		
		upConstant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (constantsList.getSelectedIndex() != -1 && constantsList.getSelectedIndex() > 0) {
					String id = responder.getBody().getContent().get(constantsList.getSelectedIndex());
					responder.getBody().getContent().remove(id);
					responder.getBody().getContent().add(constantsList.getSelectedIndex() - 1, id);
					populate();
				}
			}
		});
		
		downConstant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (constantsList.getSelectedIndex() != -1 && constantsList.getSelectedIndex() < responder.getBody().getContent().size() - 1) {
					String id = responder.getBody().getContent().get(constantsList.getSelectedIndex());
					responder.getBody().getContent().remove(id);
					responder.getBody().getContent().add(constantsList.getSelectedIndex() + 1, id);
					populate();
				}
			}
		});
		
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GroupHandler.getResponderHandler(GroupHandler.rtgID(responder.getResponderID())).removeResponder(responder.getResponderID());
				Main.frame.respondersMode(GroupHandler.getResponderHandler(GroupHandler.rtgID(responder.getResponderID())));
			}
		});
		
	}

	public void init(Responder responder) {
		this.responder = responder;
		populate();
	}
	
	private void populate() {
		
		log.setSelected(responder.getLog());
		nameValue.setText(responder.getName());
		url.setText(ConstantHandler.getConstantNames().get(responder.getHeader().getUrl()));
		if (responder.getHeader().getUserAgent() != null && !responder.getHeader().getUserAgent().isEmpty()) {
			userAgent.setText(ConstantHandler.getConstantNames().get(responder.getHeader().getUserAgent()));
		}
		if (responder.getHeader().getContentType() != null && !responder.getHeader().getContentType().isEmpty()) {
			contentType.setText(ConstantHandler.getConstantNames().get(responder.getHeader().getContentType()));
		}
		
		requestType.removeAllItems();
		requestType.addItem("auto");
		requestType.addItem("GET");
		requestType.addItem("GET");
		requestType.addItem("HEAD");
		requestType.setSelectedItem(responder.getHeader().getRequestType());
		
		separator.setText(responder.getBody().getSeparator());
		
		List<String> customArgs = responder.getHeader().getCustomArgs();
		DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) customArgsList.getModel();
		model.clear();
		for (String customArg : customArgs) {
			model.addElement(new ListElement(customArg, ConstantHandler.getConstantNames().get(customArg)));
		}
		
		List<String> content = responder.getBody().getContent();
		DefaultListModel model1 = (DefaultListModel) constantsList.getModel();
		model1.clear();
		for (String constant : content) {
			model1.addElement(new ListElement(constant, ConstantHandler.getConstantNames().get(constant)));
		}
	}
}
