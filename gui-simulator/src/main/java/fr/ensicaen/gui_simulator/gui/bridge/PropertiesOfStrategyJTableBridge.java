package fr.ensicaen.gui_simulator.gui.bridge;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class PropertiesOfStrategyJTableBridge extends AbstractTableModel {

	private ArrayList<Property> proprietesStrategy;

	private static final long serialVersionUID = -5803605093352590693L;

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return propertiesStrategy.get(rowIndex).getName();
				break;
			
			case 1:
				return propertiesStrategy.get(rowIndex).getValue();
				break;
		
			case 2:
				return propertiesStrategy.get(rowIndex).getComment();
				break;
			default:
				return " ";
				break;
		}
	}
	
	

}
