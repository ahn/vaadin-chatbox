package org.vaadin.chatbox.gwt.shared;

/**
 * One line of chat.
 * 
 */
public class ChatLine {

	private final String text;
	private final String userId;
	private final String userName;
	private final String userStyle;

	public ChatLine(String text, String userId, String userName,
			String userStyle) {
		this.text = text;
		this.userId = userId;
		this.userName = userName;
		this.userStyle = userStyle;
	}

	public ChatLine(String text) {
		this(text, null, null, null);
	}

	public String getText() {
		return text;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserStyle() {
		return userStyle;
	}

	@Override
	public String toString() {
		return (userId == null ? "" : (userName + ": ")) + text;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ChatLine) {
			ChatLine ocl = (ChatLine) other;
			return ocl.userId.equals(userId) && ocl.userName.equals(userName)
					&& ocl.userStyle.equals(userStyle) && ocl.text.equals(text);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return text.hashCode();
	}
}
