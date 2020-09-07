package se.jbee.doc;

import se.jbee.doc.tree.Element;

public interface DocumentBuilder {

	DocumentContext context();

	/**
	 * @param header an Element with no body yet
	 * @param body   the scanner to use for the body if the header is not
	 *               indicating a switch to another scanner implementation
	 */
	void add(Element header, Runnable body);

	Element createPlain();
}
