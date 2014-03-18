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
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;

import fr.ensicaen.gui_simulator.gui.bridge.ComponentWrapper;
import fr.ensicaen.gui_simulator.gui.bridge.MediatorWrapper;
import fr.ensicaen.gui_simulator.gui.bridge.SimulatorGUIBridge;
import fr.ensicaen.gui_simulator.gui.bridge.StartPointJTableBridge;
import fr.ensicaen.gui_simulator.gui.editor.DateTimeCellEditor;
import fr.ensicaen.gui_simulator.gui.renderer.DateTimeCellRenderer;
import fr.ensicaen.gui_simulator.gui.tools.MediatorAnalysisPanel;
import fr.ensicaen.simulator.model.component.Component;
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
		MediatorFactoryListener, mxIEventListener {

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
		bge_frame.getGraphComponent().getGraph().getSelectionModel()
				.addListener(mxEvent.CHANGE, this);
		btnOneStep.setEnabled(true);
		btnLaunch.setText("Skip");
		restoreCellColor();
	}

	@Override
	public void simulationEnded() {
		btnOneStep.setEnabled(false);
		btnLaunch.setText("Launch");
		bge_frame.getGraphComponent().getGraph().getSelectionModel()
				.removeListener(this);
		restoreCellColor();
	}

	/**
	 * Restore default style for all components & Remove data in mediatorwrapper
	 */
	private void restoreCellColor() {
		List<mxCell> cells = SimulatorGUIBridge.findAllCell(bge_frame
				.getGraphComponent().getGraph());

		for (mxCell c : cells) {
			if (c.getValue() instanceof ComponentWrapper) {
				ComponentWrapper cw = (ComponentWrapper) c.getValue();
				c.setStyle(cw.getNormalStyle());

			} else if (c.getValue() instanceof MediatorWrapper) {
				MediatorWrapper cw = (MediatorWrapper) c.getValue();
				c.setStyle(cw.getStyle());
				// remove data in mediator
				cw.setData(null);
			}
		}
		bge_frame.getGraphComponent().refresh();
	}

	@Override
	public void onSendData(Mediator m, boolean response, String data) {
		mxGraph graph = bge_frame.getGraphComponent().getGraph();
		graph.fireEvent(new mxEventObject(SimulatorGUIBridge.EVT_PAUSE_CTX_SYNC));
		// all cell in default style
		restoreCellColor();

		// retrieve cells
		mxCell cell_sender = SimulatorGUIBridge.findVertex(
				(!response) ? (Component) m.getSender() : (Component) m
						.getReceiver(), graph);
		mxCell cell_receiver = SimulatorGUIBridge.findVertex(
				(!response) ? (Component) m.getReceiver() : (Component) m
						.getSender(), graph);
		mxCell cell_mediator = SimulatorGUIBridge.findEdge(m, graph);

		// concerned component
		if (cell_sender != null) {
			ComponentWrapper cw = (ComponentWrapper) cell_sender.getValue();
			cell_sender.setStyle(cw.getSenderStyle());
		}

		if (cell_receiver != null) {
			ComponentWrapper cw = (ComponentWrapper) cell_receiver.getValue();
			cell_receiver.setStyle(cw.getReceiverStyle());
		}

		// concerned mediator
		if (cell_mediator == null) {
			Map<String, Object> map = new HashMap<>();
			map.put(m.getUuid(), new MediatorWrapper(m));
			cell_mediator = SimulatorGUIBridge.createEdge(m, map, graph);
			graph.addEdge(cell_mediator, null, cell_sender, cell_receiver, null);
		}
		// useStyle
		cell_mediator.setStyle(((MediatorWrapper) cell_mediator.getValue())
				.getUseStyle());
		// add data in mediatorWrapper
		((MediatorWrapper) cell_mediator.getValue()).setData(data);

		graph.fireEvent(new mxEventObject(
				SimulatorGUIBridge.EVT_RESUME_CTX_SYNC));
		// refresh frame
		bge_frame.getGraphComponent().refresh();
	}

	@Override
	public void addMediator(Mediator m) {
		m.addListener(himself);
	}

	@Override
	public void removeMediator(Mediator m) {
		mxCell cell_mediator = SimulatorGUIBridge.findEdge(m, bge_frame
				.getGraphComponent().getGraph());
		if (cell_mediator != null) {
			bge_frame.getGraphComponent().getGraph()
					.removeCells(new Object[] { cell_mediator });

			// refresh frame
			bge_frame.getGraphComponent().refresh();
		}

	}

	@Override
	public void invoke(Object sender, mxEventObject evt) {
		// show data in mediator if its possible
		if (sender instanceof mxGraphSelectionModel) {
			mxGraphSelectionModel gsm = (mxGraphSelectionModel) sender;
			mxCell cell = (mxCell) gsm.getCell();
			// select mediator ?
			if (cell != null
					&& ((mxCell) cell).getValue() instanceof MediatorWrapper) {
				MediatorWrapper mw = (MediatorWrapper) ((mxCell) cell)
						.getValue();
				// contains data ?
				if (mw.getData() != null && !mw.getData().isEmpty()) {
					MediatorAnalysisPanel frame = new MediatorAnalysisPanel(mw);
					frame.setVisible(true);
				}
			}
		}

	}
}
