package se.jbee.doc.tree;

import se.jbee.doc.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * An {@link Element} created for elements of {@link Nature#define}.
 */
public class Define extends Element {

	//TODO maybe add a nature pattern that is used to validate values?
	static final Pattern ALIAS = Pattern.compile("[a-z](:?[-a-z]*[a-z])?");
	static final Pattern NAME = Pattern.compile("[-a-zA-Z0-9_]+");

	public static Namespace[] bootstrap(DocumentContext context) {
		// 1: \define[\nature define \is define]
		Define define = new Define(context, null); // define is defined by and as itself ;)
		// 2: \define[\nature define \is nature]
		Define nature = new Define(define);
		// 3: \define[\nature alias \is is]
		Define is = new Define(define);
		// 4: \define[\nature ns \is system]
		Define system = new Define(define);
		// 5: \define[\nature ns \is "_ns_"]
		Define ns = new Define(define);
		define.add(Nature.define, new Attribute(nature, Nature.define));
		define.add(Nature.alias, new Attribute(is, "define"));
		nature.add(Nature.define, new Attribute(nature, Nature.define));
		nature.add(Nature.alias, new Attribute(is, "nature"));
		is.add(Nature.define, new Attribute(nature, Nature.alias));
		is.add(Nature.alias, new Attribute(is, "is"));
		system.add(Nature.define, new Attribute(nature, Nature.ns));
		system.add(Nature.alias, new Attribute(is, "system"));
		ns.add(Nature.define, new Attribute(nature, Nature.ns));
		ns.add(Nature.alias, new Attribute(is, DocumentContext.NS_NS_ALIAS));
		// \system{
		//   \define (1-3)
		// }
		// \_ns_{
		//   \define (4-5)
		// }
		//NOTE: it is the use of an element defined with nature "ns" that creates the element of type Namespace
		return new Namespace[] {
				new Namespace(system).add(define).add(nature).add(is),
				new Namespace(ns).add(ns).add(system)
		};
	}

	public final Style styles;
	private Map<String, Define> partOfDefinitions = new HashMap<>();
	private boolean locked;

	/**
	 * @param definedAs refers to the element used to define the created {@link Define}
	 */
	public Define(Define definedAs) {
		this(definedAs.context, definedAs);
	}

	private Define(DocumentContext context, Define definedAs) {
		super(context, definedAs, Nature.define);
		this.styles = new Style(this);
	}

	@Override
	public Define definedAs() {
		return this; // any Define is defined as itself
	}

	/**
	 * @return the definition of the element that was used to define this one
	 */
	public Define definedBy() {
		return super.definedAs();
	}

	public Element newElement() {
		if (isOf(Nature.define)) return new Define(this);
		if (isOf(Nature.text)) return new Text(this);
		if (isOf(Nature.ns)) new Namespace(this);
		return new Element(this);
	}

	/*
	  Attributes of a define-Elements
	 */

	public Operation operation() {
		return value(Nature.operation, Operation.AUTO, Operation.class, Operation[]::new, Operation::valueOf);
	}

	public String[] alias() {
		Attribute alias = get(Nature.alias);
		return alias == null ? null : alias.values;
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

	public Qualifier[] accept() {
		return valuesAsCategory(Nature.accept);
	}

	public Qualifier[] must() {
		return valuesAsCategory(Nature.must);
	}

	public Qualifier[] may() {
		return valuesAsCategory(Nature.may);
	}

	public boolean isLocked() {
		return locked;
	}

	void lock() {
		locked = true;
	}

	@Override
	protected void references(Define key) {
		String name = name();
		if (name != null) // during bootstrapping name might not yet be defined
			key.partOfDefinitions.put(name, this);
	}

	private Qualifier[] valuesAsCategory(Nature nature) {
		return values(nature, Qualifier.class, Qualifier[]::new, this::newCategory);
	}

	private Qualifier newCategory(String pattern) {
		if (pattern.charAt(0) == '&')
			return new Qualifier(Nature.valueOf(pattern.substring(1)));
		boolean isa = pattern.charAt(0) == '*';
		if (isa) pattern = pattern.substring(1);
		return new Qualifier(context.definition(pattern), isa);
	}
}
