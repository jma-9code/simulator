package fr.ensicaen.simulator.simulator;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ensicaen.simulator.model.dao.jaxbadapter.JaxbDateConverter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StartPoint {

	@XmlJavaTypeAdapter(JaxbDateConverter.class)
	protected Date time;
	protected String event;

	public StartPoint() {
	}

	public StartPoint(Date time, String event) {
		super();
		this.time = time != null ? time : Calendar.getInstance().getTime();
		this.event = event;
	}

	public static final class StartPointComparator implements Comparator<StartPoint> {

		@Override
		public int compare(StartPoint o1, StartPoint o2) {
			return o1.time != null ? o1.time.compareTo(o2.time) : -1;
		}

	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

}