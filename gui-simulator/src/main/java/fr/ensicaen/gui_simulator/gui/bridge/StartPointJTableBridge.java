package fr.ensicaen.gui_simulator.gui.bridge;

import java.util.Date;
import java.util.Iterator;
import java.util.Queue;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mxgraph.util.mxResources;

import fr.ensicaen.simulator.simulator.StartPoint;

public class StartPointJTableBridge extends AbstractTableModel {

	private static Logger logger = LoggerFactory
			.getLogger(StartPointJTableBridge.class);

	private static final String NEW_TAG = mxResources.get("new_entry");
	private Queue<StartPoint> queue;

	private boolean addRowEnabled = false;

	public StartPointJTableBridge(Queue<StartPoint> queue) {
		this.queue = queue;
	}

	@Override
	public int getRowCount() {
		if (addRowEnabled) {
			return queue != null ? queue.size() + 1 : 0;
		} else {
			return queue != null ? queue.size() : 0;
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return mxResources.get("datetime");
		case 1:
			return mxResources.get("event");
		default:
			return "";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Date.class;
		default:
			return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// logger.debug("getValueAt(" + rowIndex + ", " + columnIndex + ")");
		switch (columnIndex) {
		case 0:
			return (addRowEnabled && rowIndex == getRowCount() - 1) ? new Date()
					: getStartPoint(rowIndex).getTime();
		case 1:
			return (addRowEnabled && rowIndex == getRowCount() - 1) ? NEW_TAG
					: getStartPoint(rowIndex).getEvent();
		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// [" + aValue.getClass() + "]
		logger.debug("setValueAt(" + aValue + ", " + rowIndex + ", "
				+ columnIndex + ")");
		switch (columnIndex) {
		case 0:
			if (addRowEnabled && rowIndex == getRowCount() - 1) {
				queue.add(new StartPoint((Date) aValue, (String) getValueAt(
						rowIndex, 1)));
				addRowEnabled = false;
				fireTableDataChanged();
			} else {
				getStartPoint(rowIndex).setTime((Date) aValue);
			}

			break;
		case 1:
			if (addRowEnabled && rowIndex == getRowCount() - 1) {
				queue.add(new StartPoint((Date) getValueAt(rowIndex, 0),
						(String) aValue));
				addRowEnabled = false;
				fireTableDataChanged();
			} else {
				getStartPoint(rowIndex).setEvent((String) aValue);
			}
			break;
		default:
			return;
		}
	}

	private StartPoint getStartPoint(int index) {
		Iterator<StartPoint> ite = queue.iterator();
		StartPoint cur = null;
		for (int i = 0; ite.hasNext() && i <= index; i++) {
			cur = ite.next();
		}
		return cur;
	}

	/**
	 * Supprime un enregistrement
	 * 
	 * @param rowIndex
	 */
	public void deleteRow(int rowIndex) {
		if (!addRowEnabled || rowIndex != getRowCount() - 1) {
			queue.remove(getStartPoint(rowIndex));
		}
		addRowEnabled = false;
		fireTableDataChanged();
	}

	/**
	 * Affiche la ligne temporaire d'ajout d'un enregistrement
	 */
	public void enableAddRow() {
		this.addRowEnabled = true;
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}

	public boolean isAddRowEnabled() {
		return addRowEnabled;
	}

}
