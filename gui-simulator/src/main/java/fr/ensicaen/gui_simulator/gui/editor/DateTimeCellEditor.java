package fr.ensicaen.gui_simulator.gui.editor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.text.MaskFormatter;

public class DateTimeCellEditor extends AbstractCellEditor implements TableCellEditor {

	private JFormattedTextField txtField;

	public DateTimeCellEditor() {
		MaskDateFormatter date = new MaskDateFormatter();
		txtField = new JFormattedTextField(date);
	}

	public JFormattedTextField getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
			int rowIndex, int colIndex) {
		txtField.setValue(value);
		return txtField;
	}

	public Object getCellEditorValue() {
		return txtField.getValue();
	}

	private class MaskDateFormatter extends MaskFormatter {

		public MaskDateFormatter() { // set mask and placeholder
			try {
				setMask("##/##/#### ##:##:##");
				setPlaceholderCharacter('0');
				setAllowsInvalid(false);
				setOverwriteMode(true);
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}

		@Override
		public Object stringToValue(String string) throws ParseException {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			if (string == null) {
				string = "00/00/0000 00:00:00";
			}
			return df.parse(string);
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			if (value == null) {
				value = new Date(0);
			}

			return df.format((Date) value);
		}
	}
}