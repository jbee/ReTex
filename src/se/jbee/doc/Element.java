package se.jbee.doc;

import java.util.EnumMap;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Represents an element of the {@link Nature#define} that defines new elements and attributes.
 */
public class Element implements Defined {

	/**
	 * Like a "prototype" information for creating {@link Element} of a kind.
	 * This itself is also just an {@link Element}.
	 */
	private final Define definition;
	protected final DocumentContext context;
	private final EnumMap<Nature, Attribute> attributes = new EnumMap<>(Nature.class);

	public Element(DocumentContext context, Define definition) {
		this.definition = definition == null ? (Define) this : definition;
		this.context = context;
	}

	@Override
	public Define definition() {
		return definition;
	}

	public final void add(Define key, String[] values) {
		Attribute attr = new Attribute(key, values);
		for (Nature n : key.natures())
			add(n, attr);
	}

	final void add(Nature key, Attribute attr) {
		attributes.put(key, attr);
	}

	public final Attribute get(Nature nature) {
		return attributes.get(nature);
	}

	public <T> T[] values(Nature nature, Class<T> as, IntFunction<T[]> create, Function<String, T> map) {
		Attribute attr = get(nature);
		if (attr == null)
			return create.apply(0);
		return attr.values(as, create, map);
	}

	public <T> T value(Nature nature, Class<T> as, IntFunction<T[]> create, Function<String, T> map) {
		Attribute attr = get(nature);
		if (attr == null || attr.values.length == 0)
			return null;
		return attr.values(as, create, map)[0];
	}

	private Define resolve(String alias) {
		return context.definitionFor(alias);
	}

	protected final Define[] valuesAsDefine(Nature nature) {
		return values(nature, Define.class, Define[]::new, this::resolve);
	}

	protected final Define valueAsDefine(Nature nature) {
		return value(nature, Define.class, Define[]::new, this::resolve);
	}


	/*
		Methods used for any type of Element
	 */

	public Nature[] natures() {
		return values(Nature.define, Nature.class, Nature[]::new, Nature::valueOf);
	}

	public Define[] ref() {
		return valuesAsDefine(Nature.ref);
	}

	/*
	  Methods used for text-Elements
	 */

	/**
	 * @return true if the element has a defining nature which means a new {@link Element} or {@link Attribute} is defined.
	 */
	public boolean isDefining() {
		return isOf(Nature.define);
	}

	public boolean isOpeningNamespace() {
		return isOf(Nature.ns);
	}

	/*
		General Methods
	 */

	@Override
	public final String toString() {
		StringBuilder str = new StringBuilder();
		str.append('\\').append(definition.name());
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
