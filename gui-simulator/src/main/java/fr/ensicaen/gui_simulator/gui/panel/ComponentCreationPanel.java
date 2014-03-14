package fr.ensicaen.gui_simulator.gui.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.mxgraph.util.mxResources;

import fr.ensicaen.gui_simulator.gui.bridge.PropertiesOfStrategyJTableBridge;
import fr.ensicaen.gui_simulator.gui.bridge.StrategyComboBoxBridge;
import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.component.ComponentI;
import fr.ensicaen.simulator.model.component.ComponentIO;
import fr.ensicaen.simulator.model.component.ComponentO;
import fr.ensicaen.simulator.model.dao.factory.DAOFactory;
import fr.ensicaen.simulator.model.properties.PropertyDefinition;
import fr.ensicaen.simulator.model.strategies.IStrategy;

public class ComponentCreationPanel extends JDialog {

	private static final long serialVersionUID = 6023326846638316412L;
	
	private JLabel label_nameOfComponent;
	private JLabel label_typeOfComponent;
	private JLabel label_formatOfComponent;
	private JLabel label_typeOfStrategy;
	private JLabel label_parametersOfStrategy;
	
	private ButtonGroup buttonGroup;
	private JRadioButton checkbox_inComponent;
	private JRadioButton checkbox_outComponent;
	private JRadioButton checkbox_inoutComponent;
	
	private JTextField nameOfComponent;
	private JTextField formatOfComponent;
	private JComboBox strategies;
	
	private JScrollPane parametersScroll;
	private JTable parametersOfStrategy;
	private PropertiesOfStrategyJTableBridge jtableModel;
	
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
		setResizable(false);
		
		label_nameOfComponent = new JLabel (mxResources.get("name_of_component"));
		label_typeOfComponent = new JLabel (mxResources.get("type_of_component"));
		label_formatOfComponent = new JLabel (mxResources.get("format_of_component"));
		
		label_typeOfStrategy = new JLabel (mxResources.get("type_of_strategy"));
		label_parametersOfStrategy = new JLabel (mxResources.get("parameters_of_strategy"));
		buttonGroup = new ButtonGroup();
		checkbox_inComponent = new JRadioButton(mxResources.get("in_component"));
		checkbox_outComponent = new JRadioButton(mxResources.get("out_component"));
		checkbox_inoutComponent = new JRadioButton(mxResources.get("inout_component"));
		buttonGroup.add(checkbox_inComponent);
		buttonGroup.add(checkbox_outComponent);
		buttonGroup.add(checkbox_inoutComponent);
		checkbox_inComponent.setEnabled(false);
		checkbox_outComponent.setEnabled(false);
		checkbox_inoutComponent.setEnabled(false);
		
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
		
		nameOfComponent = new JTextField("Default name", 50);
		nameOfComponent.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					formatOfComponent.setEnabled(true);
					nameOfComponent.setEnabled(false);
				}		
			}
		);
		
		formatOfComponent = new JTextField("0", 10);
		formatOfComponent.setEnabled(false);
		formatOfComponent.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (isInteger(formatOfComponent.getText())) {
						formatOfComponent.setEnabled(false);
						checkbox_inComponent.setEnabled(true);
						checkbox_outComponent.setEnabled(true);
						checkbox_inoutComponent.setEnabled(true);	
					}
					
				}		
			}
		);
		
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
		parametersOfStrategy = new JTable(2,3);
		parametersOfStrategy.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		parametersScroll = new JScrollPane(parametersOfStrategy);
		
		
		jtableModel = new PropertiesOfStrategyJTableBridge();
		parametersOfStrategy.setModel(jtableModel);
		parametersOfStrategy.getTableHeader().setResizingAllowed(true);
		parametersOfStrategy.getTableHeader().setReorderingAllowed(false);
		
		create = new JButton(mxResources.get("create_button"));
		create.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					DAOFactory.getFactory().getComponentDAO().create(component);
					
					closeWindow();
				}		
			}
		);
		create.setEnabled(false);
		
		cancel = new JButton(mxResources.get("cancel_button"));
		cancel.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					closeWindow();
				}		
			}
		);
		
		myLayout.putConstraint(SpringLayout.NORTH, label_nameOfComponent, 10, SpringLayout.NORTH, this);
		myLayout.putConstraint(SpringLayout.WEST, label_nameOfComponent, 10, SpringLayout.WEST, this);
		
		myLayout.putConstraint(SpringLayout.NORTH, nameOfComponent, 00, SpringLayout.NORTH, label_nameOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, nameOfComponent, 150, SpringLayout.WEST, this);
		
		
		
		myLayout.putConstraint(SpringLayout.NORTH, label_formatOfComponent, 10, SpringLayout.SOUTH, label_nameOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, label_formatOfComponent, 0, SpringLayout.WEST, label_nameOfComponent);
		
		myLayout.putConstraint(SpringLayout.NORTH, formatOfComponent, 10, SpringLayout.SOUTH, label_nameOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, formatOfComponent, 150, SpringLayout.WEST, this);
		
		
		
		myLayout.putConstraint(SpringLayout.NORTH, label_typeOfComponent, 10, SpringLayout.SOUTH, label_formatOfComponent);
		myLayout.putConstraint(SpringLayout.WEST, label_typeOfComponent, 0, SpringLayout.WEST, label_formatOfComponent);
		
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
		
		myLayout.putConstraint(SpringLayout.NORTH, label_parametersOfStrategy, 10, SpringLayout.SOUTH, strategies);
		myLayout.putConstraint(SpringLayout.WEST, label_parametersOfStrategy, 00, SpringLayout.WEST, label_formatOfComponent);
				
		myLayout.putConstraint(SpringLayout.NORTH, parametersScroll, 10, SpringLayout.SOUTH, strategies);
		myLayout.putConstraint(SpringLayout.SOUTH, parametersScroll, 150, SpringLayout.SOUTH, strategies);
		myLayout.putConstraint(SpringLayout.WEST, parametersScroll, 150, SpringLayout.WEST, this);
		
		myLayout.putConstraint(SpringLayout.NORTH, cancel, 10, SpringLayout.SOUTH, parametersScroll);
		myLayout.putConstraint(SpringLayout.WEST, cancel, 300, SpringLayout.WEST, this);
		
		myLayout.putConstraint(SpringLayout.NORTH, create, 10, SpringLayout.SOUTH, parametersScroll);
		myLayout.putConstraint(SpringLayout.WEST, create, 400, SpringLayout.WEST, this);
		
		this.add(label_nameOfComponent);
		this.add(nameOfComponent);
		this.add(label_typeOfComponent);
		this.add(checkbox_inComponent);
		this.add(checkbox_outComponent);
		this.add(checkbox_inoutComponent);
		this.add(label_formatOfComponent);
		this.add(formatOfComponent);
		this.add(label_typeOfStrategy);
		this.add(strategies);
		this.add(label_parametersOfStrategy);
		this.add(parametersScroll);
		this.add(cancel);
		this.add(create);

		this.setModal(true);
		this.setSize(650, 350);
		this.setVisible(true);
	}
	
	public void closeWindow () {
		SwingUtilities.windowForComponent(this).dispose();
	}
	
	public void loadStrategy (ActionEvent arg0) {
		jtableModel.setProperties(((IStrategy) strategies.getSelectedItem()).getPropertyDefinitions());	
		jtableModel.fireTableDataChanged();
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public void changeTypeOfComponent (ActionEvent arg0) {
		if (arg0.getSource().equals(checkbox_inComponent)) {
			component = new ComponentI();
		} else if (arg0.getSource().equals(checkbox_outComponent)) {
			component = new ComponentO();
		} else if (arg0.getSource().equals(checkbox_inoutComponent)) {
			component = new ComponentIO();
		}
		component.setName(nameOfComponent.getText());
		strategies.setEnabled(true);
		checkbox_inComponent.setEnabled(false);
		checkbox_outComponent.setEnabled(false);
		checkbox_inoutComponent.setEnabled(false);
		create.setEnabled(true);
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
