package fr.ensicaen.gui_simulator.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import fr.ensicaen.simulator.model.component.Component;
import fr.ensicaen.simulator.model.strategies.IStrategy;

/**
 * Permet d'afficher de l'aide (sur le componsant & strategie) via des pages
 * html disponibles dans /doc.
 * 
 * @author JM
 * 
 */
public class HelpPanel extends JDialog implements ActionListener {
	private JPanel panel;
	private JButton bt_quit;
	private Component component;
	private IStrategy strategy = null;
	private JTabbedPane tabbedPane;
	private JEditorPane tp_component;
	private JEditorPane tp_strategy;
	public static final String PATH_DOC_COMPONENTS = "doc/components/";
	public static final String PATH_DOC_STRATEGIES = "doc/strategies/";
	public static URL TODO_PAGE;

	/**
	 * Afficher l'aide du composant
	 * 
	 * @param c
	 * @wbp.parser.constructor
	 */
	public HelpPanel(Component c) {
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		component = c;
		// TODO Auto-generated constructor stub
		initGUI();
		getInformations();
	}

	/**
	 * Afficher l'aide de la strategie
	 * 
	 * @param _strat
	 */
	public HelpPanel(IStrategy _strat) {
		strategy = _strat;
		// TODO Auto-generated constructor stub
		initGUI();
		getInformations();
	}

	private void initGUI() {
		setSize(800, 600);
		setLocationRelativeTo(null);
		try {
			TODO_PAGE = Paths.get(PATH_DOC_COMPONENTS + "todo.html").toUri()
					.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setTitle("Aide");
		getContentPane().setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		bt_quit = new JButton("Quitter");
		bt_quit.addActionListener(this);
		panel.add(bt_quit);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		tp_component = new JEditorPane();
		tp_component.setEditable(false);
		tabbedPane.addTab("Composant", null, tp_component, null);

		tp_strategy = new JEditorPane();
		tp_strategy.setEditable(false);
		tabbedPane.addTab("Strategie", null, tp_strategy, null);
	}

	public void getInformations() {
		boolean strat = false;
		// desactive les elements inutilisables selon le contexte
		if (component != null) {
			if (component.getStrategy() == null) {
				tabbedPane.setEnabledAt(1, false);
			} else {
				strat = true;
			}

			try {
				tp_component.setPage(Paths
						.get(PATH_DOC_COMPONENTS + component.getName()
								+ ".html").toUri().toURL());
			} catch (IOException e1) {
				printTodoPage(tp_component);
			}

		} else if (strategy != null) {
			tabbedPane.setEnabledAt(0, false);
			strat = true;
		}

		if (strat) {
			try {
				tp_strategy.setPage(Paths
						.get(PATH_DOC_STRATEGIES + strategy + ".html").toUri()
						.toURL());
			} catch (IOException e) {
				printTodoPage(tp_strategy);
			}
		}
	}

	private void printTodoPage(JEditorPane pane) {
		try {
			pane.setPage(TODO_PAGE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bt_quit) {
			this.dispose();
		}
	}
}
