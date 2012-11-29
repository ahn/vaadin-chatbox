package org.vaadin.chatbox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.vaadin.chatbox.gwt.shared.Chat;
import org.vaadin.chatbox.gwt.shared.ChatDiff;
import org.vaadin.diffsync.Shared.Listener;

public class SentNotifier implements Listener<Chat, ChatDiff> {
	private final static SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
	private final Timer timer = new Timer();
	private final SharedChat chat;
	private final long delay;
	private int n = 0;

	public SentNotifier(SharedChat chat, long delayMs) {
		this.chat = chat;
		this.delay = delayMs;
	}
	
	public void changed(Chat newValue, ChatDiff diff, long collaboratorId) {
		if (collaboratorId==SharedChat.NO_COLLABORATOR_ID) {
			return;
		}
		incr();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (decr()==0) {
					String date = longFormat.format(new Date(new Date().getTime()-delay));
					chat.newLine("Sent on " + date+"");
				}
			}}, delay);
	}
	
	synchronized private int incr() {
		return ++n;
	}
	
	synchronized private int decr() {
		return --n;
	}
}
