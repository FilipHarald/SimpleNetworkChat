package server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("[")
			.append(SIMPLE_DATE_FORMAT.format(new Date(record.getMillis())))
			.append("] [")
			.append(record.getLevel().getLocalizedName())
			.append("] ")
			.append(formatMessage(record))
			.append(LINE_SEPARATOR);
		
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
				// do nothing
			}
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}

}
