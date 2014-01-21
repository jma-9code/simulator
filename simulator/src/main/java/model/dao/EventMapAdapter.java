package model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import model.component.ComponentIO;
import model.component.ComponentO;
import model.component.IOutput;
import model.dao.EventMapAdapter.EventMap;

public final class EventMapAdapter extends XmlAdapter<EventMap, Map<String, List<IOutput>>> {

	public static class EventMap {

		public List<MyMapEntryType> entry = new ArrayList<MyMapEntryType>();

	}

	public static class MyMapEntryType {
		@XmlAttribute
		public String event;

		@XmlIDREF
		@XmlElements({ @XmlElement(type = ComponentO.class), @XmlElement(type = ComponentIO.class) })
		public List<IOutput> components;

	}

	@Override
	public Map<String, List<IOutput>> unmarshal(EventMap v) throws Exception {
		Map<String, List<IOutput>> hashMap = new HashMap<>();
		for (MyMapEntryType myEntryType : v.entry) {
			hashMap.put(myEntryType.event, myEntryType.components);
		}
		return hashMap;
	}

	@Override
	public EventMap marshal(Map<String, List<IOutput>> v) throws Exception {
		EventMap em = new EventMap();

		for (Entry<String, List<IOutput>> entry : v.entrySet()) {
			MyMapEntryType myMapEntryType = new MyMapEntryType();
			myMapEntryType.event = entry.getKey();
			myMapEntryType.components = entry.getValue();
			em.entry.add(myMapEntryType);
		}
		return em;
	}
}