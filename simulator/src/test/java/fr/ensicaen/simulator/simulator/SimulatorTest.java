package fr.ensicaen.simulator.simulator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.IOutput;
import fr.ensicaen.simulator.model.mediator.HalfDuplexMediator;
import fr.ensicaen.simulator.model.mediator.Mediator;
import fr.ensicaen.simulator.model.mediator.SimplexMediator;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.response.IResponse;
import fr.ensicaen.simulator.model.response.VoidResponse;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.model.strategies.NullStrategy;
import fr.ensicaen.simulator.simulator.exception.SimulatorException;

public class SimulatorTest {

	@Before
	public void beforeTest() {
		System.out.println("---------NEW TEST---------");

		Context ctx = Context.getInstance();
		ctx.reset();
		ctx.autoRegistrationMode();
	}

	@Test(expected = SimulatorException.class)
	public void testWithNoStartPoint() throws SimulatorException {
		Simulator simulator = SimulatorFactory.getSimulator();
		simulator.start();
	}

	@Test
	public void testAsyncWithNoStartPoint() throws SimulatorException {
		AsyncSimulator simulator = SimulatorFactory.getAsyncSimulator();
		simulator.start();

		try {
			simulator.waitUntilEnd();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertNotNull(simulator.getException());
	}

	@Test
	public void testWithOneStartPoint() throws SimulatorException {
		// setting simple model
		ComponentIO florent = new ComponentIO("personne");
		florent.getProperties().put("nom", "moisson");
		florent.getProperties().put("prenom", "florent");
		florent.getProperties().put("age", "22");
		florent.getProperties().put("adresse", "...........");
		florent.setStrategy(new NullStrategy());

		ComponentIO bank = new ComponentIO("banque");
		bank.getProperties().put("marque", "bnp");
		bank.setStrategy(new NullStrategy());

		ComponentIO account = new ComponentIO("compte");
		account.getProperties().put("porteur", "florent moisson");
		account.getProperties().put("montant", "1500");
		account.getProperties().put("plafond", "9000");
		bank.addChild(account);
		account.setStrategy(new NullStrategy());

		// TODO : va surement poser pb si on doit passer les types hérités
		// A l'instanciation, je vois plus passer Component mais on sait pas si
		// c'est
		// IOuput ou IInput ... control dans le mediator ?
		Mediator mediator = new SimplexMediator(florent, bank);

		// setting context
		Context ctx = Context.getInstance();
		short contextIndex = ctx.currentCounter();
		ctx.addStartPoint(new Date(), "TEST EVENT");

		// running simulation
		Simulator simulator = SimulatorFactory.getSimulator();
		simulator.start();

		Assert.assertEquals(ctx.currentCounter(), contextIndex + 1);
	}

	@Test
	public void testAsyncWithOneStartPoint() throws SimulatorException {
		// setting simple model
		ComponentIO florent = new ComponentIO("personne");
		florent.getProperties().put("nom", "moisson");
		florent.getProperties().put("prenom", "florent");
		florent.getProperties().put("age", "22");
		florent.getProperties().put("adresse", "...........");

		ComponentIO bank = new ComponentIO("banque");
		bank.getProperties().put("marque", "bnp");

		ComponentIO account = new ComponentIO("compte");
		account.getProperties().put("porteur", "florent moisson");
		account.getProperties().put("montant", "1500");
		account.getProperties().put("plafond", "9000");
		bank.addChild(account);

		// TODO : va surement poser pb si on doit passer les types hérités
		// A l'instanciation, je vois plus passer Component mais on sait pas si
		// c'est
		// IOuput ou IInput ... control dans le mediator ?
		Mediator mediator = new SimplexMediator(florent, bank);

		// setting context
		Context ctx = Context.getInstance();
		short contextIndex = ctx.currentCounter();
		ctx.addStartPoint(new Date(), "TEST EVENT");

		// running simulation
		AsyncSimulator simulator = SimulatorFactory.getAsyncSimulator();
		simulator.start();

		try {
			simulator.waitUntilEnd();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertNull(simulator.getException());
		Assert.assertEquals(ctx.currentCounter(), contextIndex + 1);
	}

	@Test
	public void testWithDynamicStartPoint() throws SimulatorException {
		// setting simple model
		ComponentIO florent = new ComponentIO("personne");
		florent.getProperties().put("nom", "moisson");
		florent.getProperties().put("prenom", "florent");
		florent.getProperties().put("age", "22");
		florent.getProperties().put("adresse", "...........");
		florent.setStrategy(new NullStrategy());

		final ComponentIO bank = new ComponentIO("banque");
		bank.getProperties().put("marque", "bnp");
		bank.setStrategy(new NullStrategy());
		bank.setStrategy(new IStrategy() {
			@Override
			public void init(IOutput _this, Context ctx) {
				ctx.subscribeEvent(_this, "TEST EVENT 2");
				ctx.subscribeEvent(_this, "TEST EVENT 3");
				ctx.subscribeEvent(_this, "TEST EVENT 4");
			}

			@Override
			public IResponse processMessage(Component component, Mediator mediator, String data) {
				return VoidResponse.build();
			}

			@Override
			public void processEvent(Component _this, String event) {
				if ("TEST EVENT 2".equals(event)) {
					System.out.println("Test Event 2");
					Context ctx = Context.getInstance();
					ctx.addStartPoint(new Date(System.currentTimeMillis() - 3600 * 4 * 1000), "TEST EVENT 3");
					ctx.addStartPoint(new Date(System.currentTimeMillis() + 3600 * 4 * 1000), "TEST EVENT 4");
				}
			}

			@Override
			public List<PropertyDefinition> getPropertyDefinitions() {
				return new ArrayList<PropertyDefinition>();
			}
		});

		ComponentIO account = new ComponentIO("compte");
		account.getProperties().put("porteur", "florent moisson");
		account.getProperties().put("montant", "1500");
		account.getProperties().put("plafond", "9000");
		bank.addChild(account);
		account.setStrategy(new NullStrategy());

		// TODO : va surement poser pb si on doit passer les types hérités
		// A l'instanciation, je vois plus passer Component mais on sait pas si
		// c'est
		// IOuput ou IInput ... control dans le mediator ?
		Mediator mediator1 = new HalfDuplexMediator(florent, bank);
		Mediator mediator2 = new SimplexMediator(bank, account);

		// setting context
		Context ctx = Context.getInstance();
		short contextIndex = ctx.currentCounter();
		ctx.addStartPoint(new Date(), "TEST EVENT 1");
		ctx.addStartPoint(new Date(), "TEST EVENT 2");
		// ajout dynamique en strategy
		// ctx.addStartPoint(new Date(), bank, mediator1, "TEST CTX 3");

		// running simulation
		Simulator simulator = SimulatorFactory.getSimulator();
		simulator.start();
		Simulator.resume();

		Assert.assertEquals(ctx.currentCounter(), contextIndex + 4);
	}

	@Test
	public void testStepbyStepSimulator() {
		ComponentIO card = new ComponentIO("Card");
		ComponentIO tpe = new ComponentIO("TPE");

	}
}
