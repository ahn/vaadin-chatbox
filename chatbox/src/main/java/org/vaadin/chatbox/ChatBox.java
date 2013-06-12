package org.vaadin.chatbox;

import org.vaadin.chatbox.client.ChatBoxClientRpc;
import org.vaadin.chatbox.client.ChatBoxServerRpc;
import org.vaadin.chatbox.client.ChatBoxState;

import com.vaadin.shared.MouseEventDetails;

// This is the server-side UI component that provides public API 
// for ChatBox
public class ChatBox extends com.vaadin.ui.AbstractComponent {

	private int clickCount = 0;

	// To process events from the client, we implement ServerRpc
	private ChatBoxServerRpc rpc = new ChatBoxServerRpc() {

		// Event received from client - user clicked our widget
		public void clicked(MouseEventDetails mouseDetails) {
			
			// Send nag message every 5:th click with ClientRpc
			if (++clickCount % 5 == 0) {
				getRpcProxy(ChatBoxClientRpc.class)
						.alert("Ok, that's enough!");
			}
			
			// Update shared state. This state update is automatically 
			// sent to the client. 
			getState().text = "You have clicked " + clickCount + " times";
		}
	};

	public ChatBox() {

		// To receive events from the client, we register ServerRpc
		registerRpc(rpc);
	}

	// We must override getState() to cast the state to ChatBoxState
	@Override
	public ChatBoxState getState() {
		return (ChatBoxState) super.getState();
	}
}
