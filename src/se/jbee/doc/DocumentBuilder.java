package se.jbee.doc;

public interface DocumentBuilder {

	DocumentMetadata meta();

	/**
	 * Called for usual <code>\element</code> elements
	 * @param header an Element with no body yet
	 */
	void begin(Element header);

	/**
	 * Called for <code>\define!</code> elements
	 * @param header an Element with no body yet
	 */
	void beginRedefine(Element header);

	/**
	 * Called for <code>prototype*</code> born elements
	 * @param header an Element with no body yet
	 */
	void beginDerive(Element header);

	Element createPlain();
}
