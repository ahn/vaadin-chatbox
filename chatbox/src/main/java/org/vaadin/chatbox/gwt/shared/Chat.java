package org.vaadin.chatbox.gwt.shared;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Immutable chat.
 * 
 * Contains two kinds of {@link ChatLine}s: frozen (retain their position permanently)
 * and live (may still change positions).
 * 
 */
public class Chat implements Iterable<ChatLine> {

	public static final Chat EMPTY_CHAT = new Chat();

	private final List<ChatLine> frozenLines;
	private final List<ChatLine> liveLines;
	
	private Chat() {
		this.frozenLines = Collections.emptyList();
		this.liveLines = Collections.emptyList();
	}
	
	
	public Chat(List<ChatLine> frozenLines, List<ChatLine> liveLines) {
		if (frozenLines==null) {
			this.frozenLines = Collections.emptyList();
		}
		else {
			this.frozenLines = frozenLines;
		}
		
		if (liveLines==null) {
			this.liveLines = Collections.emptyList();
		}
		else {
			this.liveLines = liveLines;
		}
	}
	
	public Chat(List<ChatLine> frozenLines) {
		this(frozenLines, null);
	}
	
	public List<ChatLine> getFrozenLines() {
		return Collections.unmodifiableList(frozenLines);
	}
	
	public int getFrozenLinesSize() {
		return frozenLines.size();
	}
	
	public List<ChatLine> getLiveLines() {
		return Collections.unmodifiableList(liveLines);
	}
	
	public int getLiveLinesSize() {
		return liveLines.size();
	}
	
	@Override
	public String toString() {
		return "CHAT: " + frozenLines + ">>>" + liveLines;
	}

	public ChatLine getFrozenLine(int i) {
		return frozenLines.get(i);
	}

	public ChatLine getLiveLine(int i) {
		return liveLines.get(i);
	}


	private Ite ite;

	public Iterator<ChatLine> iterator() {
		if (ite == null) {
			ite = new Ite();
		}
		return ite;
	}

	private class Ite implements Iterator<ChatLine> {
		final int numFrozen;
		final int numLive;
		int at = 0;

		private Ite() {
			numFrozen = frozenLines.size();
			numLive = liveLines.size();
		}

		public boolean hasNext() {
			return at < numFrozen + numLive;
		}

		public ChatLine next() {
			ChatLine line = at < numFrozen ? frozenLines.get(at) : liveLines
					.get(at - numFrozen);
			at++;
			return line;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
	
}
