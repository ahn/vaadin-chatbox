package org.vaadin.chatbox.client;

import com.vaadin.shared.communication.ServerRpc;

// ServerRpc is used to pass events from client to server
public interface ChatBoxServerRpc extends ServerRpc {
	public void lineAdded(ChatBoxState.Line line);
}
