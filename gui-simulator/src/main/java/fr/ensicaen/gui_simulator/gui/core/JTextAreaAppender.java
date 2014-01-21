package fr.ensicaen.gui_simulator.gui.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.encoder.Encoder;

/**
 * Appender LogBack for JTextArea (swing component)
 * 
 * @author Florent Moisson
 */
public class JTextAreaAppender extends AppenderBase<ILoggingEvent> {
	private static Logger logger = LoggerFactory.getLogger(JTextAreaAppender.class);

	private Encoder<ILoggingEvent> encoder = new EchoEncoder<ILoggingEvent>();
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private JTextArea textArea;

	public JTextAreaAppender(JTextArea textArea) {
		logger.info("Initialization of GUI Console...");
		this.textArea = textArea;

		// set ctx & launch
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		setContext(lc);
		start();

		// auto-add
		lc.getLogger("ROOT").addAppender(this);
	}

	@Override
	public void start() {
		try {
			encoder.init(out);
		}
		catch (IOException e) {
		}

		super.start();
	}

	@Override
	public void append(ILoggingEvent event) {
		try {
			encoder.doEncode(event);
			out.flush();
			String line = out.toString();
			textArea.append(line);
			out.reset();
		}
		catch (IOException e) {
		}
	}

}