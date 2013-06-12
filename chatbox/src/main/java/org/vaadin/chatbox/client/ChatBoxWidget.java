package org.vaadin.chatbox.client;

import com.google.gwt.user.client.ui.Label;

// Extend any GWT Widget
public class ChatBoxWidget extends Label {

	public ChatBoxWidget() {

		// CSS class-name should not be v- prefixed
		setStyleName("chatbox");

		// State is set to widget in ChatBoxConnector		
	}

}