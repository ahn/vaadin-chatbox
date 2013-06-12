package org.vaadin.chatbox.client;

/**
 *
 */
public class ChatUser {
	
	private static long latestUserId = 0;
	private static synchronized String getNewUserId() {
		return "user"+(++latestUserId);
	}
	
	public static ChatUser newUser(String name) {
		return newUser(name, "user1");
	}
	
	public static ChatUser newUser(String name, String style) {
		return new ChatUser(getNewUserId(), name, style);
	}

	private final String id;
	private final String name;
	private final String style;
	
	public ChatUser(String id, String name, String style) {
		this.id = id;
		this.name = name;
		this.style = style;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChatUser) {
			ChatUser ou = (ChatUser)obj;
			return id.equals(ou.id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return "{"+id+":"+name+":"+style+"}";
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getStyle() {
		return style;
	}
}
