package org.vaadin.chatbox;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.vaadin.chatbox.gwt.shared.Chat;
import org.vaadin.chatbox.gwt.shared.ChatDiff;
import org.vaadin.diffsync.DiffTask;

public class TimeStamper implements DiffTask<Chat, ChatDiff> {
	
	private final static SimpleDateFormat longFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm z]");
	private final static SimpleDateFormat shortFormat = new SimpleDateFormat("[HH:mm]");
	
	private int latestMinute = -1;
	private int latestDay = -1;
	
	public TimeStamper() {
	}

	public ChatDiff exec(Chat value, ChatDiff diff, long collaboratorId) {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int minute = cal.get(Calendar.MINUTE);
		if (latestDay!=day) {
			latestDay = day;
			latestMinute = minute;
			return ChatDiff.newLiveLine(longFormat.format(cal.getTime()));
		}
		else if (latestMinute!=minute) {
			latestMinute = minute;
			return ChatDiff.newLiveLine(shortFormat.format(cal.getTime()));
		}
		return null;
	}

	public boolean needsToExec(Chat value, ChatDiff diff, long collaboratorId) {
		return true;
	}
}