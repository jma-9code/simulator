package gui.panel;

import gui.core.JTextAreaAppender;
import gui.core.LimitLinesDocumentListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.util.mxResources;

public class ConsolePanel extends JTabbedPane {

	private static Logger logger = LoggerFactory.getLogger(ConsolePanel.class);

	private JTextArea txta_console;

	public ConsolePanel(BasicGraphEditor frame) {
		// tab
		addTab(mxResources.get("console"), new JScrollPane(initTab_console()));
	}

	private JComponent initTab_console() {
		// init txtarea
		txta_console = new JTextArea();
		DefaultCaret caret = (DefaultCaret) txta_console.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		txta_console.setEditable(false);
		txta_console.getDocument().addDocumentListener(new LimitLinesDocumentListener(50));
		txta_console.setRows(5);

		JTextAreaAppender appender = new JTextAreaAppender(txta_console);

		// filter all technic log
		Filter filter = new SimulatorLogFilter();
		appender.addFilter(filter);

		return txta_console;
	}

	private class SimulatorLogFilter extends Filter<ILoggingEvent> {

		public FilterReply decide(ILoggingEvent event) {
			if (event.getLoggerName().startsWith("ep.") || event.getLoggerName().startsWith("simulator.")) {
				return FilterReply.ACCEPT;
			}

			return FilterReply.DENY;
		}
	}

	public void reset() {
		txta_console.setText(null);
	}
}
