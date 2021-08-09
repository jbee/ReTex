package se.jbee.doc.tree;

import se.jbee.doc.DocumentContext;
import se.jbee.doc.Nature;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link Namespace} {@link Element}s are special in that for each new namespace
 * both a {@link Define} and a {@link Namespace} instance is created.
 */
public final class Namespace extends Element {

	private final Map<String, Define> definitions = new LinkedHashMap<>();

	public Namespace(Define definedAs) {
		super(definedAs, Nature.ns);
	}

	public Namespace add(Define definition) {
		for (String alias : definition.alias())
			definitions.put(alias, definition);
		return this;
	}

	public Define definition(String alias) {
		return definitions.get(alias);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append('\n');
		str.append(super.toString());
		str.append('\n');
		for (Define e : definitions.values())
			str.append("# ").append(e).append('\n');
		return str.toString();
	}
}
