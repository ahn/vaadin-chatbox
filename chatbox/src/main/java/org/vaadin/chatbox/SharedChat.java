package org.vaadin.chatbox;


import org.vaadin.chatbox.gwt.shared.Chat;
import org.vaadin.chatbox.gwt.shared.ChatDiff;
import org.vaadin.chatbox.gwt.shared.ChatLine;
import org.vaadin.diffsync.DiffCalculator;
import org.vaadin.diffsync.Shared;

/**
 * A thread-safe chat.
 * 
 */
public class SharedChat extends Shared<Chat, ChatDiff> {
	
	public SharedChat() {
		this(Chat.EMPTY_CHAT);
	}
	
	public SharedChat(Chat value) {
		super(value);
//		addTask(new TimeStamper());
		addTask(new Freezer());
	}
	
	public void newLine(String text) {
		applyDiff(ChatDiff.newLiveLine(text));
	}
	
	public void newLine(ChatLine line) {
		applyDiff(ChatDiff.newLiveLine(line));
	}
	
	private static class Freezer implements DiffCalculator<Chat, ChatDiff> {
		public boolean needsToRunAfter(ChatDiff diff, long byCollaboratorId) {
			return true;
		}

		public ChatDiff calcDiff(Chat value) throws InterruptedException {
			return ChatDiff.freezeLive(value.getLiveLinesSize());
		}
	}

}
