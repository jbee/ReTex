package se.jbee.doc;

import se.jbee.doc.tree.Define;
import se.jbee.doc.tree.Namespace;

@FunctionalInterface
public interface DocumentContext {

	/**
	 * The namespace where all namespaces are defined.
	 * They use their own ns to avoid name collisions with other definitions.
	 */
	String NS_NS_ALIAS = "_ns_";

	Define definition(String alias);

	default Define definition(String ns, String alias) {
		return definition(ns + ':' + alias);
	}

	default Define operation() {
		return definition("system:op");
	}
}
