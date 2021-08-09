package se.jbee.doc.tree;

import se.jbee.doc.Component;
import se.jbee.doc.DocumentContext;
import se.jbee.doc.Nature;

public final class Text extends Element {

	public Text(Define definedAs) {
		super(definedAs, Nature.text);
	}

	public Component component() {
		return value(Nature.component, Component.inline, Component.class, Component[]::new, Component::valueOf);
	}
}
