package se.jbee.doc;

import se.jbee.doc.tree.Element;

public interface DocumentBuilder {

	DocumentContext context();

	/**
	 * @param header an Element with no body yet
	 */
	void addElement(Element header);

	void addText(CharSequence text);

	void openElementBody();

	void closeElementBody();

	void openTextBody();

	void closeTextBody();

	//TODO remove body argument and add a openBody() and closeBody() method here instead
	// the builder needs to track the state of the added element in a node
	// so that it can do the appropriate open and close action once the method is
	// called by the scanner

	// also instead of returning whether or not a body is expected the builder
	// should track this and should the scanner try to open a body where it is not
	// expected the builder must throw an exception.

	// if the scanner does not call openBody() but continues with add() or
	// createPlain() instead the builder has to check if that means the body
	// is opened implicitly and from there track that it does close implicitly
	// as well should an element be added that cannot exit in the current implicitly
	// open parent element

	// this might be especally tricky as define and namespace elements do not
	// make part of the document itself but they still need to be tracked
	// properly as last element added to which a body might belong

}
