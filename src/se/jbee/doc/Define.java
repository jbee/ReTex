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

	static {
		DocumentContext context = Define::bootstrapContext;
		DEFINE.set(Nature.define, new Attribute(context, NATURE, new String[] { Nature.define.name() }));
		DEFINE.set(Nature.alias, new Attribute(context, IS, new String[] { "alias" }));
		NATURE.set(Nature.define, new Attribute(context, NATURE, new String[] { Nature.define.name() }));
		NATURE.set(Nature.alias, new Attribute(context, IS, new String[] { "nature" }));
		IS.set(Nature.define, new Attribute(context, NATURE, new String[] { Nature.alias.name() }));
		IS.set(Nature.alias, new Attribute(context, IS, new String[] { "is" }));
	}

	private Map<String, Define> usedBy = new HashMap<>();

	public Define(DocumentContext context, Define definition) {
		super(context, definition);
	}

	/**
	 * Minimal definition required to bootstrap all other definitions from documents.
	 * The 3 minimal definitions should be redefined by the bootstrapping document.
	 */
	private Define(Define definition) {
		super(Define::bootstrapContext, definition);
	}

	private static Define bootstrapContext(String alias) {
		throw new UnsupportedOperationException("None of the bootstrap definitions should ever be resolved as a " + Define.class.getSimpleName());
	}

	/*
	  Methods used for define-Elements
	 */

	public Define[] implicit() {
		Attribute implicit = get(Nature.implicit);
		return implicit == null ? new Define[0] : implicit.asDefinitions();
	}

}
