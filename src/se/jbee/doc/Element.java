package se.jbee.doc;

import java.util.EnumMap;

/**
 * Represents an element of the {@link Nature#define} that defines new elements and attributes.
 */
public class Element {

	/**
	 * Like a "prototype" information for creating {@link Element} of a kind.
	 * This itself is also just an {@link Element}.
	 */
	public final Define definition;
	private final DocumentContext context;
	private final EnumMap<Nature, Attribute> attributes = new EnumMap<>(Nature.class);

	public Element(DocumentContext context, Define definition) {
		this.definition = definition == null ? (Define) this : definition;
		this.context = context;
	}

	public final void set(Define key, String[] values) {
		Attribute attr = new Attribute(context, key, values);
		for (Nature n : key.get(Nature.define).asNatures())
			set(n, attr);
	}

	final void set(Nature key, Attribute attr) {
		attributes.put(key, attr);
	}

	public final Attribute get(Nature nature) {
		return attributes.get(nature);
	}

	/*
		Methods used for any type of Element
	 */

	public final String name() {
		return get(Nature.alias).values[0];
	}

	/*
	  Methods used for text-Elements
	 */

	public boolean hasNature(Nature nature) {
		for (Nature n : get(Nature.define).asNatures())
			if (n == nature)
				return true;
		return false;
	}

	/*
		General Methods
	 */

	@Override
	public final String toString() {
		StringBuilder str = new StringBuilder();
		str.append('\\').append(definition);
		str.append('[');
		for (Nature n : Nature.values()) {
			Attribute attr = get(n);
			if (attr != null) {
				attr.toString(str);
				str.append(' ');
			}
		}
		if (str.charAt(str.length() -1) == ' ')
			str.setLength(str.length()-1);
		str.append(']');
		return str.toString();
	}

}
