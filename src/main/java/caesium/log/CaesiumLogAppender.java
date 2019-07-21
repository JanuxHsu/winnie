package caesium.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class CaesiumLogAppender extends AppenderSkeleton {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	ArrayList<LogListener> logSubscriberList = new ArrayList<>();

	public CaesiumLogAppender() {

	}

	public int addLogSubscriber(LogListener logListener) {
		this.logSubscriberList.add(logListener);
		return this.logSubscriberList.indexOf(logListener);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {

		// System.out.println(event.getLoggerName() + " | " + event.getMessage());
		String loggerNameString = event.getLoggerName();
		if (loggerNameString.contains("caesium") || loggerNameString.contains("root")) {
			String timestamp = sdf.format(new Date(event.getTimeStamp()));
			String logText = String.format("[%s]%s : %s", event.getLevel(), timestamp, event.getMessage());
			System.out.println(logText);
			for (LogListener logListener : this.logSubscriberList) {
				logListener.onLog(logText);
			}
		}

	}

}
