package org.vaadin.chatbox.gwt.client;

import java.util.LinkedList;
import java.util.List;

import org.vaadin.chatbox.gwt.client.ChatWidget.ChatCLickListener;
import org.vaadin.chatbox.gwt.client.ChatWidget.TextInputListener;
import org.vaadin.chatbox.gwt.shared.Chat;
import org.vaadin.chatbox.gwt.shared.ChatDiff;
import org.vaadin.chatbox.gwt.shared.ChatLine;
import org.vaadin.diffsync.gwt.client.VAbstractDiffSyncComponent;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VChatBox extends VAbstractDiffSyncComponent<Chat, ChatDiff>
		implements TextInputListener, ChatCLickListener {

	private static final String CLASSNAME = "ChatBox";
	private ChatWidget chatWidget = new ChatWidget();
	private String userId;
	private String userName;
	private String userStyle;
	private boolean listeningClicks = false;
	
	private String width;
	private String height;

	public VChatBox() {
		super();
		initWidget(chatWidget);
		chatWidget.addListener((TextInputListener) this);
		setStylePrimaryName(CLASSNAME);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		super.updateFromUIDL(uidl, client);
		if (client.updateComponent(this, uidl, true)) {
			return;
		}

		if (uidl.hasAttribute("userid")) {
			userId = uidl.getStringAttribute("userid");
			userName = uidl.getStringAttribute("username");
			userStyle = uidl.getStringAttribute("userstyle");
			chatWidget.setUser(userName, userStyle);
		} else {
			userId = null;
			userName = null;
			userStyle = null;
		}

		chatWidget.setEnabled(userId != null);
		
		chatWidget.setShowMyNick(uidl.getBooleanAttribute("showmynick"));
		
		chatWidget.setShowSendButton(uidl.getBooleanAttribute("showsendbutton"));

		if (uidl.hasAttribute("listening")) {
			setListeningClicks(uidl.getBooleanAttribute("listening"));
		}
	}

	private void setListeningClicks(boolean listening) {
		if (this.listeningClicks == listening) {
			return;
		}
		if (listening) {
			chatWidget.addListener((ChatCLickListener) this);
		}
		this.listeningClicks = listening;
	}

	@Override
	protected ChatDiff diff(Chat v1, Chat v2) {
		ChatDiff d = ChatDiff.diff(v1, v2);
		return d;
	}

	@Override
	protected void diffToClient(ChatDiff diff, ApplicationConnection client,
			String paintableId, boolean immediate) {
		List<ChatLine> addedLines = diff.getAddedLive();
		String[] texts = new String[addedLines.size()];
		int i = 0;
		for (ChatLine cl : addedLines) {
			texts[i] = cl.getText();
			i++;
		}

		client.updateVariable(paintableId, "added-texts", texts, immediate);
	}

	@Override
	protected ChatDiff diffFromUIDL(UIDL uidl) {
		if (!uidl.hasVariable("added-texts")) {
			return ChatDiff.IDENTITY;
		}
		String[] texts = uidl.getStringArrayVariable("added-texts");
		String[] userIds = uidl.getStringArrayVariable("added-userids");
		String[] userNames = uidl.getStringArrayVariable("added-usernames");
		String[] userStyles = uidl.getStringArrayVariable("added-userstyles");
		int removedLive = uidl.getIntVariable("removed-live");

		LinkedList<ChatLine> lines = new LinkedList<ChatLine>();
		for (int i = 0; i < texts.length; ++i) {
			lines.add(new ChatLine(texts[i], userIds[i].isEmpty() ? null
					: userIds[i], userNames[i].isEmpty() ? null : userNames[i],
					userStyles[i].isEmpty() ? null : userStyles[i]));
		}
		return new ChatDiff(lines, null, removedLive);
	}

	@Override
	protected Chat getValue() {
		Chat c = new Chat(chatWidget.getFrozenLines(), chatWidget.getLiveLines());
		return c;
	}

	@Override
	protected void applyDiff(ChatDiff diff) {
		chatWidget.freeze(diff.getFreezeLive());
		for (ChatLine li : diff.getAddedFrozen()) {
			chatWidget.addFrozenLine(li);
		}
	}
	
	@Override
	public void setWidth(String w) {
		super.setWidth(w);
		if (!width.equals(w)) {
			// -2 because of 1px border
			chatWidget.setWidth((getOffsetWidth()-2)+"px");
			width = w;
		}
	}
	
	@Override
	public void setHeight(String h) {
		super.setHeight(h);
		if (!height.equals(h)) {
			// -2 because of 1px border
			chatWidget.setHeight((getOffsetHeight()-2)+"px");
			height = h;
		}
	}
	

	public void textInput(String text) {
		ChatLine li = new ChatLine(text, userId, userName, userStyle);
		chatWidget.addLiveLine(li);
		valueChanged();
	}

	public void userClicked(String userId) {
		getClient().updateVariable(getPaintableId(), "userclicked", userId,
				false);
		valueChangedSendEvenIfIdentityASAP();
	}

	public void itemClicked(String itemId) {
		getClient().updateVariable(getPaintableId(), "itemclicked", itemId,
				false);
		valueChangedSendEvenIfIdentityASAP();
	}

}
