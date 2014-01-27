package fr.ensicaen.simulator.model.dao.jaxbadapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.dao.jaxbadapter.OutputAdapter.OutputAdapted;

public final class OutputAdapter extends XmlAdapter<OutputAdapted, IOutput> {

	@XmlRootElement
	public static class OutputAdapted {

		@XmlIDREF
		@XmlAttribute
		public Component uuidComponent;

	}

	@Override
	public IOutput unmarshal(OutputAdapted v) throws Exception {
		return (IOutput) v.uuidComponent;
	}

	@Override
	public OutputAdapted marshal(IOutput v) throws Exception {
		OutputAdapted ioa = new OutputAdapted();
		ioa.uuidComponent = (Component) v;
		return ioa;
	}

}