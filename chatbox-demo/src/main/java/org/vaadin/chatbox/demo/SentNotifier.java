package org.vaadin.chatbox.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.vaadin.chatbox.SharedChat;
import org.vaadin.chatbox.SharedChat.ChatListener;
import org.vaadin.chatbox.client.ChatLine;

public class SentNotifier implements ChatListener {
	private final static SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
	private final Timer timer = new Timer();
	private final SharedChat chat;
	private final long delay;
	private int n = 0;

	public SentNotifier(SharedChat chat, long delayMs) {
		this.chat = chat;
		this.delay = delayMs;
	}
	
	synchronized private int incr() {
		return ++n;
	}
	
	synchronized private int decr() {
		return --n;
	}

	@Override
	public void lineAdded(ChatLine line) {
		if (line.getUser()==null) {
			return;
		}
		
		incr();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (decr()==0) {
					String date = longFormat.format(new Date(new Date().getTime()-delay));
					chat.addLine("Sent on " + date+"");
				}
			}}, delay);
	}
}
