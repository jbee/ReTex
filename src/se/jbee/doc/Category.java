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
public final class Category {

	private final Nature nature;
	private final Define category;
	private final boolean isa;

	public Category(Nature nature) {
		this(nature, null, false);
	}

	public Category(Define category, boolean isa) {
		this(null, category, isa);
	}

	private Category(Nature nature, Define category, boolean isa) {
		this.nature = nature;
		this.category = category;
		this.isa = isa;
	}

	public boolean matches(Defined actual) {
		if (nature != null) return actual.definition().isOf(nature);
		String name = actual.definition().name();
		if (!isa) return category.name().equals(name);
		return matches(name, category);
	}

	private boolean matches(String name, Define category) {
		if (category == null) return false;
		if (category.name().equals(name)) return true;
		Define[] isa = category.isa();
		if (isa.length == 0) return false;
		for (Define e : isa)
			if (matches(name, e)) return true;
		return false;
	}

	@Override
	public String toString() {
		return nature != null ? "&" + nature.name() : (isa ? "*" : "") + category.name();
	}
}
