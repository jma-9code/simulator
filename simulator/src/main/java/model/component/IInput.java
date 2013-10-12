package model.component;

import model.mediator.Mediator;
import model.response.IResponse;

public interface IInput {
	public IResponse input(Mediator m, String data);
}
