package fr.ensicaen.gui_simulator.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;

import fr.ensicaen.gui_simulator.gui.bridge.MediatorWrapper;
import fr.ensicaen.simulator_ep.utils.ISOTools;
import fr.ensicaen.simulator_ep.utils.ProtocolEP;

public class MediatorAnalysisPanel extends JDialog implements ActionListener {
	private final JPanel pnl_north = new JPanel();
	private final JPanel pnl_south = new JPanel();
	private final JScrollPane sp_center = new JScrollPane();
	private final JButton bt_ok = new JButton("OK");
	private final JLabel lbl_informations = new JLabel("");
	private JTable jtable_parsedData;
	private MediatorWrapper mediatorwrapper = null;
	private GenericPackager packager = null;

	/**
	 * Create the dialog.
	 */
	public MediatorAnalysisPanel(MediatorWrapper _mediatorwapper) {
		mediatorwrapper = _mediatorwapper;
		initGUI();
	}

	private void initGUI() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) pnl_north.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		getContentPane().add(pnl_north, BorderLayout.NORTH);

		pnl_north.add(lbl_informations);

		getContentPane().add(pnl_south, BorderLayout.SOUTH);
		pnl_south.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		bt_ok.addActionListener(this);

		pnl_south.add(bt_ok);

		getContentPane().add(sp_center, BorderLayout.CENTER);
		String protocol = mediatorwrapper.getMediator().getProperties()
				.get("protocol");

		// protocol is set
		if (protocol != null && !protocol.isEmpty()) {
			try {
				packager = new GenericPackager(MediatorAnalysisPanel.class
						.getResource(
								"/" + ProtocolEP.valueOf(protocol).toString()
										+ ".xml").toURI().getPath());
				jtable_parsedData = new JTable(
						new DataTable(ISOTools.readISOMsg(
								mediatorwrapper.getData(), packager)));
			} catch (ISOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			jtable_parsedData = new JTable(new DataTable(Arrays.asList(Arrays
					.asList("0", mediatorwrapper.getData(), "raw data"))));
		}
		sp_center.setViewportView(jtable_parsedData);

		// set informations
		lbl_informations.setText(mediatorwrapper.getMediator().toString());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bt_ok) {
			do_bt_ok_actionPerformed(e);
		}
	}

	// close windows when click bt OK
	protected void do_bt_ok_actionPerformed(ActionEvent e) {
		this.dispose();
	}

	/**
	 * Permet d'afficher les donnees contenues dans le mediator
	 * 
	 * @author JM
	 * 
	 */
	private class DataTable extends AbstractTableModel {

		// id / value (0) description (1)
		private List<List<String>> datas;

		public DataTable(List<List<String>> _datas) {
			datas = _datas;
		}

		@Override
		public int getRowCount() {
			return datas.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(final int col) {
			switch (col) {
			case 0:
				return "Id";
			case 1:
				return "Value";
			case 2:
				return "Description";
			default:
				return null;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < datas.size()
					&& columnIndex < datas.get(rowIndex).size()) {
				return datas.get(rowIndex).get(columnIndex);
			}
			return null;
		}
	}
}
