package org.vaadin.chatbox.gwt.shared;

public class ChatLineBuilder {

	public static class Item {
		private final String text;
		private final String style;
		private final String itemId;

		public Item(String text, String style, String itemId) {
			this.text = text.replace(":", "%58").replace("{", "%123")
					.replace("}", "%125");
			this.style = style;
			this.itemId = itemId;
		}

		@Override
		public String toString() {
			return "{{" + text + ":" + style + ":" + itemId + "}}";
		}
	}

	private StringBuilder sb = new StringBuilder();

	public ChatLineBuilder append(String s) {
		sb.append(s);
		return this;
	}

	public ChatLineBuilder append(Item item) {
		sb.append("{{").append(item.text).append(":");
		if (item.style != null)
			sb.append(item.style);
		sb.append(":");
		if (item.itemId != null)
			sb.append(item.itemId);
		sb.append("}}");
		return this;
	}

	public ChatLine toLine() {
		return new ChatLine(sb.toString());
	}
}
