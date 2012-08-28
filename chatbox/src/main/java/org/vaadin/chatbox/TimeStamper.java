package org.vaadin.chatbox;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.vaadin.chatbox.gwt.shared.Chat;
import org.vaadin.chatbox.gwt.shared.ChatDiff;
import org.vaadin.diffsync.DiffCalculator;

public class TimeStamper implements DiffCalculator<Chat, ChatDiff> {
	
	private final static SimpleDateFormat longFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm z]");
	private final static SimpleDateFormat shortFormat = new SimpleDateFormat("[HH:mm]");
	
	private final long collId;
	private int latestMinute = -1;
	private int latestDay = -1;
	
	public TimeStamper(long collId) {
		this.collId = collId;
	}
	
	public boolean needsToRunAfter(ChatDiff diff, long byCollaboratorId) {
		return byCollaboratorId!=collId && !diff.isIdentity();
	}

	public ChatDiff calcDiff(Chat value) throws InterruptedException {
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
}