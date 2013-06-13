package org.vaadin.chatbox.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ChatBoxWidget extends DockLayoutPanel {

	public interface TextInputListener {
		public void liveLineAdded(ChatLine line);
	}

	private LinkedList<TextInputListener> tiListeners = new LinkedList<TextInputListener>();

	private ArrayList<String> myHistory = new ArrayList<String>();
	private int myHistoryAt = 0;

	public void addTextInputListener(TextInputListener li) {
		tiListeners.add(li);
	}

	public interface ChatCLickListener {
		public void userClicked(String userId);

		public void itemClicked(String itemId);
	}

	private LinkedList<ChatCLickListener> ccListeners = new LinkedList<ChatCLickListener>();

	public void addListener(ChatCLickListener li) {
		ccListeners.add(li);
	}

	private static final String CLASSNAME = "ChatBox";

	private ScrollPanel chatPanel = new ScrollPanel();
	{
		chatPanel.setStylePrimaryName("ChatPanel");
	}
	private VerticalPanel vp = new VerticalPanel();
	{
		chatPanel.add(vp);
	}

	private FlexTable chatTable = new FlexTable();
	{
		chatTable.setStylePrimaryName("FrozenLines");
		vp.add(chatTable);
	}

	private FlexTable liveTable = new FlexTable();
	{
		liveTable.setStylePrimaryName("LiveLines");
		vp.add(liveTable);
	}

	private HorizontalPanel inputPanel = null;

	private InlineLabel nameLabel = new InlineLabel();
	{
		nameLabel.setWordWrap(false);
	}

	private TextBox chatInput = new TextBox();
	{
		// Up and down arrows to browse what I've written earlier.
		chatInput.addKeyDownHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				if (event.isUpArrow()) {
					if (myHistoryAt > 0) {
						String text = chatInput.getText();
						if (!text.isEmpty()) {
							if (myHistoryAt == myHistory.size()) {
								myHistory.add(text);
							} else {
								myHistory.set(myHistoryAt, text);
							}
						}

						String msg = myHistory.get(--myHistoryAt);
						chatInput.setText(msg);
						chatInput.setCursorPos(msg.length());
					}
					event.preventDefault();

				} else if (event.isDownArrow()) {
					if (myHistoryAt < myHistory.size() - 1) {
						myHistory.set(myHistoryAt, chatInput.getText());
						String msg = myHistory.get(++myHistoryAt);
						chatInput.setText(msg);
						chatInput.setCursorPos(msg.length());
					} else if (myHistoryAt == myHistory.size() - 1) {
						myHistory.set(myHistoryAt, chatInput.getText());
						chatInput.setText("");
						myHistoryAt = myHistory.size();
					} else if (myHistoryAt == myHistory.size()) {
						String msg = chatInput.getText();
						if (!msg.isEmpty()) {
							myHistory.add(msg);
							myHistoryAt = myHistory.size();
							chatInput.setText("");
						}
					}
					event.preventDefault();
				}
			}
		});

		chatInput.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					newChatLine();
					event.preventDefault();
				}
			}
		});
	}

	private Button sendButton = new Button("Send");

	private int numFrozen = 0;

	private int numLive = 0;

	private ChatUser user;

	{
		sendButton.setWidth("60px");
		sendButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				newChatLine();
			}
		});
	}

	private boolean newChatLine() {
		if (!chatInput.getText().isEmpty()) {
			String msg = chatInput.getText();
			chatInput.setText("");
			myHistory.add(msg);
			myHistoryAt = myHistory.size();
			addLiveLine(msg);
			return true;
		}
		return false;
	}

	private void addLiveLine(String msg) {
		ChatLine line = new ChatLine(msg, user);
		addLiveLine(line);

	}

	public void setShowSendButton(boolean show) {
		sendButton.setVisible(show);
	}

	public void setShowMyNick(boolean show) {
		nameLabel.setVisible(show);
	}

	private LinkedList<ChatLine> liveLines = new LinkedList<ChatLine>();
	private LinkedList<ChatLine> frozenLines = new LinkedList<ChatLine>();

	public ChatBoxWidget() {
		super(Style.Unit.PX);

		setStylePrimaryName(CLASSNAME);

		createInputPanel();
		this.add(chatPanel);

		setEnabled(false);
	}

	private void createInputPanel() {
		inputPanel = new HorizontalPanel();
		inputPanel.setWidth("100%");
		inputPanel.setHeight("100%");

		chatInput.setWidth("100%");

		SimplePanel spacer = new SimplePanel();
		spacer.setWidth("10px");

		inputPanel.add(nameLabel);
		inputPanel.add(chatInput);
		inputPanel.add(spacer);
		inputPanel.add(sendButton);

		inputPanel.setCellWidth(chatInput, "100%");

		inputPanel.setCellVerticalAlignment(nameLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		inputPanel.setCellVerticalAlignment(chatInput,
				HasVerticalAlignment.ALIGN_MIDDLE);
		inputPanel.setCellVerticalAlignment(sendButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		this.addSouth(inputPanel, 28);
	}

	private void fireLiveLineAdded(ChatLine line) {
		for (TextInputListener til : tiListeners) {
			til.liveLineAdded(line);
		}
	}

	public void addFrozenLine(ChatLine line) {
		chatTable.setWidget(numFrozen, 0, new ChatWidgetLine(line, this));
		frozenLines.add(line);
		scrollToBottom();
		++numFrozen;
	}

	private void addLiveLine(ChatLine line) {
		liveTable.setWidget(numLive, 0, new ChatWidgetLine(line, this));
		liveLines.add(line);
		scrollToBottom();
		++numLive;
		fireLiveLineAdded(line);
	}

	private void scrollToBottom() {
		// http://stackoverflow.com/questions/6484319/scrollpanel-scrolltobottom-not-working-as-expected
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				chatPanel.scrollToBottom();
			}
		});
	}

	public List<ChatLine> getLiveLines() {
		return new ArrayList<ChatLine>(liveLines);
	}

	public void clicked(String itemId) {
		for (ChatCLickListener ccl : ccListeners) {
			ccl.itemClicked(itemId);
		}
	}

	public void clickedUser(String userId) {
		for (ChatCLickListener ccl : ccListeners) {
			ccl.userClicked(userId);
		}
	}

	private void setEnabled(boolean enable) {
		inputPanel.setVisible(enable);
	}

	public void setUser(ChatBoxState.User u) {
		ChatUser user = ChatBoxState.User.convert(u);
		if (sameUser(this.user, user)) {
			return;
		}
		if (user == null) {
			setEnabled(false);
		} else {
			nameLabel.setText(user.getName() + ":");
			nameLabel.setStylePrimaryName(user.getStyle());
			setEnabled(true);
		}
		this.user = user;
		scrollToBottom(); // ?
	}

	private void freeze(int freezeLive) {
		// extra check for initial
		if (numLive == 0) {
			return;
		}

		for (int i = 0; i < freezeLive; ++i) {
			liveLines.remove(0);
			liveTable.removeRow(0);
			--numLive;
		}
	}

	private static boolean sameUser(ChatUser u1, ChatUser u2) {
		return u1 == null ? u2 == null : u1.equals(u2);
	}

	public void addFrozenLines(List<ChatBoxState.Line> lines) {
		int mine = 0;
		for (ChatBoxState.Line line : lines) {
			ChatLine li = ChatBoxState.Line.convert(line);

			// assuming all the live lines by the user are added on this
			// client... XXX
			if (user != null && user.equals(li.getUser())) {
				mine++;
			}
			frozenLines.add(li);
			chatTable.setWidget(numFrozen++, 0, new ChatWidgetLine(li, this));
		}
		freeze(mine);
		scrollToBottom();
	}

	public void focusToInputField() {
		chatInput.setFocus(true);
	}

}
