package se.jbee.doc;

import se.jbee.doc.tree.Define;
import se.jbee.doc.tree.Defined;

/**
 * Possible patterns:
 * <p>
 * - a nature: <code>&nature</code>
 * - a exact alias: <code>alias</code>
 * - a is-a alias: <code>*alias</code>
 */
public final class Qualifier {

	private final Nature nature;
	private final Define qualifier;
	private final boolean isa;

	public Qualifier(Nature nature) {
		this(nature, null, false);
	}

	public Qualifier(Define qualifier, boolean isa) {
		this(null, qualifier, isa);
	}

	private Qualifier(Nature nature, Define qualifier, boolean isa) {
		this.nature = nature;
		this.qualifier = qualifier;
		this.isa = isa;
	}

	public boolean matches(Defined actual) {
		if (nature != null) return actual.definedAs().isOf(nature);
		String name = actual.definedAs().name();
		if (!isa) return qualifier.name().equals(name);
		return matches(name, qualifier);
	}

	private boolean matches(String name, Define qualifier) {
		if (qualifier == null) return false;
		if (qualifier.name().equals(name)) return true;
		Define[] isa = qualifier.isa();
		if (isa.length == 0) return false;
		for (Define e : isa)
			if (matches(name, e)) return true;
		return false;
	}

	@Override
	public String toString() {
		return nature != null ? "&" + nature.name() : (isa ? "*" : "") + qualifier.name();
	}
}
