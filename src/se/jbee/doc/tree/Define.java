package se.jbee.doc.tree;

import se.jbee.doc.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * An {@link Element} created for elements of {@link Nature#define}.
 */
public final class Define extends Element {

	static final Pattern ALIAS = Pattern.compile("[a-z](:?[-a-z]*[a-z])?");
	static final Pattern NAME = Pattern.compile("[-a-zA-Z0-9_]+");

	/**
	 * Bootstrapping of the 3 essential definitions required to make further definitions possible.
	 */
	static {

	}

	private static Define bootstrapContext(String alias) {
		throw new UnsupportedOperationException("None of the bootstrap definitions should ever be resolved as a " + Define.class.getSimpleName());
	}

	public static Namespace bootstrap(DocumentContext context) {
		Define define = new Define(context, null);
		Define nature = new Define(define);
		Define is = new Define(define);
		Define system = new Define(define);
		define.add(Nature.define, new Attribute(nature, Nature.define));
		define.add(Nature.alias, new Attribute(is, "alias"));
		nature.add(Nature.define, new Attribute(nature, Nature.define));
		nature.add(Nature.alias, new Attribute(is, "nature"));
		is.add(Nature.define, new Attribute(nature, Nature.alias));
		is.add(Nature.alias, new Attribute(is, "is"));
		system.add(Nature.define, new Attribute(nature, Nature.ns));
		system.add(Nature.alias, new Attribute(is, "system"));
		return new Namespace(system)
				.add(define)
				.add(nature)
				.add(is);
	}

	public final Style styles;
	private Map<String, Define> partOfDefinitions = new HashMap<>();

	public Define(Define definition) {
		this(definition.context, definition);
	}
	private Define(DocumentContext context, Define definition) {
		super(context, definition, Nature.define);
		this.styles = new Style(definition);
	}

	public Element newElement() {
		if (isOf(Nature.define)) return new Define(this);
		if (isOf(Nature.text)) return new Text(this);
		if (isOf(Nature.ns)) {
			Element ns = context.getDefinition("system:" + name() );
			return ns != null ? (Namespace) ns : new Namespace(this);
		}
		return new Element(this);
	}

	/*
	  Attributes of a define-Elements
	 */

	public Operation operation() {
		return value(Nature.operation, Operation.AUTO, Operation.class, Operation[]::new, Operation::valueOf);
	}

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
		return valuesAsCategory(Nature.accept);
	}

	public Category[] must() {
		return valuesAsCategory(Nature.must);
	}

	public Category[] may() {
		return valuesAsCategory(Nature.may);
	}

	private Category[] valuesAsCategory(Nature nature) {
		return values(nature, Category.class, Category[]::new, this::newCategory);
	}

	private Category newCategory(String pattern) {
		if (pattern.charAt(0) == '&')
			return new Category(Nature.valueOf(pattern.substring(1)));
		boolean isa = pattern.charAt(0) == '*';
		if (isa) pattern = pattern.substring(1);
		return new Category(context.getDefinition(pattern), isa);
	}
}
