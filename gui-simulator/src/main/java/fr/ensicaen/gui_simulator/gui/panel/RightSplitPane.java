package fr.ensicaen.gui_simulator.gui.panel;

import java.awt.Dimension;

import javax.swing.JSplitPane;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;

public class RightSplitPane extends JSplitPane {

	public RightSplitPane(BasicGraphEditor frame) {
		// parent init
		setResizeWeight(1);
		setDividerSize(6);
		setBorder(null);
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		setDividerLocation(0.50);
		setMinimumSize(new Dimension(250, 0));
		setResizeWeight(0.50);

		setTopComponent(new ComponentPanel(frame));
		setBottomComponent(new SimulatorPanel(frame));
	}

	public ComponentPanel getComponentPanel() {
		return (ComponentPanel) getTopComponent();
	}

	public SimulatorPanel getSimulatorPanel() {
		return (SimulatorPanel) getBottomComponent();
	}

}
