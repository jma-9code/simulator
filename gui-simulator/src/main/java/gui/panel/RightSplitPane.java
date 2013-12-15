package gui.panel;

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
		setDividerLocation(250);
		setMinimumSize(new Dimension(250, 0));
		// pas trouvé pour fixer la largeur de ce panel lorsqu'on agrandit la
		// fenetre ...
		// max, preferred + size testé ...

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
