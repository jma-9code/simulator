package gui.feature;

import gui.bridge.ComponentWrapper;
import gui.bridge.MediatorWrapper;
import gui.main.CustomGraph;
import simulator.Context;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

public class ComponentRegistrationFeature implements mxIEventListener {

	@Override
	public void invoke(Object obj, mxEventObject e) {
		CustomGraph graph = (CustomGraph) obj;
		Object[] cells = ((Object[]) e.getProperty("cells"));
		Context ctx = Context.getInstance();

		switch (e.getName()) {
			case mxEvent.CELLS_ADDED:
				for (Object _cell : cells) {
					mxCell cell = (mxCell) _cell;

					// registration depends on type
					if (cell.getValue() instanceof ComponentWrapper) {
						ctx.registerComponent(((ComponentWrapper) cell.getValue()).getComponent());
					}
					else if (cell.getValue() instanceof MediatorWrapper) {
						ctx.registerMediator(((MediatorWrapper) cell.getValue()).getMediator());
					}
				}
				break;

			case mxEvent.CELLS_REMOVED:
				for (Object _cell : cells) {
					mxCell cell = (mxCell) _cell;

					// registration depends on type
					if (cell.getValue() instanceof ComponentWrapper) {
						ctx.unregisterComponent(((ComponentWrapper) cell.getValue()).getComponent());
					}
					else if (cell.getValue() instanceof MediatorWrapper) {
						ctx.unregisterMediator(((MediatorWrapper) cell.getValue()).getMediator());
					}
				}
				break;
		}
	}
}
