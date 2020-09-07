package se.jbee.doc;

import se.jbee.doc.tree.Define;

@FunctionalInterface
public interface DocumentContext {

	Define getDefinition(String alias);

	default Define operation() {
		return getDefinition("system:op");
	}
}
