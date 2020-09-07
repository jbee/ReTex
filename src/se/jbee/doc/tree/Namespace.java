package se.jbee.doc.tree;

import se.jbee.doc.DocumentContext;
import se.jbee.doc.Nature;

import java.util.HashMap;
import java.util.Map;

public final class Namespace extends Element implements DocumentContext {

	private final Map<String, Define> definitions = new HashMap<>();

	public Namespace(Define definition) {
		super(definition, Nature.ns);
	}

	public Namespace add(Define definition) {
		for (String alias : definition.alias())
			definitions.put(alias, definition);
		return this;
	}

	@Override
	public Define getDefinition(String alias) {
		return definitions.get(alias);
	}

}
