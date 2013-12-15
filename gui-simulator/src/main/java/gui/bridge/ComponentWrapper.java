package gui.bridge;

import java.io.Serializable;

import model.component.Component;

import com.mxgraph.util.mxResources;

public class ComponentWrapper implements Serializable {

	private Component component;
	private String iconPath;

	public ComponentWrapper(Component obj, String iconPath) {
		this.component = obj;
		this.iconPath = iconPath;
	}

	@Override
	public String toString() {
		return getTranslationName(((Component) component).getName());
	}

	private String getTranslationName(String name) {
		String transName = mxResources.get(name);
		return transName != null ? transName : name;
	}

	public String getCollapsedStyle() {
		return iconPath != null && !iconPath.isEmpty() ? "image;image=" + iconPath : "group";
	}

	public String getExpandedStyle() {
		return "group";
	}

	public Component getComponent() {
		return component;
	}
}
