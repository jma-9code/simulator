package fr.ensicaen.simulator.model.dao.jaxbadapter;

import javax.xml.bind.annotation.XmlAttribute;
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
		@XmlAttribute
		public Component uuidComponent;

	}

	@Override
	public IInput unmarshal(InputAdapted v) throws Exception {
		return (IInput) v.uuidComponent;
	}

	@Override
	public InputAdapted marshal(IInput v) throws Exception {
		InputAdapted ioa = new InputAdapted();
		ioa.uuidComponent = (Component) v;
		return ioa;
	}

}