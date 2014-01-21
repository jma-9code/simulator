package fr.ensicaen.gui_simulator.gui.panel;

import fr.ensicaen.gui_simulator.gui.bridge.ComponentWrapper;
import fr.ensicaen.gui_simulator.gui.bridge.MapJTableBridge;
import fr.ensicaen.gui_simulator.gui.bridge.MediatorWrapper;
import fr.ensicaen.gui_simulator.gui.bridge.StrategyComboBoxBridge;
import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.strategies.IStrategy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraphSelectionModel;

public class ComponentPanel extends JTabbedPane implements mxIEventListener, ListSelectionListener {

	// properties table tab - BEGIN
	private JTable propertiesTable;
	private MapJTableBridge propertiesModelTable;
	private List<JButton> buttons = new ArrayList<>(2);

	// properties table tab - END

	// detail panel tab - BEGIN
	private JLabel valName;
	private JLabel valIdentifier;
	private JComboBox<IStrategy> valStrategy;
	private StrategyComboBoxBridge strategyModelComboBox;

	// detail panel tab - END

	public ComponentPanel(BasicGraphEditor frame) {
		// tab
		addTab(mxResources.get("detail"), new JScrollPane(initTab_detailPanel()));
		addTab(mxResources.get("attributes"), new JScrollPane(initTab_propertiesTable()));
	}

	private JPanel initTab_detailPanel() {
		JPanel detailPanelTab = new JPanel();
		SpringLayout layout = new SpringLayout();
		detailPanelTab.setLayout(layout);

		JLabel lblName = new JLabel(mxResources.get("name"));
		layout.putConstraint(SpringLayout.NORTH, lblName, 10, SpringLayout.NORTH, detailPanelTab);
		layout.putConstraint(SpringLayout.EAST, lblName, 65, SpringLayout.WEST, detailPanelTab);
		detailPanelTab.add(lblName);

		valName = new JLabel("<nothing selected>");
		layout.putConstraint(SpringLayout.NORTH, valName, 0, SpringLayout.NORTH, lblName);
		layout.putConstraint(SpringLayout.WEST, valName, 10, SpringLayout.EAST, lblName);
		detailPanelTab.add(valName);

		JLabel lblIdentifier = new JLabel(mxResources.get("identifier"));
		layout.putConstraint(SpringLayout.NORTH, lblIdentifier, 10, SpringLayout.SOUTH, lblName);
		layout.putConstraint(SpringLayout.EAST, lblIdentifier, 0, SpringLayout.EAST, lblName);
		detailPanelTab.add(lblIdentifier);

		valIdentifier = new JLabel("<nothing selected>");
		valIdentifier.setPreferredSize(new Dimension(155, 20));
		layout.putConstraint(SpringLayout.NORTH, valIdentifier, 0, SpringLayout.NORTH, lblIdentifier);
		layout.putConstraint(SpringLayout.WEST, valIdentifier, 10, SpringLayout.EAST, lblIdentifier);
		detailPanelTab.add(valIdentifier);

		JLabel lblStrategy = new JLabel(mxResources.get("strategy"));
		layout.putConstraint(SpringLayout.NORTH, lblStrategy, 10, SpringLayout.SOUTH, lblIdentifier);
		layout.putConstraint(SpringLayout.EAST, lblStrategy, 0, SpringLayout.EAST, lblIdentifier);
		detailPanelTab.add(lblStrategy);

		strategyModelComboBox = new StrategyComboBoxBridge();
		valStrategy = new JComboBox<>(strategyModelComboBox);
		valStrategy.setPreferredSize(new Dimension(155, 20));
		layout.putConstraint(SpringLayout.NORTH, valStrategy, 0, SpringLayout.NORTH, lblStrategy);
		layout.putConstraint(SpringLayout.WEST, valStrategy, 10, SpringLayout.EAST, lblStrategy);
		detailPanelTab.add(valStrategy);

		return detailPanelTab;
	}

	private JPanel initTab_propertiesTable() {
		JPanel propertiesTableTab = new JPanel();
		propertiesTableTab.setLayout(new BorderLayout());

		// model
		propertiesModelTable = new MapJTableBridge();

		// view
		propertiesTable = new JTable(propertiesModelTable);
		// propertiesTable.setBackground(null);
		propertiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		propertiesTable.setRowHeight(20);
		propertiesTable.getSelectionModel().addListSelectionListener(this);

		// view header
		JTableHeader header = propertiesTable.getTableHeader();
		header.setResizingAllowed(true);
		header.setReorderingAllowed(false);

		// sub panel for button
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.WHITE);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		buttonsPanel.add(initButton("/com/mxgraph/examples/swing/images/new.gif", new AddEntryAction()));
		buttonsPanel.add(initButton("/com/mxgraph/examples/swing/images/delete.gif", new DeleteEntryAction()));

		// add in parent layout
		propertiesTableTab.add(header, BorderLayout.NORTH);
		propertiesTableTab.add(propertiesTable, BorderLayout.CENTER);
		propertiesTableTab.add(buttonsPanel, BorderLayout.SOUTH);

		// init
		updateDetailPanel(null);

		return propertiesTableTab;
	}

	private JButton initButton(String iconPath, ActionListener action) {
		JButton button = new JButton(new ImageIcon(ComponentPanel.class.getResource(iconPath)));
		button.addActionListener(action);
		button.setEnabled(false);

		// register
		buttons.add(button);

		return button;
	}

	/**
	 * 
	 * @param index
	 *            Numéro de l'index ou -1 pour tous.
	 * @param enable
	 */
	private void setButtonsState(int index, boolean enable) {
		if (index == -1) {
			for (JButton btn : buttons) {
				btn.setEnabled(enable);
			}
		}
		else {
			buttons.get(index).setEnabled(enable);
		}
	}

	@Override
	public void invoke(Object _selectionModel, mxEventObject e) {
		mxGraphSelectionModel selectionModel = (mxGraphSelectionModel) _selectionModel;

		// dans tous les cas il faudra sélectionner une ligne pour
		// effectuer des actions dessus (par défaut rien n'est sélectionné)
		setButtonsState(-1, false);
		updateDetailPanel(null);

		if (selectionModel.size() != 1) {
			propertiesModelTable.reset();
		}
		else {
			Object sel = ((mxICell) selectionModel.getCell()).getValue();

			if (sel instanceof ComponentWrapper) {
				Component c = ((ComponentWrapper) sel).getComponent();

				propertiesModelTable.update(c.getProperties());
				setButtonsState(0, true);
				updateDetailPanel(c);

			}
			else if (sel instanceof MediatorWrapper) {
				propertiesModelTable.update(((MediatorWrapper) sel).getMediator().getProperties());
				setButtonsState(0, true);
			}
			else {
				propertiesModelTable.reset();
			}
		}
	}

	private void updateDetailPanel(Component c) {
		if (c != null) {
			this.valName.setText(c.getName());
			this.valIdentifier.setText(c.getInstanceName());
			this.strategyModelComboBox.update(c);
			this.valStrategy.setEnabled(true);
		}
		else {
			this.valName.setText("");
			this.valIdentifier.setText("");
			this.valStrategy.setEnabled(false);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (propertiesTable.getSelectedRow() != -1) {
			setButtonsState(1, true);
		}
		else {
			setButtonsState(1, false);
		}

		setButtonsState(0, !propertiesModelTable.isAddRowEnabled());
	}

	private class AddEntryAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			propertiesModelTable.enableAddRow();
			setButtonsState(0, false);
		}

	}

	private class DeleteEntryAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int rep = JOptionPane.showConfirmDialog(ComponentPanel.this, mxResources.get("delete_confirmation"),
					mxResources.get("delete_confirmation_title"), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (rep == JOptionPane.YES_OPTION) {
				propertiesModelTable.deleteRow(propertiesTable.getSelectedRow());
				setButtonsState(0, true);
			}
		}
	}

}
