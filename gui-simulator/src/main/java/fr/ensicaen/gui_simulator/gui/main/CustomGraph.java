package fr.ensicaen.gui_simulator.gui.main;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import fr.ensicaen.gui_simulator.gui.bridge.ComponentWrapper;
import fr.ensicaen.gui_simulator.gui.bridge.MediatorWrapper;
import fr.ensicaen.gui_simulator.gui.bridge.SimulatorGUIBridge;
import fr.ensicaen.gui_simulator.gui.feature.ComponentAppearanceFeature;
import fr.ensicaen.gui_simulator.gui.feature.ComponentRegistrationFeature;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.mediator.Mediator;

/**
 * A graph that creates new edges from a given template edge.
 */
public class CustomGraph extends mxGraph {
	/**
	 * Holds the shared number formatter.
	 * 
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * Holds the edge to be used as a template for inserting new edges.
	 */
	protected Object edgeTemplate;

	/**
	 * Custom graph that defines the alternate edge style to be used when the
	 * middle control point of edges is double clicked (flipped).
	 */
	public CustomGraph() {
		setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
		setCellsEditable(false);
		setMultigraph(false);
		setAllowDanglingEdges(false);
		setConnectableEdges(false);

		// link to our code
		ComponentAppearanceFeature appearanceFeature = new ComponentAppearanceFeature(this);
		addListener(mxEvent.FOLD_CELLS, appearanceFeature);

		ComponentRegistrationFeature registrationFeature = new ComponentRegistrationFeature();
		addListener(mxEvent.CELLS_ADDED, registrationFeature);
		addListener(mxEvent.CELLS_REMOVED, registrationFeature);
		addListener(SimulatorGUIBridge.EVT_PAUSE_CTX_SYNC, registrationFeature);
		addListener(SimulatorGUIBridge.EVT_RESUME_CTX_SYNC, registrationFeature);
	}

	/**
	 * Sets the edge template to be used to inserting edges.
	 */
	public void setEdgeTemplate(Object template) {
		edgeTemplate = template;
	}

	/**
	 * Prints out some useful information about the cell in the tooltip.
	 */
	public String getToolTipForCell(Object cell) {
		String tip = "<html>";
		mxGeometry geo = getModel().getGeometry(cell);
		mxCellState state = getView().getState(cell);

		if (getModel().isEdge(cell)) {
			tip += "points={";

			if (geo != null) {
				List<mxPoint> points = geo.getPoints();

				if (points != null) {
					Iterator<mxPoint> it = points.iterator();

					while (it.hasNext()) {
						mxPoint point = it.next();
						tip += "[x=" + numberFormat.format(point.getX()) + ",y=" + numberFormat.format(point.getY())
								+ "],";
					}

					tip = tip.substring(0, tip.length() - 1);
				}
			}

			tip += "}<br>";
			tip += "absPoints={";

			if (state != null) {

				for (int i = 0; i < state.getAbsolutePointCount(); i++) {
					mxPoint point = state.getAbsolutePoint(i);
					tip += "[x=" + numberFormat.format(point.getX()) + ",y=" + numberFormat.format(point.getY()) + "],";
				}

				tip = tip.substring(0, tip.length() - 1);
			}

			tip += "}";
		}
		else {
			tip += "geo=[";

			if (geo != null) {
				tip += "x=" + numberFormat.format(geo.getX()) + ",y=" + numberFormat.format(geo.getY()) + ",width="
						+ numberFormat.format(geo.getWidth()) + ",height=" + numberFormat.format(geo.getHeight());
			}

			tip += "]<br>";
			tip += "state=[";

			if (state != null) {
				tip += "x=" + numberFormat.format(state.getX()) + ",y=" + numberFormat.format(state.getY()) + ",width="
						+ numberFormat.format(state.getWidth()) + ",height=" + numberFormat.format(state.getHeight());
			}

			tip += "]";
		}

		mxPoint trans = getView().getTranslate();

		tip += "<br>scale=" + numberFormat.format(getView().getScale()) + ", translate=[x="
				+ numberFormat.format(trans.getX()) + ",y=" + numberFormat.format(trans.getY()) + "]";
		tip += "</html>";

		return tip;
	}

	/**
	 * Overrides the method to use the currently selected edge template for new
	 * edges.
	 * 
	 * @param graph
	 * @param parent
	 * @param id
	 * @param value
	 * @param source
	 * @param target
	 * @param style
	 * @return
	 */
	// public Object createEdge(Object parent, String id, Object value, Object
	// source, Object target, String style) {
	// if (edgeTemplate != null) {
	// mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
	// edge.setId(id);
	//
	// return edge;
	// }
	//
	// return super.createEdge(parent, id, value, source, target, style);
	// }

	@Override
	public String validateEdge(Object _edge, Object _source, Object _target) {
		// cast
		mxCell edge = (mxCell) _edge;

		// get user objects
		ComponentWrapper source = (ComponentWrapper) ((mxCell) _source).getValue();
		ComponentWrapper target = (ComponentWrapper) ((mxCell) _target).getValue();

		MediatorFactory factory = MediatorFactory.getInstance();
		Mediator m = factory.getMediator(source.getComponent(), target.getComponent());

		if (m != null) {
			MediatorWrapper wrapper = new MediatorWrapper(m);
			edge.setValue(wrapper);
			edge.setStyle(wrapper.getStyle());
			return "";
		}
		else {
			return mxResources.get("invalid_link");
		}
	}

	// @Override
	// public boolean isValidConnection(Object arg0, Object arg1) {
	// // TODO Auto-generated method stub
	// return false;
	// }

	@Override
	public boolean isValidSource(Object arg0) {
		return arg0 != null && ((mxCell) arg0).getValue() instanceof ComponentWrapper;
	}

	@Override
	public boolean isValidTarget(Object arg0) {
		return arg0 != null && ((mxCell) arg0).getValue() instanceof ComponentWrapper;
	}

}
