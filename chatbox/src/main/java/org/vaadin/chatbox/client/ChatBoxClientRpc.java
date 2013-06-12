package org.vaadin.chatbox.client;

import java.util.List;

import com.vaadin.shared.communication.ClientRpc;

// ClientRpc is used to pass events from server to client
// For sending information about the changes to component state, use State instead
public interface ChatBoxClientRpc extends ClientRpc {
	public void addLines(List<ChatBoxState.Line> lines);
	public void focus();
}