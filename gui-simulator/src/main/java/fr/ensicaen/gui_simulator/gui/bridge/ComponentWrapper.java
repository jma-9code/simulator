package fr.ensicaen.gui_simulator.gui.bridge;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxResources;

import fr.ensicaen.gui_simulator.gui.core.GUIUtils;
import fr.ensicaen.simulator.model.component.Component;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentWrapper implements Serializable {

	@XmlTransient
	private Component component;

	// ui attributs to persist
	@XmlTransient
	private String iconPath;

	@XmlElement
	private double x = 0;

	@XmlElement
	private double y = 0;

	@XmlElement
	private double width = 0;

	@XmlElement
	private double height = 0;

	@XmlElement
	private boolean collapsed = true;

	public ComponentWrapper() {
	}

	public ComponentWrapper(Component c) {
		init(c);
	}

	public void init(Component c) {
		this.component = c;

		// get graph icon
		this.iconPath = GUIUtils.getGraphIconPath(component.getName());
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
		return "";// iconPath != null && !iconPath.isEmpty() ? "image;image="
					// + iconPath : "group;whiteSpace=wrap";
	}

	public String getExpandedStyle() {
		return "";// "group;whiteSpace=wrap";
	}

	public Component getComponent() {
		return component;
	}

	/**
	 * Allows to restore the component ui
	 * 
	 * @param cell
	 */
	public void restoreUiComponent(mxCell cell) {
		mxGeometry geo = new mxGeometry(x, y, width, height);
		cell.setCollapsed(collapsed);
		cell.setGeometry(geo);
	}

	/**
	 * Save the component ui
	 * 
	 * @param cell
	 */
	public void saveUiComponent(mxCell cell) {
		mxGeometry geo = cell.getGeometry();
		x = geo.getX();
		y = geo.getY();
		width = geo.getWidth();
		height = geo.getHeight();
		collapsed = cell.isCollapsed();
	}

	public mxGeometry getMxGeometry() {
		if (height == 0 || width == 0) {
			int[] size = GUIUtils.getSize(iconPath);
			height = size[1];
			width = size[0];
			collapsed = true;
			// x, y are given by mouse position.
		}

		return new mxGeometry(x, y, width, height);
	}

	public boolean isCollapsed() {
		return collapsed;
	}
}
