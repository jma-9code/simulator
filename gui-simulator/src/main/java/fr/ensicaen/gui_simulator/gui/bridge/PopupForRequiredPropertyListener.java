package fr.ensicaen.gui_simulator.gui.bridge;

import javax.swing.JOptionPane;

import com.mxgraph.util.mxResources;

import fr.ensicaen.simulator.model.properties.listener.PropertyListener;

public class PopupForRequiredPropertyListener implements PropertyListener {

	@Override
	public String onRequiredRead(String key, String value) {
		if (value == null) {
			return inputDialog(key);
		}

		return null;
	}

	@Override
	public String onNotRequiredRead(String key, String value) {
		return null;
	}

	private String inputDialog(String key) {
		return (String) JOptionPane.showInputDialog(null, mxResources.get("property_required", new String[] { key }),
				mxResources.get("information_required"), JOptionPane.INFORMATION_MESSAGE);
	}

}
