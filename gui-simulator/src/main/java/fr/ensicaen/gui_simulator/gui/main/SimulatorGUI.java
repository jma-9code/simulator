package fr.ensicaen.gui_simulator.gui.main;

import java.awt.Color;
import java.net.URL;
import java.text.NumberFormat;

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
	/**
	 * 
	 */
	private static final long serialVersionUID = -4601740824088314699L;

	/**
	 * Holds the shared number formatter.
	 * 
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * Holds the URL for the icon to be used as a handle for creating new
	 * connections. This is currently unused.
	 */
	public static URL url = null;

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
		EditorPalette componentsPalette = insertPalette(mxResources.get("components"));

		// getting dao
		DAO<Component> dao = DAOFactory.getFactory().getComponentDAO();

		// bridge components
		ComponentPaletteBridge bridge = new ComponentPaletteBridge(componentsPalette, dao, graphComponent.getGraph());
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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}

		mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

		// DAO<IStrategy> daoStrategy =
		// DAOFactory.getFactory().getStrategyDAO();
		// daoStrategy.create(new NullStrategy());
		// daoStrategy.create(new CardChipStrategy());
		// daoStrategy.create(new CardStrategy());
		// daoStrategy.create(new EPTChipsetStrategy());
		// daoStrategy.create(new EPTSmartCardReader());
		// daoStrategy.create(new EPTStrategy());
		// daoStrategy.create(new FOStrategy());

		SimulatorGUI editor = new SimulatorGUI();
		editor.createFrame(new EditorMenuBar(editor)).setVisible(true);
	}

}
