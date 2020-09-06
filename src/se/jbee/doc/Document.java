package se.jbee.doc;

import java.util.*;

public final class Document {

	private final Namespaces namespaces = new Namespaces();

	//TODO scanners are defined using scanner as an element with an attribute that binds a name to a class

	public Namespaces ns() {
		return namespaces;
	}

	//TODO document structure in form of ElementNode tree
}
