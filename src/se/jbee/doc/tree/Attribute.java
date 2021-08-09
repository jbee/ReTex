package se.jbee.doc.tree;

import se.jbee.doc.Nature;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Attribute implements Defined {

	//TODO constants: basically an attribute that is defined once and reused including its value

	private final Define definedAs;
	public final String[] values;
	private Object[] valuesCache;

	public Attribute(Define definedAs, Nature value) {
		this(definedAs, value.name());
	}

	@SafeVarargs
	public Attribute(Define definedAs, String... values) {
		this.definedAs = definedAs;
		this.values = values;
	}

	@Override
	public Define definedAs() {
		return definedAs;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		toString(str);
		return str.toString();
	}

	public void toString(StringBuilder str) {
		str.append('\\').append(name()).append(' ');
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
		if (as != Nature.class) { // checking would cause endless loop
			Class<?> expectedType = definedAs.nature().attrType;
			Class<?> actualType = as;
			if (actualType != expectedType)
				throw new IllegalArgumentException("Expected " + expectedType + " but tried " + actualType);
		}
		valuesCache = Arrays.stream(values).map(map).toArray(create);
		return (T[]) valuesCache;
	}

}
