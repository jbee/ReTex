package se.jbee.doc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class Define extends Element {

	static final java.util.regex.Pattern ALIAS = java.util.regex.Pattern.compile("[a-z](:?[-a-z]*[a-z])?");
	static final java.util.regex.Pattern NAME = Pattern.compile("[-a-zA-Z0-9_]+");

	public static final Define DEFINE = new Define(null);
	public static final Define NATURE = new Define(DEFINE);
	public static final Define IS = new Define(DEFINE);

	/**
	 * Bootstrapping of the 3 essential definitions required to make further definitions possible.
	 */
	static {
		DEFINE.add(Nature.define, new Attribute(NATURE, new String[] { Nature.define.name() }));
		DEFINE.add(Nature.alias, new Attribute(IS, new String[] { "alias" }));
		NATURE.add(Nature.define, new Attribute(NATURE, new String[] { Nature.define.name() }));
		NATURE.add(Nature.alias, new Attribute(IS, new String[] { "nature" }));
		IS.add(Nature.define, new Attribute(NATURE, new String[] { Nature.alias.name() }));
		IS.add(Nature.alias, new Attribute(IS, new String[] { "is" }));
	}

	private final Style styles;

	private Map<String, Define> usedBy = new HashMap<>();

	public Define(DocumentContext context, Define definition) {
		super(context, definition);
		this.styles = new Style(definition);
	}

	/**
	 * Only used by the 3 essential bootstrap definitions
	 */
	private Define(Define definition) {
		super(Define::bootstrapContext, definition);
		this.styles = new Style(definition);
	}

	private static Define bootstrapContext(String alias) {
		throw new UnsupportedOperationException("None of the bootstrap definitions should ever be resolved as a " + Define.class.getSimpleName());
	}

	public Element newElement() {
		if (isDefining())
			return new Define(context, this);
		return new Element(context, this);
	}

	private Category[] valuesAsCategories(Nature nature) {
		return values(nature, Category.class, Category[]::new, this::newCategory);
	}

	private Category newCategory(String pattern) {
		if (pattern.charAt(0) == '&')
			return new Category(Nature.valueOf(pattern.substring(1)));
		boolean isa = pattern.charAt(0) == '*';
		if (isa)
			pattern = pattern.substring(1);
		return new Category(context.definitionFor(pattern), isa);
	}

	/*
	  Attributes of a define-Elements
	 */

	public String[] alias() {
		return get(Nature.alias).values;
	}

	public Define[] isa() {
		return valuesAsDefine(Nature.is_a);
	}

	public Define[] in() {
		return valuesAsDefine(Nature.in);
	}

	public Define[] around() {
		return valuesAsDefine(Nature.around);
	}

	public Define[] implicit() {
		return valuesAsDefine(Nature.implicit);
	}

	public Define plain() {
		return valueAsDefine(Nature.plain);
	}

	public Define inline() {
		return valueAsDefine(Nature.inline);
	}

	public Category[] accept() {
		return valuesAsCategories(Nature.accept);
	}

	public Category[] must() {
		return valuesAsCategories(Nature.must);
	}

	public Category[] may() {
		return valuesAsCategories(Nature.may);
	}
}
