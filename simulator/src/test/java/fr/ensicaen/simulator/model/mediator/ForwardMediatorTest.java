package fr.ensicaen.simulator.model.mediator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IInput;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.factory.MediatorFactory;
import fr.ensicaen.simulator.model.factory.MediatorFactory.EMediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.simulator.Context;
import fr.ensicaen.simulator.simulator.Simulator;

public class ForwardMediatorTest {

	public static class Counter {
		private int counter = 0;

		public void inc() {
			this.counter++;
		}

		@Override
		public String toString() {
			return this.counter + "";
		}
	}

	/*
	 * @Test public void testSimpleForward() { final Counter cpt = new
	 * Counter(); final ComponentIO c1 = new ComponentIO("C1"); final Component
	 * c2 = new ComponentIO("C2"); final Component c3 = new ComponentIO("C3");
	 * 
	 * c1.setStrategy(new IStrategy() {
	 * 
	 * @Override public void processMessage(Component _this, Mediator mediator,
	 * String data) { if(mediator == null) {
	 * System.out.println("PROCESS "+_this.getName()); Mediator m =
	 * MediatorFactory.getInstance().getMediator(_this, c2,
	 * EMediator.HALFDUPLEX); m.send((IOutput)_this, data); // c1 to c2 }
	 * 
	 * 
	 * cpt.inc(); } });
	 * 
	 * c2.setStrategy(new IStrategy() {
	 * 
	 * @Override public void processMessage(Component _this, Mediator mediator,
	 * String data) { System.out.println("PROCESS "+_this.getName()); Mediator m
	 * = new ForwardMediator(mediator, (IInput) c3); m.send((IOutput)_this,
	 * data); // c2 to c3 cpt.inc(); } });
	 * 
	 * c3.setStrategy(new IStrategy() {
	 * 
	 * @Override public void processMessage(Component _this, Mediator mediator,
	 * String data) { System.out.println("PROCESS "+_this.getName());
	 * mediator.send((IOutput) _this, data); // back to c1 cpt.inc(); } });
	 * 
	 * c1.input(null, "DATATEST"); Assert.assertEquals(cpt.toString(), "4"); }
	 */

	@Test
	public void testDoubleForward() {
		final Counter cpt = new Counter();
		final ComponentIO c1 = new ComponentIO("C1");
		final Component c2 = new ComponentIO("C2");
		final Component c3 = new ComponentIO("C3");
		final Component c4 = new ComponentIO("C4");

		c1.setStrategy(new IStrategy() {
			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public IResponse processMessage(Component _this, Mediator mediator, String data) {
				System.out.println("PROCESS " + _this.getName());
				System.out.println("data = " + data);
				if (mediator == null) {
					Mediator m = MediatorFactory.getInstance().getMediator(_this, c2, EMediator.HALFDUPLEX);
					m.send((IOutput) _this, data); // c1 to c2
				}
				else {
					Thread.currentThread();
					Thread.dumpStack();
					mediator.send((IOutput) _this, "ALLER2");
				}
				cpt.inc();

				return VoidResponse.build();
			}

			@Override
			public void processEvent(Component _this, String event) {
				// TODO Auto-generated method stub

			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}
		});

		c2.setStrategy(new IStrategy() {
			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public IResponse processMessage(Component _this, Mediator mediator, String data) {
				System.out.println("PROCESS " + _this.getName());
				System.out.println("data = " + data);
				Mediator m = new ForwardMediator(mediator, (IInput) c3);
				m.send((IOutput) _this, data); // c2 to c3
				cpt.inc();

				return VoidResponse.build();
			}

			@Override
			public void processEvent(Component _this, String event) {
				// TODO Auto-generated method stub

			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}
		});

		c3.setStrategy(new IStrategy() {
			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public IResponse processMessage(Component _this, Mediator mediator, String data) {
				System.out.println("PROCESS " + _this.getName());
				System.out.println("data = " + data);
				Mediator m = new ForwardMediator(mediator, (IInput) c4);
				m.send((IOutput) _this, data); // c3 to c4
				cpt.inc();

				return VoidResponse.build();
			}

			@Override
			public void processEvent(Component _this, String event) {
				// TODO Auto-generated method stub

			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}
		});

		c4.setStrategy(new IStrategy() {
			@Override
			public void init(IOutput _this, Context ctx) {
			}

			@Override
			public IResponse processMessage(Component _this, Mediator mediator, String data) {
				System.out.println("PROCESS " + _this.getName());
				System.out.println("data = " + data);

				if (!"ALLER2".equals(data)) {
					mediator.send((IOutput) _this, "RETOUR"); // back to c1
					cpt.inc();
				}

				return VoidResponse.build();
			}

			@Override
			public void processEvent(Component _this, String event) {
			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}
		});

		Simulator.resume();
		c1.notifyMessage(null, "ALLER1");
		Assert.assertEquals(cpt.toString(), "5");
	}
}
