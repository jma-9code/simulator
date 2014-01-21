package fr.ensicaen.gui_simulator.gui.bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.strategies.IStrategy;
import fr.ensicaen.simulator.model.strategies.NullStrategy;

public class StrategyComboBoxBridge extends AbstractListModel<IStrategy>
		implements ComboBoxModel<IStrategy> {

	private static Logger logger = LoggerFactory
			.getLogger(StrategyComboBoxBridge.class);

	private List<IStrategy> strategies = new ArrayList<>();
	// private DAO<IStrategy> dao;
	private IStrategy selection;
	private IStrategy nullStrategy;

	private Component component;

	public StrategyComboBoxBridge() {
		/*
		 * this.dao = DAOFactory.getFactory().getStrategyDAO(); this.strategies
		 * = dao.findAll(); Collections.sort(strategies,
		 * ComparatorFactory.withToString());
		 * 
		 * if (strategies != null) { logger.debug(strategies.size() +
		 * " loaded"); } else { logger.debug("no strategy loaded"); }
		 */

		Reflections reflections = new Reflections("fr.ensicaen");
		Set<Class<? extends IStrategy>> classes = reflections
				.getSubTypesOf(IStrategy.class);
		for (Class<? extends IStrategy> s : classes) {
			try {
				strategies.add(s.newInstance());
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// seek the null strategy (default)
		for (IStrategy strategy : strategies) {
			if (strategy instanceof NullStrategy) {
				this.nullStrategy = strategy;
				this.selection = strategy;
				break;
			}
		}
	}

	@Override
	public int getSize() {
		return strategies != null ? strategies.size() : 0;
	}

	@Override
	public IStrategy getElementAt(int index) {
		return strategies.get(index);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		if (anItem == null) {
			// debug à supprimer qd yaura qu'une seule instance par stratégie
			if (!strategies.contains(anItem)) {
				strategies.add((IStrategy) anItem);
			}

			component.setStrategy(nullStrategy);
		} else {
			component.setStrategy((IStrategy) anItem);
		}

		fireContentsChanged(this, 0, getSize());
	}

	@Override
	public IStrategy getSelectedItem() {
		return component != null ? component.getStrategy() : nullStrategy;
	}

	public void update(Component c) {
		this.component = c;

		if (c.getStrategy() == null) {
			c.setStrategy(nullStrategy);
		}

		fireContentsChanged(this, 0, getSize());
	}

}
