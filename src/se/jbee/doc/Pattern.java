package se.jbee.doc;

/**
 * 	Possible patterns:
 *
 * 	- a nature: <code>&nature</code>
 * 	- a exact alias: <code>alias</code>
 * 	- a is-a alias: <code>*alias</code>
 */
public final class Pattern {

	private final Nature nature;
	private final Define element;
	private final boolean isa;

	public Pattern(Nature nature) {
		this(nature, null, false);
	}

	public Pattern(Define element, boolean isa) {
		this(null, element, isa);
	}

	private Pattern(Nature nature, Define element, boolean isa) {
		this.nature = nature;
		this.element = element;
		this.isa = isa;
	}

	public boolean matches(Defined actual) {
		if (nature != null)
			return actual.definition().isOf(nature);
		String name = actual.definition().name();
		if (!isa)
			return element.name().equals(name);
		return matches(name, element);
	}

	private boolean matches(String name, Define e) {
		if (e == null)
			return false;
		if (e.name().equals(name))
			return true;
		Define[] isa = e.isa();
		if (isa.length == 0)
			return false;
		for (Define isae : isa)
			if (matches(name, isae))
				return true;
		return false;
	}

	@Override
	public String toString() {
		return nature != null ? "&"+nature.name() : (isa ? "*" : "") + element.name();
	}
}
