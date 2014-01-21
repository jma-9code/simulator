package fr.ensicaen.simulator.model.dao.jaxbadapter;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.dao.jaxbadapter.InputAdapter.InputAdapted;

public final class InputAdapter extends XmlAdapter<InputAdapted, IInput> {

	@XmlRootElement
	public static class InputAdapted {

		@XmlIDREF
		@XmlElementWrapper
		public List<Component> component;

	}

	@Override
	public IInput unmarshal(InputAdapted v) throws Exception {
		if (!v.component.isEmpty())
			return (IInput) v.component.get(0);
		throw new Exception("no component object find");
	}

	@Override
	public InputAdapted marshal(IInput v) throws Exception {
		InputAdapted ioa = new InputAdapted();
		ioa.component = Arrays.asList((Component) v);
		return ioa;
	}

}