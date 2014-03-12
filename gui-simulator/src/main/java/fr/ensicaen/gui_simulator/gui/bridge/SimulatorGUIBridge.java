package fr.ensicaen.gui_simulator.gui.bridge;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.view.mxGraph;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.dao.ScenarioData;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.simulator.Context;

public class SimulatorGUIBridge {

	public static final String EVT_RESUME_CTX_SYNC = "resume_ctx_sync";
	public static final String EVT_PAUSE_CTX_SYNC = "pause_ctx_sync";
	private static Logger logger = LoggerFactory
			.getLogger(SimulatorGUIBridge.class);

	/**
	 * Save ui data
	 * 
	 * @param graph
	 * @return
	 */
	public static Map<String, Object> exportUiData(mxGraph graph) {
		Map<String, Object> uiData = new HashMap<String, Object>();
		recursiveExportUiData(uiData, graph, graph.getDefaultParent());
		return uiData;
	}

	private static void recursiveExportUiData(Map<String, Object> uiData,
			mxGraph graph, Object parent) {
		Object[] cells = graph.getChildCells(parent, true, true);

		for (Object obj : cells) {
			mxCell cell = (mxCell) obj;

			if (cell.getValue() instanceof ComponentWrapper) {
				ComponentWrapper wrapper = (ComponentWrapper) cell.getValue();
				wrapper.saveUiComponent(cell);
				uiData.put(wrapper.getComponent().getUuid(), wrapper);
				logger.debug("UI Data saved for component "
						+ wrapper.getComponent().getInstanceName());

				// recursive call
				recursiveExportUiData(uiData, graph, cell);
			} else if (cell.getValue() instanceof MediatorWrapper) {
				MediatorWrapper wrapper = (MediatorWrapper) cell.getValue();
				wrapper.saveUiComponent(cell);
				uiData.put(wrapper.getMediator().getUuid(), wrapper);
				logger.debug("UI Data saved for mediator "
						+ wrapper.getMediator().getUuid());
			}

		}
	}

	/**
	 * Restore ui data
	 */
	public static void syncContextToGraph(ScenarioData scenaData, mxGraph graph) {
		Map<String, Object> uiData = scenaData.getUiData();
		Context ctx = Context.getInstance();

		graph.fireEvent(new mxEventObject(EVT_PAUSE_CTX_SYNC));

		for (Component c : ctx.getComponents().values()) {
			logger.debug("Init component " + c.getInstanceName() + " to graph");
			mxCell cell = createVertex(c, uiData);
			graph.addCell(cell);
		}

		for (Mediator m : ctx.getMediators()) {
			logger.debug("Init mediator " + m.getUuid() + " to graph");
			mxCell cell = createEdge(m, uiData, graph);
			mxCell source = findVertex((Component) m.getSender(), graph);
			mxCell target = findVertex((Component) m.getReceiver(), graph);

			graph.addEdge(cell, null, source, target, null);
		}

		graph.fireEvent(new mxEventObject(EVT_RESUME_CTX_SYNC));

	}

	public static mxCell createVertex(Component c) {
		return createVertex(c, null);
	}

	public static mxCell createVertex(Component c, Map<String, Object> uiData) {
		// ui wrapper
		ComponentWrapper wrapper = null;

		if (uiData != null) {
			wrapper = (ComponentWrapper) uiData.get(c.getUuid());
		}

		if (wrapper == null) {
			wrapper = new ComponentWrapper(c);
		} else {
			wrapper.init(c);
		}

		// parent vertex
		String style = wrapper.isCollapsed() ? wrapper.getCollapsedStyle()
				: wrapper.getExpandedStyle();
		mxCell cell = new mxCell(wrapper, wrapper.getMxGeometry(), style);
		cell.setValue(wrapper);
		cell.setVertex(true);
		cell.setCollapsed(wrapper.isCollapsed());

		if (c.getChilds() != null && !c.getChilds().isEmpty()) {

			// creation of child vertex recursively
			for (Component child : c.getChilds()) {
				mxCell cellChild = createVertex(child, uiData);
				// view
				cell.insert(cellChild);
			}

		}

		return cell;

	}

	public static mxCell createEdge(Mediator m, Map<String, Object> uiData,
			mxGraph graph) {
		// ui wrapper
		MediatorWrapper wrapper = null;

		if (uiData != null) {
			wrapper = (MediatorWrapper) uiData.get(m.getUuid());
		}

		if (wrapper == null) {
			wrapper = new MediatorWrapper(m);
		} else {
			wrapper.init(m);
		}

		// parent vertex
		mxCell cell = new mxCell(wrapper, wrapper.getMxGeometry(),
				wrapper.getStyle());
		// cell.setSource();
		// cell.setTarget();
		cell.setValue(wrapper);
		cell.setEdge(true);

		return cell;
	}

	public static mxCell findVertex(Component wanted, mxGraph graph) {
		logger.debug("find vertex with component " + wanted);
		return recursiveFindVertex(wanted, graph, graph.getDefaultParent());
	}

	public static List<mxCell> getAllCell(mxGraph graph) {
		List<mxCell> cells = Arrays.asList((mxCell[]) graph
				.getChildVertices(graph.getDefaultParent()));
		return cells;
	}

	private static mxCell recursiveFindVertex(Component wanted, mxGraph graph,
			Object parent) {
		Object[] cells = graph.getChildCells(parent, true, false);

		for (Object obj : cells) {
			mxCell cell = (mxCell) obj;
			if (cell.getValue() instanceof ComponentWrapper) {
				ComponentWrapper wrapper = (ComponentWrapper) cell.getValue();

				// instance equality
				if (wrapper.getComponent() == wanted) {
					return cell;
				}
			}

			// recursive call
			mxCell found = recursiveFindVertex(wanted, graph, cell);

			if (found != null) {
				return found;
			}
		}

		return null;
	}

	public static mxCell findEdge(Mediator wanted, mxGraph graph) {
		logger.debug("find edge with mediator " + wanted);
		return recursiveFindEdge(wanted, graph, graph.getDefaultParent());
	}

	private static mxCell recursiveFindEdge(Mediator wanted, mxGraph graph,
			Object parent) {
		Object[] cells = graph.getChildCells(parent, true, false);

		for (Object obj : cells) {
			mxCell cell = (mxCell) obj;
			if (cell.getValue() instanceof MediatorWrapper) {
				MediatorWrapper wrapper = (MediatorWrapper) cell.getValue();

				// instance equality
				if (wrapper.getMediator().equals(wanted)) {
					return cell;
				}
			}

			// recursive call
			mxCell found = recursiveFindEdge(wanted, graph, cell);

			if (found != null) {
				return found;
			}
		}

		return null;
	}
}
