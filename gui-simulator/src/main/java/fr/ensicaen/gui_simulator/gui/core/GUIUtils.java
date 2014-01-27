package fr.ensicaen.gui_simulator.gui.core;

import javax.swing.ImageIcon;

import com.mxgraph.util.mxResources;

import fr.ensicaen.gui_simulator.gui.bridge.ComponentPaletteBridge;

public class GUIUtils {
	/**
	 * Retrieve component name translation
	 * 
	 * @param name
	 * @return
	 */
	public static String getTranslationName(String name) {
		String transName = mxResources.get(name);
		return transName != null ? transName : name;
	}

	/**
	 * Retrieve icon path by name
	 * 
	 * @param name
	 * @return
	 */
	public static String getGraphIconPath(String name) {
		String iconPath = "/gui/icon/" + name + ".png";
		return (ComponentPaletteBridge.class.getResource(iconPath) != null) ? iconPath : "";
	}

	/**
	 * Retrieve image size's
	 * 
	 * @param graphIconPath
	 * @return
	 */
	public static int[] getSize(String graphIconPath) {
		if (graphIconPath != null && !graphIconPath.isEmpty()) {
			ImageIcon image = new ImageIcon(ComponentPaletteBridge.class.getResource(graphIconPath));
			return new int[] { image.getIconWidth(), image.getIconHeight() };
		}
		else {
			return new int[] { 150, 75 };
		}
	}
}
