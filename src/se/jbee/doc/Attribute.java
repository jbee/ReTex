package se.jbee.doc;

import java.util.Arrays;

public final class Attribute {

	public final Define definition;
	final String[] values;
	private final DocumentContext context;
	private Nature[] valuesAsNature;
	private Define[] valuesAsDefinition;

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		toString(str);
		return str.toString();
	}

	public void toString(StringBuilder str) {
		str.append('\\').append(definition.name()).append(' ');
		if (values.length != 1) {
			str.append('[');
			for (int i = 0; i < values.length; i++) {
				if (i > 0)
					str.append(' ');
				str.append(values[i]);
			}
			str.append(']');
		} else {
			str.append(values[0]);
		}
	}

	Attribute(DocumentContext context, Define definition, String[] values) {
		this.definition = definition;
		this.values = values;
		this.context = context;
	}

	public Nature[] asNatures() {
		if (valuesAsNature == null)
			valuesAsNature = Arrays.stream(values).map(Nature::valueOf).toArray(Nature[]::new);
		return valuesAsNature;
	}

	public Define[] asDefinitions() {
		if (valuesAsDefinition == null)
			valuesAsDefinition = Arrays.stream(values).map(context::definitionFor).toArray(Define[]::new);
		return valuesAsDefinition;
	}

	public static boolean isValue(Attribute attr, Element other) {
		for (Element e : attr.asDefinitions())
			if (e == other)
				return true;
		return false;
	}

	public static boolean includes(Attribute attr, Nature nature) {
		return hasPattern(attr,"&"+nature.name());
	}

	public static boolean includes(Attribute attr, Element element) {
		for (Nature n : element.get(Nature.define).asNatures())
			if (includes(attr, n))
				return true;
		for (String alias : element.get(Nature.alias).values)
			if (hasPattern(attr, alias))
				return true;
		return includesIsA(attr, element);
	}

	private static boolean includesIsA(Attribute attr, Element element) {
		Attribute is_a = element.get(Nature.is_a);
		while (is_a != null) {
			for (Element e : is_a.asDefinitions())
				if (hasPattern(attr,"*" + e.get(Nature.alias)) || includesIsA(attr, e))
					return true;
		}
		return false;
	}

	private static boolean hasPattern(Attribute attr, String pattern) {
		for (String p : attr.values)
			if (p.equals(pattern))
				return true;
		return false;
	}
}
