package fr.ensicaen.simulator.model.dao.jaxbadapter;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
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
		@XmlElementWrapper
		public List<Component> component;

	}

	@Override
	public IOutput unmarshal(OutputAdapted v) throws Exception {
		if (!v.component.isEmpty())
			return (IOutput) v.component.get(0);
		throw new Exception("no component object find");
	}

	@Override
	public OutputAdapted marshal(IOutput v) throws Exception {
		OutputAdapted ioa = new OutputAdapted();
		ioa.component = Arrays.asList((Component) v);
		return ioa;
	}

}