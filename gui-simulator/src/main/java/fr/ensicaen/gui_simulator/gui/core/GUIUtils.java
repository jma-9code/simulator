package fr.ensicaen.gui_simulator.gui.core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.ImageIcon;

import com.mxgraph.util.mxResources;

import fr.ensicaen.simulator.model.component.Component;

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
	public static String getGraphIconPath(Component c) {
		String iconWithNamePath = Paths.get("icon").toAbsolutePath()
				+ File.separator + c.getName() + ".png";
		String iconWithTypePath = Paths.get("icon").toAbsolutePath()
				+ File.separator + c.getType() + ".png";

		if (Files.isReadable(Paths.get(iconWithNamePath))) {
			return iconWithNamePath;
		} else if (Files.isReadable(Paths.get(iconWithTypePath))) {
			return iconWithTypePath;
		}

		return "";
	}

	/**
	 * Retrieve image size's
	 * 
	 * @param graphIconPath
	 * @return
	 */
	public static int[] getSize(String graphIconPath) {
		if (graphIconPath != null && !graphIconPath.isEmpty()) {
			ImageIcon image = new ImageIcon(graphIconPath);
			return new int[] { image.getIconWidth(), image.getIconHeight() };
		} else {
			return new int[] { 150, 75 };
		}
	}
}
