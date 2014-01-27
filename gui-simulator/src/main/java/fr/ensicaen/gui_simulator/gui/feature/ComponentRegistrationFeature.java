package fr.ensicaen.gui_simulator.gui.feature;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import fr.ensicaen.gui_simulator.gui.bridge.ComponentWrapper;
import fr.ensicaen.gui_simulator.gui.bridge.SimulatorGUIBridge;
import fr.ensicaen.gui_simulator.gui.bridge.MediatorWrapper;
import fr.ensicaen.gui_simulator.gui.main.CustomGraph;
import fr.ensicaen.simulator.simulator.Context;

public class ComponentRegistrationFeature implements mxIEventListener {

	private boolean pause = false;

	@Override
	public void invoke(Object obj, mxEventObject e) {
		CustomGraph graph = (CustomGraph) obj;
		Object[] cells = ((Object[]) e.getProperty("cells"));
		Context ctx = Context.getInstance();

		switch (e.getName()) {
			case SimulatorGUIBridge.EVT_PAUSE_CTX_SYNC:
				pause = true;
				break;
			case SimulatorGUIBridge.EVT_RESUME_CTX_SYNC:
				pause = false;
				break;
			case mxEvent.CELLS_ADDED:
				if (pause) {
					break;
				}

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
				if (pause) {
					break;
				}

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
