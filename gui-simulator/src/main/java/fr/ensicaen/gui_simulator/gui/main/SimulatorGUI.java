package fr.ensicaen.gui_simulator.gui.main;

import java.awt.Color;
import java.io.File;

import javax.swing.UIManager;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import fr.ensicaen.gui_simulator.gui.bridge.ComponentPaletteBridge;
import fr.ensicaen.gui_simulator.gui.bridge.PopupForRequiredPropertyListener;
import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.dao.DAO;
import fr.ensicaen.simulator.model.dao.factory.DAOFactory;
import fr.ensicaen.simulator.simulator.Context;

public class SimulatorGUI extends BasicGraphEditor {

	private static final long serialVersionUID = -4601740824088314699L;

	private ComponentPaletteBridge bridge = null;
	private EditorPalette palette = null;

	static {
		try {
			mxResources.add("gui/resources/simulator-ep");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SimulatorGUI() {
		this(mxResources.get("simulator-title"), new CustomGraphComponent(new CustomGraph()));
	}

	/**
	 * 
	 */
	public SimulatorGUI(String appTitle, mxGraphComponent graphComponent) {
		super(appTitle, graphComponent);
		// final mxGraph graph = graphComponent.getGraph();

		mxGraph graph = graphComponent.getGraph();

		// link selection event
		graph.getSelectionModel().addListener(mxEvent.CHANGE, vRightSplit.getComponentPanel());

		// Creates the components palette for our electronic payment application
		palette = insertPalette(mxResources.get("components"));

		// getting dao
		DAO<Component> dao = DAOFactory.getFactory().getComponentDAO();

		// bridge components
		bridge = new ComponentPaletteBridge(palette, dao, graphComponent.getGraph());
		bridge.refresh();

		// property listener for asking popup
		PopupForRequiredPropertyListener listener = new PopupForRequiredPropertyListener();
		Context.getInstance().setPropertyListener(listener);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Relative path : " + new File(".").getAbsolutePath());
		try {
			// theme windows
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}

		mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

		// instanciation du panel / menu bar puis creation JFrame
		SimulatorGUI editor = new SimulatorGUI();
		EditorMenuBar menuBar = new EditorMenuBar(editor);
		editor.createFrame(menuBar).setVisible(true);
	}

	@Override
	public void refreshAll() {
		super.refreshAll();
		bridge.refresh();
	}

	public ComponentPaletteBridge getBridge() {
		return bridge;
	}

	public EditorPalette getPalette() {
		return palette;
	}

}
