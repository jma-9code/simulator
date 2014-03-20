package fr.ensicaen.gui_simulator.gui.bridge;

import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mxgraph.util.mxResources;

public class MapJTableBridge extends AbstractTableModel {

	private static final String NEW_TAG = mxResources.get("new_entry");

	private static Logger logger = LoggerFactory.getLogger(MapJTableBridge.class);

	private Map<String, Object> map;

	// optimization
	private String[] keyIndex;
	private int keyIndexHash;

	private boolean addRowEnabled = false;

	public MapJTableBridge(Map<String, Object> map) {
		this.map = map;
		indexesKeys();
	}

	public MapJTableBridge() {
	}

	@Override
	public int getRowCount() {
		if (addRowEnabled) {
			return map != null ? map.size() + 1 : 0;
		}
		else {
			return map != null ? map.size() : 0;
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
				return mxResources.get("key");
			case 1:
				return mxResources.get("value");
			default:
				return "";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			default:
				return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return (addRowEnabled && rowIndex == getRowCount() - 1);
			case 1:
				return (!addRowEnabled || rowIndex != getRowCount() - 1);
			default:
				return false;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return (addRowEnabled && rowIndex == getRowCount() - 1) ? NEW_TAG : getKey(rowIndex);
			case 1:
				return (addRowEnabled && rowIndex == getRowCount() - 1) ? NEW_TAG : map.get(getKey(rowIndex));
			default:
				return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		logger.debug("setValueAt(" + aValue + ", " + rowIndex + ", " + columnIndex + ")");
		switch (columnIndex) {
			case 0:
				String key = String.valueOf(aValue).trim();
				if (!key.equals(NEW_TAG) && !key.isEmpty()) {
					map.put(key, String.valueOf(getValueAt(rowIndex, 1)));
					addRowEnabled = false;
					fireTableDataChanged();
				}
				break;
			case 1:
				if (!addRowEnabled || rowIndex != getRowCount() - 1) {
					map.put(getKey(rowIndex), String.valueOf(aValue));
				}
				break;
			default:
				return;
		}
	}

	private String getKey(int rowIndex) {
		if (map.size() != keyIndex.length || map.hashCode() != keyIndexHash) {
			indexesKeys();
		}
		return keyIndex[rowIndex];
	}

	private void indexesKeys() {
		logger.debug("key indexation");
		Set<String> set = map.keySet();
		keyIndex = set.toArray(new String[0]);
		keyIndexHash = map.hashCode();
	}

	public void reset() {
		logger.debug("reset");
		this.map = null;
		this.keyIndex = null;
		this.keyIndexHash = 0;
		this.addRowEnabled = false;
		fireTableStructureChanged();
	}

	/**
	 * Met à jour le modèle avec une nouvelle hash map
	 * 
	 * @param map
	 */
	public void update(Map map) {
		if (map == null) {
			logger.debug("update with no entry");
		}
		else {
			logger.debug("update with " + map.size() + " entries");
		}

		this.map = map;
		indexesKeys();
		fireTableStructureChanged();
	}

	/**
	 * Supprime un enregistrement
	 * 
	 * @param rowIndex
	 */
	public void deleteRow(int rowIndex) {
		if (!addRowEnabled || rowIndex != getRowCount() - 1) {
			map.remove(getKey(rowIndex));
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
