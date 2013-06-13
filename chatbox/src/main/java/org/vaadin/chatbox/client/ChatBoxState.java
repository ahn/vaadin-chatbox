package org.vaadin.chatbox.client;

import java.io.Serializable;

import com.vaadin.shared.annotations.DelegateToWidget;

@SuppressWarnings("serial")
public class ChatBoxState extends com.vaadin.shared.AbstractComponentState {
	
	public static class User implements Serializable {
		public String id;
		public String name;
		public String style;
		public static User convert(ChatUser cu) {
			if (cu==null) {
				return null;
			}
			User u = new User();
			u.id = cu.getId();
			u.name = cu.getName();
			u.style = cu.getStyle();
			return u;
		}
		public static ChatUser convert(User u) {
			if (u==null) {
				return null;
			}
			return new ChatUser(u.id, u.name, u.style);
		}
	}
	
	public static class Line implements Serializable {
		public User user;
		public String text;
		public static Line convert(ChatLine cl) {
			Line li = new Line();
			li.user = User.convert(cl.getUser());
			li.text = cl.getText();
			return li;
		}
		public static ChatLine convert(Line li) {
			return new ChatLine(li.text, User.convert(li.user));
		}
	}
	
	@DelegateToWidget("setShowSendButton")
	public boolean showSendButton = true;
	
	@DelegateToWidget("setUser")
	public User user;

}