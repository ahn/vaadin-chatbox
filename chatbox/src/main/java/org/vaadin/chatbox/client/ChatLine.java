package org.vaadin.chatbox.client;

/**
 * One line of chat.
 * 
 * IMPORTANT: because of concurrent access, this class must be effectively immutable!
 * the setters are just for Vaadin transportation. (TODO: could do a separate class for that?)
 * 
 */
public class ChatLine {

	private final String text;
	private final ChatUser user;
	
	public ChatLine(String text) {
		this(text, null);
	}

	public ChatLine(String text, ChatUser user) {
		this.text = text;
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public ChatUser getUser() {
		return user;
	}

	@Override
	public String toString() {
		return (user == null ? "" : (user.getName() + ": ")) + text;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ChatLine) {
			ChatLine ocl = (ChatLine) other;
			return user==null ? ocl.user==null : user.equals(ocl.user) && ocl.text.equals(text);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return text.hashCode();
	}
}
