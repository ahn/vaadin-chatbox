package org.vaadin.chatbox;

import org.vaadin.chatbox.ChatBox.UserClickListener;
import org.vaadin.chatbox.gwt.shared.Chat;
import org.vaadin.chatbox.gwt.shared.ChatLine;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

// NOTE: To update changes from server to client, we should
// use some addon like Refresher or DontPush OzoneLayer.

@SuppressWarnings("serial")
public class ChatBoxDemo extends Application {

	// A static variable so that everybody gets the same instance.
	private static SharedChat sharedChat = new SharedChat();

	static {
		sharedChat.newLine("Welcome to chat!");
		sharedChat.newLine("This chat component is available at http://vaadin.com/addon/chatbox");
		sharedChat.addListener(new SentNotifier(sharedChat, 60*1000));
	}
	
	@Override
	public void init() {
		setMainWindow(new ChatBoxDemoWindow());		
	}
	
	// Subclassing Window and overriding getWindow to make multiple tabs work.

	private class ChatBoxDemoWindow extends Window {
		private ChatBox chatBox;
		ChatBoxDemoWindow() {
			super();
			getContent().setSizeFull();
			VerticalLayout la = new VerticalLayout();
			la.setSizeFull();
			addComponent(la);
			
			chatBox = new ChatBox(sharedChat);
			chatBox.setSizeFull();
			
			chatBox.setMaxNumLinesToLoad(1000);
			
			final TextField tf = new TextField("Nick:");
			final Button b = new Button("Join Chat");
			b.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					String name = (String)tf.getValue();
					if (!name.isEmpty()) {
						// Set the user for the ChatBox to be able to chat.
						chatBox.setUser(name, name, "user1");
					}
				}
			});
			la.addComponent(tf);
			la.addComponent(b);
			
			la.addComponent(chatBox);
			la.setExpandRatio(chatBox, 1);
			
			chatBox.addListener(new UserClickListener() {
				public void userClicked(String userId) {
					System.out.println("Clicked user "+userId + "!");
					Chat ch = sharedChat.getValue();
					for (ChatLine cl : ch) {
						System.out.println(cl);
					}
				}
			});
		}
	}
	
	@Override
	public Window getWindow(String name) {
		Window w = super.getWindow(name);
		if (w == null) {
			w = new ChatBoxDemoWindow();
			w.setName(name);
			addWindow(w);
			w.open(new ExternalResource(w.getURL()));
		}
		return w;
	}
}
