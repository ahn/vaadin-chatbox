package org.vaadin.chatbox.gwt.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.chatbox.gwt.shared.ChatLine;

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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ChatWidget extends DockLayoutPanel {

	public interface TextInputListener {
		public void textInput(String text);
	}

	private LinkedList<TextInputListener> tiListeners = new LinkedList<TextInputListener>();
	
	private ArrayList<String> myHistory = new ArrayList<String>();
	private int myHistoryAt = 0;

	public void addListener(TextInputListener li) {
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

	private static final String CLASSNAME = "ChatWidget";

	private ScrollPanel chatPanel = new ScrollPanel();
	{
		chatPanel.setStylePrimaryName("ChatPanel");
	}
	private VerticalPanel vp = new VerticalPanel();
	{
		chatPanel.add(vp);
	}
	FlexTable chatTable = new FlexTable();
	{
		chatTable.setStylePrimaryName("FrozenLines");
		vp.add(chatTable);
	}
	
	FlexTable liveTable = new FlexTable();
	{
		liveTable.setStylePrimaryName("LiveLines");
		vp.add(liveTable);
	}

	private FlowPanel inputPanel = new FlowPanel();
	{
		inputPanel.setWidth("100%");
	}
	
	private InlineLabel nameLabel = new InlineLabel();
	{
		inputPanel.add(nameLabel);
	}

	private TextBox chatInput = new TextBox();
	{
		chatInput.setWidth("100%");
		inputPanel.add(chatInput);
		
		// Up and down arrows to browse what I've written earlier.
		chatInput.addKeyDownHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				if (event.isUpArrow()) {
					if (myHistoryAt > 0) {
						String text = chatInput.getText();
						if (!text.isEmpty()) {
							if (myHistoryAt == myHistory.size()) {
								myHistory.add(text);
							}
							else {
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
	{
		sendButton.setWidth("60px");
		inputPanel.add(sendButton);
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
			textInputted(msg);
			return true;
		}
		return false;
	}

	@Override
	public void setWidth(String width) {
		super.setWidth(width);
		refreshWidth();
	}

	private LinkedList<ChatLine> liveLines = new LinkedList<ChatLine>();
	private LinkedList<ChatLine> frozenLines = new LinkedList<ChatLine>();

	public ChatWidget() {
		super(Style.Unit.PX);

		setStylePrimaryName(CLASSNAME);
		
		
		this.addSouth(inputPanel, 28);
		this.add(chatPanel);
	}

	private void textInputted(String msg) {
		for (TextInputListener til : tiListeners) {
			til.textInput(msg);
		}
	}

	public void addFrozenLine(ChatLine line) {
		chatTable.setWidget(numFrozen, 0, new ChatWidgetLine(line, this));
		frozenLines.add(line);
		chatPanel.scrollToBottom();
		++numFrozen;
	}
	
	public void addLiveLine(ChatLine line) {
		liveTable.setWidget(numLive, 0, new ChatWidgetLine(line, this));
		liveLines.add(line);
		chatPanel.scrollToBottom();
		++numLive;
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

	public void setEnabled(boolean enabled) {
		chatInput.setEnabled(enabled);
	}

	public void setUser(String userName, String userStyle) {
		nameLabel.setText(userName+":");
		nameLabel.setStylePrimaryName(userStyle);
		refreshWidth();
	}

	private void refreshWidth() {
		chatInput.setWidth((getOffsetWidth()-nameLabel.getOffsetWidth()-75) + "px");
	}

	public void removeLiveLines(int removedLive) {
		for (int i=0; i<removedLive; ++i) {
			liveLines.remove(0);
			liveTable.removeRow(0);
			--numLive;
		}
	}

	public List<ChatLine> getFrozenLines() {
		return Collections.unmodifiableList(frozenLines);
	}

	public void freeze(int freezeLive) {
		for (int i=0; i<freezeLive; ++i) {
			liveLines.remove(0);
			liveTable.removeRow(0);
			--numLive;
		}
	}

}
