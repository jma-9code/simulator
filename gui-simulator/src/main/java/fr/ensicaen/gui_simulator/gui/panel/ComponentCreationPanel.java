package fr.ensicaen.gui_simulator.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.mxgraph.util.mxResources;

import fr.ensicaen.gui_simulator.gui.bridge.StrategyComboBoxBridge;
import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.ComponentI;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.ComponentO;

public class ComponentCreationPanel extends JDialog {

	private static final long serialVersionUID = 6023326846638316412L;
	
	private JLabel label_nameOfComponent;
	private JLabel label_typeOfComponent;
	private JLabel label_typeOfStrategy;
	
	private ButtonGroup buttonGroup;
	private JRadioButton checkbox_inComponent;
	private JRadioButton checkbox_outComponent;
	private JRadioButton checkbox_inoutComponent;
	
	private JTextField nameOfComponent;
	private JComboBox strategies;
	private 
	
	private JTable parametersOfStrategy;
	private JButton create;
	private JButton cancel;
	private Component component;
	
	private StrategyComboBoxBridge comboStrategy;
	
	public ComponentCreationPanel () {
		init();
	}
	
	public void init() {
		SpringLayout myLayout = new SpringLayout();
		this.setLayout(myLayout);
		label_nameOfComponent = new JLabel (mxResources.get("name_of_component"));
		label_typeOfComponent = new JLabel (mxResources.get("type_of_component"));
		label_typeOfStrategy = new JLabel (mxResources.get("type_of_strategy"));;
		buttonGroup = new ButtonGroup();
		checkbox_inComponent = new JRadioButton(mxResources.get("in_component"));
		checkbox_outComponent = new JRadioButton(mxResources.get("out_component"));
		checkbox_inoutComponent = new JRadioButton(mxResources.get("inout_component"));
		buttonGroup.add(checkbox_inComponent);
		buttonGroup.add(checkbox_outComponent);
		buttonGroup.add(checkbox_inoutComponent);
		checkbox_inComponent.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					changeTypeOfComponent(arg0);
				}		
			}
		);
		checkbox_outComponent.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					changeTypeOfComponent(arg0);
				}		
			}
		);
		checkbox_inoutComponent.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					changeTypeOfComponent(arg0);
				}		
			}
		);
		
		nameOfComponent = new JTextField(50);
		comboStrategy = new StrategyComboBoxBridge();
		strategies = new JComboBox(comboStrategy);
		strategies.setEnabled(false);
		strategies.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					loadStrategy(arg0);
				}		
			}
		);
		parametersOfStrategy = new JTable();
		myLayout.putConstraint(SpringLayout.NORTH, label_nameOfComponent, 10, SpringLayout.NORTH, this);
		myLayout.putConstraint(SpringLayout.WEST, label_nameOfComponent, 10, SpringLayout.WEST, this);
		
		myLayout.putConstraint(SpringLayout.NORTH, nameOfComponent, 00, SpringLayout.NORTH, label_nameOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, nameOfComponent, 150, SpringLayout.WEST, this);
		
		myLayout.putConstraint(SpringLayout.NORTH, label_typeOfComponent, 10, SpringLayout.SOUTH, label_nameOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, label_typeOfComponent, 0, SpringLayout.WEST, label_nameOfComponent);
		
		myLayout.putConstraint(SpringLayout.NORTH, checkbox_inComponent, 00, SpringLayout.NORTH, label_typeOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, checkbox_inComponent, 0, SpringLayout.WEST, nameOfComponent);
		
		myLayout.putConstraint(SpringLayout.NORTH, checkbox_outComponent, 00, SpringLayout.NORTH, label_typeOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, checkbox_outComponent, 20, SpringLayout.EAST, checkbox_inComponent);
		
		myLayout.putConstraint(SpringLayout.NORTH, checkbox_inoutComponent, 00, SpringLayout.NORTH, label_typeOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, checkbox_inoutComponent, 20, SpringLayout.EAST, checkbox_outComponent);
		
		myLayout.putConstraint(SpringLayout.NORTH, label_typeOfStrategy, 10, SpringLayout.SOUTH, label_typeOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, label_typeOfStrategy, 00, SpringLayout.WEST, label_nameOfComponent);
		
		myLayout.putConstraint(SpringLayout.NORTH, strategies, 10, SpringLayout.SOUTH, label_typeOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, strategies, 150, SpringLayout.WEST, this);
		
		myLayout.putConstraint(SpringLayout.NORTH, parametersOfStrategy, 10, SpringLayout.SOUTH, strategies);
		myLayout.putConstraint(SpringLayout.WEST, parametersOfStrategy, 150, SpringLayout.WEST, this);
		
		this.add(label_nameOfComponent);
		this.add(nameOfComponent);
		this.add(label_typeOfComponent);
		this.add(checkbox_inComponent);
		this.add(checkbox_outComponent);
		this.add(checkbox_inoutComponent);
		this.add(label_typeOfStrategy);
		this.add(strategies);
		this.add(parametersOfStrategy);
		this.setModal(true);
		this.setSize(650, 500);
		this.setVisible(true);
	}
	
	public void loadStrategy (ActionEvent arg0) {
		//strategies.getSelectedItem();
	}
	
	public void changeTypeOfComponent (ActionEvent arg0) {
		if (arg0.getSource().equals(checkbox_inComponent)) {
			System.out.println("in");
			component = new ComponentI();
		} else if (arg0.getSource().equals(checkbox_outComponent)) {
			System.out.println("out");
			component = new ComponentO();
		} else if (arg0.getSource().equals(checkbox_inoutComponent)) {
			System.out.println("inout");
			component = new ComponentIO();
		}
		strategies.setEnabled(true);
		comboStrategy.update(component);
	}

	public JTextField getNameOfComponent() {
		return nameOfComponent;
	}

	public void setNameOfComponent(JTextField nameOfComponent) {
		this.nameOfComponent = nameOfComponent;
	}

	public JComboBox getStrategies() {
		return strategies;
	}

	public void setStrategies(JComboBox strategies) {
		this.strategies = strategies;
	}

	public JTable getParametersOfStrategy() {
		return parametersOfStrategy;
	}

	public void setParametersOfStrategy(JTable parametersOfStrategy) {
		this.parametersOfStrategy = parametersOfStrategy;
	}

	public JButton getCreate() {
		return create;
	}

	public void setCreate(JButton create) {
		this.create = create;
	}

	public JButton getCancel() {
		return cancel;
	}

	public void setCancel(JButton cancel) {
		this.cancel = cancel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
