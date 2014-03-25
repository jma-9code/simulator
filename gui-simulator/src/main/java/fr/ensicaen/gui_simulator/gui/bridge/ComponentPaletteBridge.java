package fr.ensicaen.gui_simulator.gui.bridge;

import java.util.List;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import fr.ensicaen.gui_simulator.gui.core.GUIUtils;
import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.dao.DAO;

public class ComponentPaletteBridge {

	private EditorPalette palette;
	private DAO<Component> dao;
	private mxGraph graph;

	private ImageIcon defaultPaletteIcon;

	private static Logger logger = LoggerFactory.getLogger(ComponentPaletteBridge.class);

	public ComponentPaletteBridge(EditorPalette palette, DAO<Component> dao, mxGraph graph) {
		this.palette = palette;
		this.dao = dao;
		this.graph = graph;

		// init
		this.defaultPaletteIcon = new ImageIcon(
				ComponentPaletteBridge.class.getResource("/com/mxgraph/examples/swing/images/rounded.png"));
	}

	private void injectComponents() {
		List<Component> components = dao.findAll();

		for (Component c : components) {
			// test if specific icons exists
			ImageIcon paletteIcon = getPaletteIcon(c);

			// create recursively the vertex for this component
			mxCell cell = SimulatorGUIBridge.createVertex(c);

			// test if name translation exists
			String name = GUIUtils.getTranslationName(c.getName());

			// add to the palette
			palette.addTemplate(name, paletteIcon, cell);
		}
	}

	/**
	 * Retrieve icon by name
	 * 
	 * @param name
	 * @return
	 */
	private ImageIcon getPaletteIcon(Component c) {
		String filePath = GUIUtils.getGraphIconPath(c);
		return (filePath != null && !filePath.isEmpty()) ? new ImageIcon(filePath) : defaultPaletteIcon;
	}

	public void refresh() {
		palette.removeAll();
		injectComponents();
	}
}
