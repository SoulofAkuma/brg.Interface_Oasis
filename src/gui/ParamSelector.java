package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ParamSelector extends JDialog {

	private ListElement selected;
	private static ParamSelector dialog;
	
	public static ListElement getSelection(Frame parent, ListElement[] values, String selecting) {
		
		dialog = new ParamSelector(parent, values, selecting);
		dialog.setVisible(true);
		return dialog.getValue();
	}
	
	private ParamSelector(Frame parent, ListElement[] values, String selecting) {
		
		super(parent, "Select " + selecting, true);
		getContentPane().setBackground(Color.DARK_GRAY);
		
		getContentPane().setLayout(null);
		
		JList list = new JList();
		list.setBackground(Color.GRAY);
		getContentPane().add(list);
		list.setModel(new DefaultListModel<ListElement>());
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		for (ListElement element : values) {
			((DefaultListModel<ListElement>)list.getModel()).addElement(element);
		}
		
		JScrollPane scrollPane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBackground(Color.LIGHT_GRAY);
		scrollPane.setBounds(20, 10, 390, 200);
		getContentPane().add(scrollPane);
		list.setPreferredSize(new Dimension(370, 180));
		
		JButton select = new JButton("Select");
		select.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (list.getSelectedIndex() == -1) {
					return;
				}
				DefaultListModel<ListElement> model = (DefaultListModel<ListElement>) list.getModel();
				selected = model.get(list.getSelectedIndex());
				dialog.setVisible(false);
			}
		});
		select.setBounds(317, 221, 89, 23);
		getContentPane().add(select);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected = null;
				dialog.setVisible(false);
			}
		});
		cancel.setBounds(218, 221, 89, 23);
		getContentPane().add(cancel);

		this.setSize(new Dimension(440, 290));
		
		getRootPane().setDefaultButton(select);
		setLocationRelativeTo(parent);
	}
	
	private ListElement getValue() {
		return this.selected;
	}
}
