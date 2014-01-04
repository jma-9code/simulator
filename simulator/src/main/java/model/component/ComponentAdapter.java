package model.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ComponentAdapter extends XmlAdapter<ComponentAdapter.AdaptedComponent, Component> {

	private List<Component> components = new ArrayList<>();
	private Map<String, Component> map = new HashMap<>();

	@XmlRootElement
	public static class AdaptedComponent {
		@XmlAttribute
		private String reference;

		@XmlElement
		private Component data;

		public AdaptedComponent() {

		}

		public AdaptedComponent(Component c) {
			data = c;
		}

	}

	@Override
	public AdaptedComponent marshal(Component component) throws Exception {
		AdaptedComponent ad = null;
		if (component == null)
			return null;
		if (components.contains(component)) {
			ad = new AdaptedComponent();
			ad.reference = component.uuid;
		}
		else {
			ad = new AdaptedComponent(component);
			components.add(component);
			// components.addAll(component.getComponents());
		}
		return ad;
	}

	@Override
	public Component unmarshal(AdaptedComponent adaptedcomponent) throws Exception {
		Component c = map.get(adaptedcomponent.reference);
		if (c == null) {
			c = adaptedcomponent.data;
			map.put(c.uuid, c);
		}

		return c;
	}
}