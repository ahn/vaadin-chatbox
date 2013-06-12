package org.vaadin.chatbox.client;

import java.util.List;

import org.vaadin.chatbox.ChatBox;
import org.vaadin.chatbox.client.ChatBoxWidget.TextInputListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@SuppressWarnings("serial")
@Connect(ChatBox.class)
public class ChatBoxConnector extends AbstractComponentConnector implements TextInputListener {

	// ServerRpc is used to send events to server. Communication implementation
	// is automatically created here
	private ChatBoxServerRpc rpc = RpcProxy.create(ChatBoxServerRpc.class, this);
	private ChatBoxWidget widget;

	public ChatBoxConnector() {
		
		// To receive RPC events from server, we register ClientRpc implementation 
		registerRpc(ChatBoxClientRpc.class, new ChatBoxClientRpc() {
			@Override
			public void addLines(List<ChatBoxState.Line> lines) {
				getWidget().addFrozenLines(lines);
			}

			@Override
			public void focus() {
				getWidget().focusToInputField();
			}
		});
		
	}

	// We must implement createWidget() to create correct type of widget
	@Override
	protected Widget createWidget() {
		widget = GWT.create(ChatBoxWidget.class);
		widget.addTextInputListener(this);
		return widget;
	}

	
	// We must implement getWidget() to cast to correct type
	@Override
	public ChatBoxWidget getWidget() {
		return (ChatBoxWidget) super.getWidget();
	}

	// We must implement getState() to cast to correct type
	@Override
	public ChatBoxState getState() {
		return (ChatBoxState) super.getState();
	}

	// Whenever the state changes in the server-side, this method is called
	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
	}

	@Override
	public void liveLineAdded(ChatLine line) {
		rpc.lineAdded(ChatBoxState.Line.convert(line));
	}

}
