package fr.ensicaen.gui_simulator.gui.bridge;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.mxgraph.util.mxResources;

import fr.ensicaen.simulator.model.properties.PropertyDefinition;

public class PropertiesOfStrategyJTableBridge extends AbstractTableModel {

	private List<PropertyDefinition> propertiesStrategy = new ArrayList<PropertyDefinition>();

	private static final long serialVersionUID = -5803605093352590693L;
	private String[] columnNames;
	
	public PropertiesOfStrategyJTableBridge () {
		columnNames = new String[3];
		columnNames[0] = mxResources.get("name_of_parameter");
		columnNames[1] = mxResources.get("value_of_parameter");
		columnNames[2] = mxResources.get("comment_of_parameter");
	}
	
	public PropertiesOfStrategyJTableBridge (List<PropertyDefinition> _propertiesStrategy) {
		propertiesStrategy = _propertiesStrategy;
	}
	
	public void setProperties (List<PropertyDefinition> _propertiesStrategy) {
		propertiesStrategy = _propertiesStrategy;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return propertiesStrategy.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return propertiesStrategy.get(rowIndex).getKey();
			
			case 1:
				return propertiesStrategy.get(rowIndex).getDefaultValue();
		
			case 2:
				return propertiesStrategy.get(rowIndex).getComment();

			default:
				return " ";

		}
	}
	
	public String getColumnName (int column) {
		return columnNames[column];
	}
	
	public boolean isCellEditable (int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return false;
		} else if (propertiesStrategy.get(rowIndex).isWritable()) {
			return true;
		} else {
			return false;
		}
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 1:
				propertiesStrategy.get(rowIndex).setDefaultValue(value.toString());
				break;
			case 2:
				propertiesStrategy.get(rowIndex).setComment(value.toString());
				break;
			default:
				break;
		}
		
	}
}
