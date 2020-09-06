package se.jbee.doc;

import java.util.*;

public final class Document {

	private final Namespaces namespaces = new Namespaces();

	public Namespaces ns() {
		return namespaces;
	}

	//TODO document structure in form of ElementNode tree
}
