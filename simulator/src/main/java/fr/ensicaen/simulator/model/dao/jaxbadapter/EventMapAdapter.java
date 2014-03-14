package fr.ensicaen.simulator.model.dao.jaxbadapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.dao.jaxbadapter.EventMapAdapter.EventMap;

public final class EventMapAdapter extends XmlAdapter<EventMap, Map<String, List<IOutput>>> {

	@XmlRootElement
	public static class EventMap {

		public List<MyMapEntryType> events = new ArrayList<MyMapEntryType>();

	}

	public static class MyMapEntryType {
		@XmlAttribute
		public String event;

		@XmlIDREF
		@XmlElementWrapper
		public List<Component> components;

	}

	@Override
	public Map<String, List<IOutput>> unmarshal(EventMap v) throws Exception {
		Map<String, List<IOutput>> hashMap = new HashMap<>();
		for (MyMapEntryType myEntryType : v.events) {
			if (myEntryType != null && myEntryType.components != null) {
				List<IOutput> cs = new ArrayList<>();

				for (Component io : myEntryType.components) {
					cs.add((IOutput) io);
				}
				hashMap.put(myEntryType.event, cs);
			}
		}
		return hashMap;
	}

	@Override
	public EventMap marshal(Map<String, List<IOutput>> v) throws Exception {
		EventMap em = new EventMap();

		for (Entry<String, List<IOutput>> entry : v.entrySet()) {
			MyMapEntryType myMapEntryType = new MyMapEntryType();
			myMapEntryType.event = entry.getKey();
			List<Component> cs = new ArrayList<>();
			for (IOutput io : entry.getValue()) {
				cs.add((Component) io);
			}
			myMapEntryType.components = cs;
			em.events.add(myMapEntryType);
		}
		return em;
	}
}