package org.vaadin.chatbox.gwt.shared;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.diffsync.gwt.shared.Diff;

/**
 * Immutable chat diff.
 * 
 * Adds live lines, adds frozen lives, and/or freezes live lines.
 * 
 */
public class ChatDiff implements Diff<Chat> {

	public static final ChatDiff IDENTITY = new ChatDiff(null, null, 0);

	private final List<ChatLine> addedLive;
	private final List<ChatLine> addedFrozen;
	private final int freezeLive;
	
	public static ChatDiff newLiveLine(ChatLine line) {
		return new ChatDiff(Collections.singletonList(line), null, 0);
	}

	public static ChatDiff newLiveLine(String line) {
		return newLiveLine(new ChatLine(line));
	}

	public static ChatDiff freezeLive(int freezeLive) {
		return new ChatDiff(null, null, freezeLive);
	}

	public ChatDiff(List<ChatLine> frozen, List<ChatLine> live) {
		this(frozen, live, 0);
	}
	
	public ChatDiff(List<ChatLine> frozen, List<ChatLine> live, int freezeLive) {
		if (frozen == null) {
			this.addedFrozen = Collections.emptyList();
		} else {
			this.addedFrozen = frozen;
		}
		if (live == null) {
			this.addedLive = Collections.emptyList();
		} else {
			this.addedLive = live;
		}
		this.freezeLive = freezeLive;
	}
	
	public List<ChatLine> getAddedLive() {
		return Collections.unmodifiableList(addedLive);
	}
	
	public List<ChatLine> getAddedFrozen() {
		return Collections.unmodifiableList(addedFrozen);
	}

	public Chat applyTo(Chat value) {
		List<ChatLine> newFrozen;
		if (!addedFrozen.isEmpty() || freezeLive>0) {
			newFrozen = new LinkedList<ChatLine>(value.getFrozenLines());
			newFrozen.addAll(addedFrozen);
		} else {
			newFrozen = value.getFrozenLines();
		}
		
		List<ChatLine> newLive;
		if (!addedLive.isEmpty() || freezeLive>0) {
			newLive = new LinkedList<ChatLine>(value.getLiveLines());
			newLive.addAll(addedLive);
		} else {
			newLive = value.getLiveLines();
		}
		
		if (freezeLive>0) {
			List<ChatLine> toBeFrozen = newLive.subList(0, Math.min(newLive.size(), freezeLive));
			for (ChatLine li : toBeFrozen) {
				newFrozen.add(li);
			}
			newLive = newLive.subList(toBeFrozen.size(), newLive.size());
		}

		return new Chat(newFrozen, newLive, null);
	}

	public boolean isIdentity() {
		return addedFrozen.isEmpty() && addedLive.isEmpty() && freezeLive==0;
	}

	public static ChatDiff diff(Chat v1, Chat v2) {
		LinkedList<ChatLine> addedFrozen;
		int fs1 = v1.getFrozenLinesSize();
		int fs2 = v2.getFrozenLinesSize();
		if (fs1 == fs2) {
			addedFrozen = null;
		} else {
			addedFrozen = new LinkedList<ChatLine>();
			for (int i = fs1; i < fs2; ++i) {
				addedFrozen.add(v2.getFrozenLine(i));
			}
		}

		LinkedList<ChatLine> addedLive;
		int ls1 = v1.getLiveLinesSize();
		int ls2 = v2.getLiveLinesSize();
		
		int freezeLive = ls1>ls2 ? (ls1-ls2) : 0;
		
		if (ls1>=ls2) {
			addedLive = null;
		} else {
			addedLive = new LinkedList<ChatLine>();
			for (int i = ls1; i < ls2; ++i) {
				addedLive.add(v2.getLiveLine(i));
			}
		}

		return new ChatDiff(addedFrozen, addedLive, freezeLive);
	}

	@Override
	public String toString() {
		return "ChatDiff " + addedFrozen + " +++ " + addedLive + " | "
				+ (freezeLive);
	}

	public int getFreezeLive() {
		return freezeLive;
	}

}
