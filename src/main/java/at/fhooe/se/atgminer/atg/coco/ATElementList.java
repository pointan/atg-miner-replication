package at.fhooe.se.atgminer.atg.coco;

import java.util.ArrayList;

import at.fhooe.se.atgminer.atg.ATElement;

/**
 * A list of non-null grammar elements.
 */
class ATElementList extends ArrayList<ATElement> {

	ATElementList() {
	}

	@Override
	public boolean add(at.fhooe.se.atgminer.atg.ATElement e) {
		if (e != null) {
			return super.add(e);
		} else {
			return false;
		}
	}

	ATElement first() {
		if (isEmpty()) {
			throw new RuntimeException("First of empty list is not allowed;");
		} else {
			return get(0);
		}
	}

	ATElement[] all() {
		var a = new ATElement[size()];
		return toArray(a);
	}
}
