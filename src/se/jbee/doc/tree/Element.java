package se.jbee.doc.tree;

import se.jbee.doc.DocumentContext;
import se.jbee.doc.Nature;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Represents an element of the {@link Nature#define} that defines new elements
 * and attributes.
 */
public class Element implements Defined {

	protected final DocumentContext context;
	/**
	 * Like a "prototype" information for creating {@link Element} of a kind.
	 * This itself is also just an {@link Element}.
	 */
	private final Define definedAs;
	private final EnumMap<Nature, Attribute> attributes = new EnumMap<>(Nature.class);
	private final Map<String, Attribute> styles = new HashMap<>();

	public Element(Define definedAs) {
		this(definedAs.context, definedAs, null);
	}

	public Element(Define definedAs, Nature expected) {
		this(definedAs.context, definedAs, expected);
	}

	public Element(DocumentContext context, Define definedAs, Nature expected) {
		this.definedAs = definedAs == null ? (Define) this : definedAs;
		this.context = context;
		if (definedAs != null && expected != null
				&& definedAs.has(Nature.define) && !definedAs.isOf(expected))
			throw new IllegalArgumentException("Must be of nature " + expected+" but was: " + this.definedAs.nature());
	}

	@Override
	public Define definedAs() {
		return definedAs;
	}

	public final void add(Define key, String... values) {
		if (key.isOf(Nature.style)) {
			styles.put(key.name(), new Attribute(key, values));
		} else {
			add(key.nature(), new Attribute(key, values));
		}
	}

	public final <T> T value(Nature nature, Class<T> as, IntFunction<T[]> create, Function<String, T> map) {
		return value(nature, null, as, create, map);
	}

	public final <T> T value(Nature nature, T defaultValue, Class<T> as, IntFunction<T[]> create, Function<String, T> map) {
		Attribute attr = get(nature);
		if (attr == null || attr.values.length == 0) return defaultValue;
		return attr.values(as, create, map)[0];
	}

	public final Attribute get(Nature nature) {
		return attributes.get(nature);
	}

	public boolean has(Nature nature) {
		return nature == Nature.style ? !styles.isEmpty() : attributes.get(nature) != null;
	}

	public boolean hasStyle(String name) {
		return styles.containsKey(name);
	}

	public final <T> T[] values(Nature nature, Class<T> as, IntFunction<T[]> create, Function<String, T> map) {
		Attribute attr = get(nature);
		if (attr == null) return create.apply(0);
		return attr.values(as, create, map);
	}

	public final String[] values(Nature nature) {
		Attribute attr = get(nature);
		return attr == null ? new String[0] : attr.values;
	}

	/*
		Methods used for any type of Element
	 */

	public final Nature nature() {
		return value(Nature.define, Nature.class, Nature[]::new, Nature::valueOf);
	}

	public final Define[] ref() {
		return valuesAsDefine(Nature.ref);
	}

	/**
	 * @return By convention the first argument of the {@link #control()} nature
	 * refers to the implementation class if such a reference is needed.
	 */
	public final String implementation() {
		String[] control = control();
		return control.length == 0 ? null : control[0];
	}

	public final String[] control() {
		return values(Nature.control);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append('\\').append(name());
		if (!attributes.isEmpty()) {
			str.append('[');
			for (Nature n : Nature.values()) {
				Attribute attr = get(n);
				if (attr != null) {
					attr.toString(str);
					str.append(' ');
				}
			}
			if (str.charAt(str.length() - 1) == ' ') str.setLength(str.length() - 1);
			str.append(']');
		}
		return str.toString();
	}

	protected final Define[] valuesAsDefine(Nature nature) {
		return values(nature, Define.class, Define[]::new, context::definition);
	}

	protected final Define valueAsDefine(Nature nature) {
		return value(nature, Define.class, Define[]::new, context::definition);
	}

	final void add(Nature key, Attribute attr) {
		attributes.put(key, attr);
		references(attr.definedAs());
	}

	protected void references(Define key) {
		key.lock();
	}

	/*
	  Methods used for text-Elements
	 */

	/*
		General Methods
	 */

}
