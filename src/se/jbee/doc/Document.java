package se.jbee.doc;

import se.jbee.doc.tree.Namespace;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes a textual document using a tree structure.
 * <p>
 * Besides the textual content of the document all metadata defined as part of
 * the document is also part of the data held by this class.
 * <p>
 * This class does not contain state required to build the document from text
 * sources. It can be seen as a structural semantic representation of the
 * textual content.
 */
public final class Document {

	private final Map<String, Namespace> namespaces = new HashMap<>();

	public Namespace ns(String name) {
		return namespaces.get(name);
	}

	public void add(Namespace ns) {
		namespaces.put(ns.name(), ns);
	}

	//TODO document structure in form of ElementNode tree
}
