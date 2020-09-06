package se.jbee.doc;

import java.util.Deque;
import java.util.LinkedList;

public class DocumentBuilderImpl implements DocumentBuilder {

	private final Document doc;

	/**
	 * Nesting from the current top level element down to the current inner most nested element.
	 *
	 * Each time a new element is added with unclear parent the hierarchy is used
	 * to determine which element becomes the new parent and the hierarchy is
	 * updated accordingly to end with the new parent element.
	 */
	private final Deque<Element> hierarchy = new LinkedList<>();

	public DocumentBuilderImpl(Document doc) {
		this.doc = doc;
	}

	@Override
	public DocumentMetadata meta() {
		return doc.ns();
	}

	@Override
	public void begin(Element header) {
		if (header.isOpeningNamespace()) {

		}
		if (header.isDefining()) {

		} else {
			//TODO add to doc body
		}
	}

	@Override
	public void beginRedefine(Element header) {

	}

	@Override
	public void beginDerive(Element header) {

	}

	@Override
	public Element createPlain() {
		return null;
	}
}
