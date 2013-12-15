package gui.main;

import ep.strategies.card.CardChipStrategy;
import ep.strategies.card.CardStrategy;
import ep.strategies.ept.EPTChipsetStrategy;
import ep.strategies.ept.EPTSmartCardReader;
import ep.strategies.ept.EPTStrategy;
import ep.strategies.fo.FOStrategy;
import gui.bridge.ComponentPaletteBridge;

import java.awt.Color;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import model.component.Component;
import model.component.ComponentI;
import model.component.ComponentIO;
import model.component.ComponentO;
import model.dao.DAO;
import model.dao.factory.DAOFactory;
import model.strategies.IStrategy;
import model.strategies.NullStrategy;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

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
		graph.getSelectionModel().addListener(mxEvent.CHANGE, rightSplitPane.getComponentPanel());

		// Creates the components palette for our electronic payment application
		EditorPalette componentsPalette = insertPalette(mxResources.get("components"));
		EditorPalette examplesPalette = insertPalette(mxResources.get("examples"));

		HashMap<String, String> test = new HashMap<>();
		test.put("cou", "oj");
		test.put("ceru", "oj");
		test.put("ceeu", "oj");

		// getting dao
		DAO<Component> dao = DAOFactory.getFactory().getComponentDAO();
		Component p = new ComponentIO("Card");
		p.setProperties(test);

		Component p1 = new ComponentIO("Test 1");
		p1.getComponents().add(new ComponentIO("Test 3"));
		p1.getComponents().add(new ComponentIO("Test 4"));
		p.getComponents().add(p1);
		p.getComponents().add(new ComponentIO("Test 2"));
		dao.create(p);
		dao.create(new ComponentIO("EPT"));
		dao.create(new ComponentO("Output"));
		dao.create(new ComponentI("Input"));
		dao.create(new ComponentIO("Input/Output"));
		dao.create(new ComponentIO("IS"));
		dao.create(new ComponentIO("FO"));
		dao.create(new ComponentIO("BO"));

		ComponentPaletteBridge bridge = new ComponentPaletteBridge(componentsPalette, dao, graphComponent.getGraph());
		bridge.refresh();

		// Adds some template cells for dropping into the graph
		examplesPalette.addTemplate("Container",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/swimlane.png")),
				"swimlane", 280, 280, "Container");
		examplesPalette.addTemplate("Icon",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/rounded.png")),
				"icon;image=/com/mxgraph/examples/swing/images/wrench.png", 70, 70, "Icon");
		examplesPalette.addTemplate("Label",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/rounded.png")),
				"label;image=/com/mxgraph/examples/swing/images/gear.png", 130, 50, "Label");
		examplesPalette.addTemplate("Rectangle",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/rectangle.png")),
				null, 160, 120, "");
		examplesPalette.addTemplate("Rounded Rectangle",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/rounded.png")),
				"rounded=1", 160, 120, "");
		examplesPalette.addTemplate("Ellipse",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/ellipse.png")),
				"ellipse", 160, 160, "");
		examplesPalette.addTemplate("Double Ellipse",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/doubleellipse.png")),
				"ellipse;shape=doubleEllipse", 160, 160, "");
		examplesPalette.addTemplate("Triangle",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/triangle.png")),
				"triangle", 120, 160, "");
		examplesPalette.addTemplate("Rhombus",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/rhombus.png")),
				"rhombus", 160, 160, "");
		examplesPalette.addTemplate("Horizontal Line",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/hline.png")), "line",
				160, 10, "");
		examplesPalette.addTemplate("Hexagon",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/hexagon.png")),
				"shape=hexagon", 160, 120, "");
		examplesPalette.addTemplate("Cylinder",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/cylinder.png")),
				"shape=cylinder", 120, 160, "");
		examplesPalette.addTemplate("Actor",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/actor.png")),
				"shape=actor", 120, 160, "");
		examplesPalette.addTemplate("Cloud",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/cloud.png")),
				"ellipse;shape=cloud", 160, 120, "");

		examplesPalette.addEdgeTemplate("Straight",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/straight.png")),
				"straight", 120, 120, "");
		examplesPalette.addEdgeTemplate("Horizontal Connector",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/connect.png")), null,
				100, 100, "");
		examplesPalette.addEdgeTemplate("Vertical Connector",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/vertical.png")),
				"vertical", 100, 100, "");
		examplesPalette.addEdgeTemplate("Entity Relation",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/entity.png")),
				"entity", 100, 100, "");
		examplesPalette.addEdgeTemplate("Arrow",
				new ImageIcon(SimulatorGUI.class.getResource("/com/mxgraph/examples/swing/images/arrow.png")), "arrow",
				120, 120, "");
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

		DAO<IStrategy> dao2 = DAOFactory.getFactory().getStrategyDAO();
		dao2.create(new NullStrategy());
		dao2.create(new CardChipStrategy());
		dao2.create(new CardStrategy());
		dao2.create(new EPTChipsetStrategy());
		dao2.create(new EPTSmartCardReader());
		dao2.create(new EPTStrategy());
		dao2.create(new FOStrategy());

		SimulatorGUI editor = new SimulatorGUI();
		editor.createFrame(new EditorMenuBar(editor)).setVisible(true);
	}

}
