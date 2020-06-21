package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import cc.Pair;
import indexassigner.IndexAssigner;
import indexassigner.IndexAssignerHandler;
import settings.IDType;
import settings.SettingHandler;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Insets;

public class IndexAssignerGUIPanel extends JPanel {
	
	private JTextField nameValue;
	private JList indexesList;
	private JList regexesList;
	private JButton saveName;
	private JButton resetName;
	private JButton delete;
	private JButton indexAdd;
	private JButton indexRemove;
	private JButton indexUp;
	private JButton indexDown;
	private JButton regexAdd;
	private JButton regexRemove;
	private JButton regexUp;
	private JButton regexDown;
	private JCheckBox rmMatch;
	
	private IndexAssigner assigner;
	
	public IndexAssignerGUIPanel() {
		setBackground(Color.DARK_GRAY);
		setBounds(new Rectangle(0, 0, 945, 110));
		setLayout(null);
		
		delete = new JButton("Delete");
		delete.setBounds(88, 6, 76, 21);
		add(delete);
		
		JLabel lblNewLabel = new JLabel("IndexAssigner");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(10, 10, 82, 13);
		add(lblNewLabel);
		
		nameValue = new JTextField();
		nameValue.setBounds(10, 56, 150, 19);
		add(nameValue);
		nameValue.setColumns(10);
		
		saveName = new JButton("Save");
		saveName.setBounds(10, 80, 70, 21);
		add(saveName);
		
		resetName = new JButton("Reset");
		resetName.setBounds(90, 80, 70, 21);
		add(resetName);
		
		JScrollPane indexesScroll = new JScrollPane();
		indexesScroll.setBorder(null);
		indexesScroll.setBounds(170, 9, 380, 65);
		add(indexesScroll);

		indexesList = new JList();
		indexesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		indexesList.setForeground(Color.WHITE);
		indexesList.setBackground(Color.GRAY);
		indexesList.setModel(new DefaultListModel<ListElement>());
		indexesScroll.setViewportView(indexesList);
		
		JScrollPane regexesScroll = new JScrollPane();
		regexesScroll.setBorder(null);
		regexesScroll.setBounds(560, 9, 380, 65);
		add(regexesScroll);

		regexesList = new JList();
		regexesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		regexesList.setForeground(Color.WHITE);
		regexesList.setBackground(Color.GRAY);
		regexesList.setModel(new DefaultListModel<ListElement>());
		regexesScroll.setViewportView(regexesList);
		
		indexAdd = new JButton("Add Index");
		indexAdd.setBounds(170, 80, 85, 21);
		add(indexAdd);
		
		indexRemove = new JButton("Remove Index");
		indexRemove.setMargin(new Insets(2, 5, 2, 5));
		indexRemove.setBounds(270, 80, 85, 21);
		add(indexRemove);
		
		indexUp = new JButton("Up");
		indexUp.setBounds(368, 80, 85, 21);
		add(indexUp);
		
		indexDown = new JButton("Down");
		indexDown.setBounds(465, 80, 85, 21);
		add(indexDown);
		
		rmMatch = new JCheckBox("Remove Match");
		rmMatch.setForeground(Color.WHITE);
		rmMatch.setBackground(Color.DARK_GRAY);
		rmMatch.setBounds(10, 32, 95, 21);
		add(rmMatch);
		
		regexAdd = new JButton("Add Regex");
		regexAdd.setBounds(560, 80, 85, 21);
		add(regexAdd);
		
		regexRemove = new JButton("Remove Index");
		regexRemove.setMargin(new Insets(2, 5, 2, 5));
		regexRemove.setBounds(660, 80, 85, 21);
		add(regexRemove);
		
		regexUp = new JButton("Up");
		regexUp.setBounds(758, 80, 85, 21);
		add(regexUp);
		
		regexDown = new JButton("Down");
		regexDown.setBounds(855, 80, 85, 21);
		add(regexDown);
		
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IndexAssignerHandler.removeIndexAssigner(assigner.getId());
				Main.frame.assignersMode();
			}
		});
		
		saveName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (SettingHandler.matchesRegex(SettingHandler.REGEXNAME, nameValue.getText())) {
					assigner.setName(nameValue.getText());
				} else {
					Main.popupMessage("Error - the name must match the following id " + SettingHandler.REGEXNAME);
				}
			}
		});
		
		resetName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nameValue.setText(assigner.getName());
			}
		});
		
		indexAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = StringInput.getSelection(Main.frame, "Position");
				if (input != null && SettingHandler.matchesRegex("[0-9]+", input)) {
					String key = StringInput.getSelection(Main.frame, "Key");
					if (key != null && !key.isEmpty()) {
						assigner.addIndex(SettingHandler.getNewID(IDType.Index), Integer.parseInt(input), key);
					}
				} else if (input == null) {
					return;
				} else {
					Main.popupMessage("Error - please enter a number as a position");
				}
				populate();
			}
		});
		
		regexAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = StringInput.getSelection(Main.frame, "Regex");
				String keys = StringInput.getSelection(Main.frame, "Keys seperated by comma");
				String defInd = StringInput.getSelection(Main.frame, "Default Index");
				if (defInd == null || keys == null || input == null || input.isEmpty() || keys.isEmpty() || defInd.isEmpty()) {
					return;
				} else if (!SettingHandler.matchesRegex("[0-9]+", defInd)) {
					Main.popupMessage("Error - The default index must be a number");
					return;
				}
				assigner.addRegex(SettingHandler.getNewID(IDType.Regex), input, keys.split(","), Integer.parseInt(defInd));
				populate();
			}
		});
		
		indexRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) indexesList.getModel();
				if (indexesList.getSelectedIndex() != -1) {
					assigner.removeIndex(model.get(indexesList.getSelectedIndex()).getID());
					populate();
				}
			}
		});
		
		regexRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) regexesList.getModel();
				if (regexesList.getSelectedIndex() != -1) {
					assigner.removeIndex(model.get(regexesList.getSelectedIndex()).getID());
					populate();
				}
			}
		});
		
		indexUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) indexesList.getModel();
				if (indexesList.getSelectedIndex() != -1 && indexesList.getSelectedIndex() > 0) {
					assigner.changeIndexPosition(model.get(indexesList.getSelectedIndex()).getID(), indexesList.getSelectedIndex() - 1);
					populate();
				}
			}
		});
		
		regexUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) regexesList.getModel();
				if (regexesList.getSelectedIndex() != -1 && regexesList.getSelectedIndex() > 0) {
					assigner.changeRegexPosition(model.get(regexesList.getSelectedIndex()).getID(), regexesList.getSelectedIndex() - 1);
					populate();
				}
			}
		});
		
		indexDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) indexesList.getModel();
				if (indexesList.getSelectedIndex() != -1 && indexesList.getSelectedIndex() < assigner.getIorder().size() - 1) {
					assigner.changeIndexPosition(model.get(indexesList.getSelectedIndex()).getID(), indexesList.getSelectedIndex() + 1);
					populate();
				}
			}
		});
		
		regexDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) regexesList.getModel();
				if (regexesList.getSelectedIndex() != -1 && regexesList.getSelectedIndex() < assigner.getRorder().size() - 1) {
					assigner.changeRegexPosition(model.get(regexesList.getSelectedIndex()).getID(), regexesList.getSelectedIndex() + 1);
					populate();
				}
			}
		});
		
		rmMatch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				assigner.setRmMatch(rmMatch.isSelected());
			}
		});
	}
	
	public void init(IndexAssigner assigner) {
		this.assigner = assigner;
		populate();
	}
	
	public void populate() {
		
		rmMatch.setSelected(assigner.getRmMatch());
		nameValue.setText(assigner.getName());
		
		List<String> iorder = assigner.getIorder();
		ConcurrentHashMap<String, Pair<Integer, String>> indexes = assigner.getIndexes();
		DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) indexesList.getModel();
		model.clear();
		for (String index : iorder) {
			Pair<Integer, String> indexKvp = indexes.get(index);
			model.addElement(new ListElement(index, String.valueOf(indexKvp.getKey()) + " - " + indexKvp.getValue()));
		}
		
		List<String> rorder = assigner.getRorder();
		ConcurrentHashMap<String, Pair<String, String[]>> regexes = assigner.getRegexes();
		ConcurrentHashMap<String, Integer> defInds = assigner.getDefInd();
		DefaultListModel<ListElement> model1 = (DefaultListModel<ListElement>) regexesList.getModel();
		model1.clear();
		for (String regex : rorder) {
			Pair<String, String[]> regexKvp = regexes.get(regex);
			model1.addElement(new ListElement(regex, regexKvp.getKey() + " - " + String.valueOf(defInds.get(regex)) + "; " + String.join(", ", regexKvp.getValue())));
		}
	}
}
