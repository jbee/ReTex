package se.jbee.doc.tree;

import se.jbee.doc.Nature;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Attribute implements Defined {

	//TODO constants: basically an attribute that is defined once and reused including its value

	private final Define definition;
	public final String[] values;
	private Object[] valuesCache;

	public Attribute(Define definition, Nature value) {
		this(definition, value.name());
	}

	@SafeVarargs
	public Attribute(Define definition, String... values) {
		this.definition = definition;
		this.values = values;
	}

	@Override
	public Define definition() {
		return definition;
	}

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
				if (i > 0) str.append(' ');
				str.append(values[i]);
			}
			str.append(']');
		} else {
			str.append(values[0]);
		}
	}

	public <T> T[] values(Class<T> as, IntFunction<T[]> create, Function<String, T> map) {
		if (valuesCache != null) {
			if (valuesCache.getClass().getComponentType() != as)
				throw new IllegalStateException("Already something else");
			return (T[]) valuesCache;
		}
		Class<?> expectedType = definition.nature().attrType;
		Class<?> actualType = as;
		if (actualType != expectedType)
			throw new IllegalArgumentException("Expected " + expectedType + " but tried " + actualType);
		valuesCache = Arrays.stream(values).map(map).toArray(create);
		return (T[]) valuesCache;
	}

}
