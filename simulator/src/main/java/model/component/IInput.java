package model.component;

import model.mediator.Mediator;
import model.response.IResponse;

public interface IInput {
	public IResponse notifyMessage(Mediator m, String data);
}
