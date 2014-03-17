package fr.ensicaen.gui_simulator.gui.bridge;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

import fr.ensicaen.simulator.model.mediator.ForwardMediator;
import fr.ensicaen.simulator.model.mediator.HalfDuplexMediator;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.mediator.PipedMediator;
import fr.ensicaen.simulator.model.mediator.SimplexMediator;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MediatorWrapper implements Serializable {

	@XmlTransient
	private Mediator mediator;

	// ui data
	@XmlElement
	private List<mxPoint> points;

	@XmlTransient
	private String data;

	public MediatorWrapper() {
	}

	public MediatorWrapper(Mediator m) {
		this.points = new LinkedList<>();
		init(m);
	}

	public void init(Mediator m) {
		this.mediator = m;
	}

	public void saveUiComponent(mxCell cell) {
		points = cell.getGeometry().getPoints();
	}

	public mxGeometry getMxGeometry() {
		mxGeometry geo = new mxGeometry();
		geo.setPoints(points);
		return geo;
	}

	@Override
	public String toString() {
		return "";
	}

	public Mediator getMediator() {
		return mediator;
	}

	public String getStyle() {
		if (mediator instanceof HalfDuplexMediator) {
			return "lineHalfDuplex";
		} else if (mediator instanceof SimplexMediator) {
			return "lineSimplex";
		} else {
			return "";
		}
	}

	public String getUseStyle() {
		if (mediator instanceof HalfDuplexMediator) {
			return "lineUseHalfDuplex";
		} else if (mediator instanceof SimplexMediator) {
			return "lineUseSimplex";
		} else if (mediator instanceof PipedMediator) {
			return "lineUseHalfDuplex";
		} else if (mediator instanceof ForwardMediator) {
			return "lineUseHalfDuplex";
		} else {
			return "";
		}
	}

	public List<mxPoint> getPoints() {
		return points;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
