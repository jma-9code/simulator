package gui.bridge;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import model.component.Component;
import model.dao.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

public class ComponentPaletteBridge {

	private EditorPalette palette;
	private DAO<Component> dao;
	private mxGraph graph;

	private ImageIcon defaultPaletteIcon;
	private String defaultGraphIconPath;
	private List<mxCell> parents;

	private static Logger logger = LoggerFactory.getLogger(ComponentPaletteBridge.class);

	public ComponentPaletteBridge(EditorPalette palette, DAO<Component> dao, mxGraph graph) {
		this.palette = palette;
		this.dao = dao;
		this.graph = graph;

		// init
		this.defaultPaletteIcon = new ImageIcon(
				ComponentPaletteBridge.class.getResource("/com/mxgraph/examples/swing/images/rounded.png"));
		this.defaultGraphIconPath = "";
		this.parents = new ArrayList();
	}

	private void injectComponents() {
		List<Component> components = dao.findAll();

		for (Component c : components) {
			// test if specific icons exists
			ImageIcon paletteIcon = getPaletteIcon(c.getName());

			// create recursively the vertex for this component
			mxCell cell = createVertex(c);

			// test if name translation exists
			String name = getTranslationName(c.getName());

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
	private ImageIcon getPaletteIcon(String name) {
		URL iconUrl = ComponentPaletteBridge.class.getResource("/gui/icon/" + name + ".png");
		return (iconUrl != null) ? new ImageIcon(iconUrl) : defaultPaletteIcon;
	}

	/**
	 * Retrieve icon path by name
	 * 
	 * @param name
	 * @return
	 */
	private String getGraphIconPath(String name) {
		String iconPath = "/gui/icon/" + name + ".png";
		return (ComponentPaletteBridge.class.getResource(iconPath) != null) ? iconPath : defaultGraphIconPath;
	}

	/**
	 * Retrieve component name translation
	 * 
	 * @param name
	 * @return
	 */
	private String getTranslationName(String name) {
		String transName = mxResources.get(name);
		return transName != null ? transName : name;
	}

	/**
	 * Create the vertex
	 * 
	 * @param c
	 * @return
	 */
	private mxCell createVertex(Component c) {
		// get graph icon and create wrapper
		String graphIconPath = getGraphIconPath(c.getName());
		ComponentWrapper wrapper = new ComponentWrapper(c, graphIconPath);
		int[] size = getSize(graphIconPath);

		// parent vertex
		mxCell cell = new mxCell(wrapper, new mxGeometry(0, 0, size[0], size[1]), wrapper.getCollapsedStyle());
		cell.setVertex(true);
		cell.setCollapsed(true);

		if (c.getComponents() != null && !c.getComponents().isEmpty()) {

			// creation of child vertex recursively
			for (Component child : c.getComponents()) {
				mxCell cellChild = createVertex(child);
				cell.insert(cellChild);
			}

		}

		return cell;

	}

	private int[] getSize(String graphIconPath) {
		if (graphIconPath != null && !graphIconPath.isEmpty()) {
			ImageIcon image = new ImageIcon(ComponentPaletteBridge.class.getResource(graphIconPath));
			logger.debug("width = " + image.getIconWidth() + ", height = " + image.getIconHeight());
			return new int[] { image.getIconWidth(), image.getIconHeight() };
		}
		else {
			return new int[] { 150, 75 };
		}
	}

	public void refresh() {
		injectComponents();
	}
}
