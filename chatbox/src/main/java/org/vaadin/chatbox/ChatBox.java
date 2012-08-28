package org.vaadin.chatbox;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vaadin.chatbox.gwt.shared.Chat;
import org.vaadin.chatbox.gwt.shared.ChatDiff;
import org.vaadin.chatbox.gwt.shared.ChatLine;
import org.vaadin.diffsync.AbstractDiffSyncComponent;
import org.vaadin.diffsync.Shared;
import org.vaadin.diffsync.gwt.shared.SendPolicy;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

@SuppressWarnings("serial")
@com.vaadin.ui.ClientWidget(org.vaadin.chatbox.gwt.client.VChatBox.class)
public class ChatBox extends AbstractDiffSyncComponent<Chat, ChatDiff> {

	public interface UserClickListener {
		public void userClicked(String userId);
	}
	
	public interface ItemClickListener {
		public void itemClicked(String itemId);
	}

	private LinkedList<UserClickListener> userListeners = new LinkedList<UserClickListener>();
	private LinkedList<ItemClickListener> itemListeners = new LinkedList<ItemClickListener>();

	public void addListener(UserClickListener cll) {
		userListeners.add(cll);
	}
	public void addListener(ItemClickListener cll) {
		itemListeners.add(cll);
	}

	private String userId;
	private String userName;
	private String userStyle;
	private boolean showMyNick = true;

	public ChatBox(Shared<Chat, ChatDiff> shared) {
		super(shared);
		setWidth("200px");
		setHeight("200px");
		setSendPolicy(SendPolicy.IMMEDIATELY);
	}

	public void setUser(String userId, String userName, String userStyle) {
		this.userId = userId;
		this.userName = userName;
		this.userStyle = userStyle;
		requestRepaint();
	}
	
	public void setShowMyNick(boolean show) {
		if (showMyNick != show) {
			showMyNick = show;
			requestRepaint();
		}
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		if (userId != null) {
			target.addAttribute("userid", userId);
			target.addAttribute("username", userName);
			if (userStyle != null) {
				target.addAttribute("userstyle", userStyle);
			}
		}
		target.addAttribute("listening", !userListeners.isEmpty() || !itemListeners.isEmpty());
		target.addAttribute("showmynick", showMyNick);
	}

	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);

		if (variables.containsKey("itemclicked")) {
			String itemId = (String) variables.get("itemclicked");
			for (ItemClickListener cll : itemListeners) {
				cll.itemClicked(itemId);
			}
		}
		if (variables.containsKey("userclicked")) {
			String userId = (String) variables.get("userclicked");
			for (UserClickListener cll : userListeners) {
				cll.userClicked(userId);
			}
		}
	}

	@Override
	protected Chat initialValue() {
		return Chat.EMPTY_CHAT;
	}

	@Override
	protected ChatDiff diff(Chat v1, Chat v2) {
		ChatDiff d = ChatDiff.diff(v1, v2);
		return d;
	}

	@Override
	protected void paintDiff(ChatDiff diff, PaintTarget target)
			throws PaintException {
		List<ChatLine> addedLines = diff.getAddedFrozen();
		String[] texts = new String[addedLines.size()];
		String[] userIds = new String[addedLines.size()];
		String[] userNames = new String[addedLines.size()];
		String[] userStyles = new String[addedLines.size()];
		int i = 0;
		for (ChatLine cl : addedLines) {
			texts[i] = cl.getText();
			userIds[i] = cl.getUserId();
			userNames[i] = cl.getUserName();
			userStyles[i] = cl.getUserStyle();
			i++;
		}
		target.addVariable(this, "added-texts", texts);
		target.addVariable(this, "added-userids", userIds);
		target.addVariable(this, "added-usernames", userNames);
		target.addVariable(this, "added-userstyles", userStyles);
		target.addVariable(this, "removed-live", diff.getFreezeLive());

	}

	@Override
	protected ChatDiff diffFromVariables(Map<String, Object> variables) {
		if (!variables.containsKey("added-texts")) {
			return ChatDiff.IDENTITY;
		}
		
		String[] texts = (String[]) variables.get("added-texts");
		LinkedList<ChatLine> lines = new LinkedList<ChatLine>();
		for (int i = 0; i < texts.length; ++i) {
			lines.add(new ChatLine(texts[i], userId, userName, userStyle));
		}
		ChatDiff d = new ChatDiff(null, lines, 0);
		return d;
	}

}
