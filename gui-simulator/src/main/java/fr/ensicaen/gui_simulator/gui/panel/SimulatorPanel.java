package fr.ensicaen.gui_simulator.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxResources;

import fr.ensicaen.gui_simulator.gui.bridge.ComponentWrapper;
import fr.ensicaen.gui_simulator.gui.bridge.MediatorWrapper;
import fr.ensicaen.gui_simulator.gui.bridge.SimulatorGUIBridge;
import fr.ensicaen.gui_simulator.gui.bridge.StartPointJTableBridge;
import fr.ensicaen.gui_simulator.gui.editor.DateTimeCellEditor;
import fr.ensicaen.gui_simulator.gui.renderer.DateTimeCellRenderer;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.factory.listener.MediatorFactoryListener;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.mediator.listener.MediatorListener;
import fr.ensicaen.simulator.simulator.AsyncSimulator;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.Simulator;
import fr.ensicaen.simulator.simulator.SimulatorFactory;
import fr.ensicaen.simulator.simulator.exception.SimulatorException;
import fr.ensicaen.simulator.simulator.listener.SimulatorListener;

public class SimulatorPanel extends JTabbedPane implements
		ListSelectionListener, SimulatorListener, MediatorListener,
		MediatorFactoryListener {

	private JTable startPointTable;
	private StartPointJTableBridge startPointModelTable;
	private List<JButton> buttons = new ArrayList<>(2);
	private JButton btnLaunch = new JButton("Launch");
	private JButton btnOneStep = new JButton("One Step");
	private AsyncSimulator sim = SimulatorFactory.getAsyncSimulator();
	private SimulatorPanel himself = null;
	private BasicGraphEditor bge_frame = null;

	public SimulatorPanel(BasicGraphEditor frame) {
		// tab
		addTab(mxResources.get("start_points"), new JScrollPane(
				initTab_startPointTable()));
		addTab(mxResources.get("simulator"), new JScrollPane(
				initTab_simulatorPanel()));
		// ajout du listener sur la simulation
		sim.addListener(this);
		himself = this;
		bge_frame = frame;
	}

	private JPanel initTab_startPointTable() {
		JPanel startPointTableTab = new JPanel();
		startPointTableTab.setLayout(new BorderLayout());

		// model
		Context ctx = Context.getInstance();
		// ctx.addStartPoint(new Date(), "COUCOU");
		// ctx.addStartPoint(new Date(System.currentTimeMillis() + 3600 * 24 *
		// 5), "LOL");
		startPointModelTable = new StartPointJTableBridge(
				ctx.getUserStartPoints());

		// view
		startPointTable = new JTable(startPointModelTable);
		// propertiesTable.setBackground(null);
		startPointTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		startPointTable.setRowHeight(20);
		startPointTable.getSelectionModel().addListSelectionListener(this);

		TableColumn col = startPointTable.getColumnModel().getColumn(0);
		col.setCellEditor(new DateTimeCellEditor());
		col.setCellRenderer(new DateTimeCellRenderer());
		col.setMinWidth(100);

		// view header
		JTableHeader header = startPointTable.getTableHeader();
		header.setResizingAllowed(true);
		header.setReorderingAllowed(false);

		// sub panel for button
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.WHITE);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		buttonsPanel.add(initButton(
				"/com/mxgraph/examples/swing/images/new.gif",
				new AddEntryAction()));
		buttonsPanel.add(initButton(
				"/com/mxgraph/examples/swing/images/delete.gif",
				new DeleteEntryAction()));
		setButtonsState(0, true);

		// add in parent layout
		startPointTableTab.add(header, BorderLayout.NORTH);
		startPointTableTab.add(startPointTable, BorderLayout.CENTER);
		startPointTableTab.add(buttonsPanel, BorderLayout.SOUTH);

		return startPointTableTab;
	}

	private JPanel initTab_simulatorPanel() {
		final JPanel simulatorPanel = new JPanel();

		btnLaunch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnLaunch.getText().equalsIgnoreCase("launch")) {
					try {
						// ajout des listeners des mediateurs statiques
						for (Mediator m : Context.getInstance().getMediators()) {
							m.addListener(himself);
						}
						// ajout d'un listener pour recuperer les mediateurs
						// dynamiques
						MediatorFactory.getInstance().addListener(himself);
						sim.start();
					} catch (SimulatorException e1) {
						e1.printStackTrace();
					}
				} else {
					Simulator.resume();
					btnOneStep.setEnabled(false);
					btnLaunch.setText("Launch");
				}
				simulatorPanel.repaint();
			}
		});

		simulatorPanel.add(btnLaunch);
		btnOneStep.addActionListener(new BtnOneStepActionListener());

		btnOneStep.setEnabled(false);
		simulatorPanel.add(btnOneStep);
		return simulatorPanel;
	}

	private JButton initButton(String iconPath, ActionListener action) {
		JButton button = new JButton(new ImageIcon(
				SimulatorPanel.class.getResource(iconPath)));
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
		} else {
			buttons.get(index).setEnabled(enable);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (startPointTable.getSelectedRow() != -1) {
			setButtonsState(1, true);
		} else {
			setButtonsState(1, false);
		}

		setButtonsState(0, !startPointModelTable.isAddRowEnabled());
	}

	private class AddEntryAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			startPointModelTable.enableAddRow();
			setButtonsState(0, false);
		}

	}

	private class DeleteEntryAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int rep = JOptionPane.showConfirmDialog(SimulatorPanel.this,
					mxResources.get("delete_confirmation"),
					mxResources.get("delete_confirmation_title"),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (rep == JOptionPane.YES_OPTION) {
				startPointModelTable
						.deleteRow(startPointTable.getSelectedRow());
				setButtonsState(0, true);
			}
		}
	}

	private class BtnOneStepActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Simulator.iterateStep();
		}
	}

	public void refresh() {
		startPointModelTable.fireTableDataChanged();
	}

	@Override
	public void simulationStarted() {
		Simulator.pausable();
		btnOneStep.setEnabled(true);
		btnLaunch.setText("Skip");
		restoreComponentColor();
	}

	@Override
	public void simulationEnded() {
		btnOneStep.setEnabled(false);
		btnLaunch.setText("Launch");
		restoreComponentColor();
	}

	private void restoreComponentColor() {
		List<mxCell> cells = SimulatorGUIBridge.findAllCell(bge_frame
				.getGraphComponent().getGraph());

		// celltracker.hi
		for (mxCell c : cells) {
			bge_frame
					.getGraphComponent()
					.getGraph()
					.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#adc5ff",
							new Object[] { c });

		}
	}

	@Override
	public void onSendData(Mediator m, IOutput sender, String data) {
		// all cell in blue
		List<mxCell> cells = SimulatorGUIBridge.findAllCell(bge_frame
				.getGraphComponent().getGraph());
		mxCell cell_sender = null;
		mxCell cell_receiver = null;
		mxCell cell_mediator = null;

		for (mxCell c : cells) {
			if (c.getValue() instanceof ComponentWrapper) {
				if (((ComponentWrapper) c.getValue()).getComponent().equals(
						m.getSender())) {
					cell_sender = c;
				} else if (((ComponentWrapper) c.getValue()).getComponent()
						.equals(m.getReceiver())) {
					cell_receiver = c;
				}
			} else if (c.getValue() instanceof MediatorWrapper) {
				if (((MediatorWrapper) c.getValue()).getMediator().equals(m)) {
					cell_mediator = c;
				}
			}
			bge_frame
					.getGraphComponent()
					.getGraph()
					.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#adc5ff",
							new Object[] { c });

		}

		// concerned component
		if (cell_sender != null) {
			ComponentWrapper cw = (ComponentWrapper) cell_sender.getValue();
			bge_frame
					.getGraphComponent()
					.getGraph()
					.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#FF463D",
							new Object[] { cell_sender });
		}

		if (cell_receiver != null) {
			ComponentWrapper cw = (ComponentWrapper) cell_receiver.getValue();
			bge_frame
					.getGraphComponent()
					.getGraph()
					.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#30FF3A",
							new Object[] { cell_receiver });
		}

		// concerned mediator
		if (cell_mediator == null) {
			Map<String, Object> map = new HashMap<>();
			map.put(m.getUuid(), new MediatorWrapper(m));
			cell_mediator = SimulatorGUIBridge.createEdge(m, map, bge_frame
					.getGraphComponent().getGraph());
			bge_frame
					.getGraphComponent()
					.getGraph()
					.addEdge(cell_mediator, null, cell_sender, cell_receiver,
							null);
		}
		bge_frame
				.getGraphComponent()
				.getGraph()
				.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#30FF3A",
						new Object[] { cell_mediator });

		// refresh frame
		bge_frame.getGraphComponent().refresh();
	}

	@Override
	public void addMediator(Mediator m) {
		m.addListener(himself);
	}

	@Override
	public void addImplicitMediator(Mediator m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeMediator(Mediator m) {
		// TODO Auto-generated method stub

	}
}
