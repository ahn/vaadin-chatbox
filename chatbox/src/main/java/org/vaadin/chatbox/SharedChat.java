package org.vaadin.chatbox;


import org.vaadin.chatbox.gwt.shared.Chat;
import org.vaadin.chatbox.gwt.shared.ChatDiff;
import org.vaadin.chatbox.gwt.shared.ChatLine;
import org.vaadin.diffsync.DiffTask;
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
	
	private static class Freezer implements DiffTask<Chat, ChatDiff> {
		public ChatDiff exec(Chat value, ChatDiff diff, long collaboratorId) {
			return ChatDiff.freezeLive(value.getLiveLinesSize());
		}

		public boolean needsToExec(Chat value, ChatDiff diff,
				long collaboratorId) {
			return !diff.getAddedLive().isEmpty();
		}
	}

}
