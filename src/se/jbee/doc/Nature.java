package se.jbee.doc;

import se.jbee.doc.tree.Define;

public enum Nature {

	// Content:
	/**
	 * the defined element is a form of textual content, like heading, section,
	 * paragraph, ...; it is meant to be readable - this is the fundamental nature
	 * for content)
	 */
	text(null),

	/**
	 * Used as attribute for {@link #text} elements to further describe the type of text component.
	 */
	component(Component.class),

	/**
	 * (content given for the sake of annotating more information to support
	 * information processing, indexing, review, etcetera; differs from layer in
	 * that layers only make sense to rendering while note classifies the content
	 * as not being part of the main body of text in a way that is universally
	 * understood)
	 * <p>
	 * TODO use to set author, add author as nature
	 */
	note(String.class),
	/**
	 * A named "pointer" to a particular element or inline section,
	 * <p>
	 * When used as element it wraps around the inline text that is marked.
	 * <p>
	 * When used as an attribute the marked position is the beginning of the
	 * element that the mark is added to as an attribute
	 */
	mark(String.class),

	//TODO a nature that hints about the type of text, e.g. number, date, ... or just plain text
	// in connection maybe add another nature to give a pattern for valid values? or have that build into the available types?
	//TODO a nature to point out the language of the text content ?
	//TODO also a nature for reader specific information type so that data can be stored using the system? for example put the java type - the correct interpretation is only known by the software that created the file but still a neat feature

	// Counters:
	/**
	 * (links a text element to a named counter; on usage of the text element the
	 * counter is incremented)
	 */
	counter(String.class),
	/**
	 * (links a text element to a named counter; on usage of the text element the
	 * counter is reset)
	 */
	reset(String.class),

	// Presentation:
	/**
	 * (vaguely: a hint for the rendering where a content is presented, e.g.
	 * document body, comment, header, footer, ...)
	 */
	layer(String.class),
	/**
	 * (as attribute: a general hint that rendering makes sense of based on the
	 * attribute name, basically a way to add key-value data for
	 * rendering/presentation, these will always be specific to the presentation
	 * used. as element: used to wrap around content to trigger a certain renderer
	 * identified by the elements name)
	 * <p>
	 * TODO this should maybe work differently so that one can specify for each
	 * element additional hints for each presentation system; for example for html
	 * give the tag that corresponds to the element
	 */
	style(String.class),

	// Identity:
	/**
	 * (an elements that defines new elements or attributes OR (when used as
	 * attribute) the attribute that references the nature of the defined element
	 * or attribute)
	 */
	define(Nature.class),

	operation(Operation.class),

	/**
	 * (refers to "parent" element or attribute definition(s) which are the basis
	 * of the defined element or attribute, it x _is a type of_ y; when used in an
	 * element that is not of nature `define` it makes the element of the referred
	 * sort)
	 */
	is_a(Define.class),
	/**
	 * (adds one or more alias names for the defined element or attribute; for
	 * example to create an abbr. name to use in inline formatting)
	 */
	alias(String.class),
	/**
	 * (gives one or more references to element or attributes, the assumed type(s)
	 * are given through the `accept` nature as defined for the attribute of nature `ref`.
	 *
	 * For example when `accept` is
	 * `define` the reference is to `alias` name(s) of a `define` element)
	 */
	ref(Define.class),

	// Validity:
	/**
	 * (gives a set of elements, is a elements (`*`) or natures (`&`) that is
	 * valid as actual value for the defined *attribute*, if used with elements
	 * this restricts possible child elements or attributes depending on the
	 * natures of the items in the set)
	 */
	accept(Qualifier.class),
	/**
	 * (what are attributes that must be given in `[...]` to the defined element
	 * or attribute)
	 */
	must(Qualifier.class),
	/**
	 * (what are attributes that may be given in `[...]` to the defined element or
	 * attribute)
	 */
	may(Qualifier.class),

	//Inference:
	/**
	 * (what are direct parents to the defined element/attribute)
	 */
	in(Define.class),
	/**
	 * (what are known direct children to the defined element)
	 */
	around(Define.class),
	/**
	 * (what is assumed in `\[...]` if no keys are given for the defined
	 * element/attribute)
	 */
	implicit(Define.class),
	/**
	 * (what is assumed for line with no type around after the usage of the
	 * defined element)
	 */
	plain(Define.class),
	/**
	 * (what is assumed for `\[...]` => `\x[...]` in text following the defined
	 * element)
	 */
	inline(Define.class),

	// Interpretation:
	/**
	 * Points out the responsible scanner/lexer implementation.
	 * If none is set usual syntax is assumed.
	 * <p>
	 * Applies for the body of the element that got the attribute,
	 * can occur in definition or usage of an element to just apply to the single
	 * occurrence.
	 * <p>
	 * Possible values: 'system', 'verbatim'
	 */
	scanner(String.class),

	/**
	 * Generally used to configure interpretation of other processing.
	 */
	control(String.class),

	// Modularisation:
	/**
	 * (declare and use namespaces)
	 */
	ns(null),



	;

	public final Class<?> attrType;

	Nature(Class<?> attrType) {
		this.attrType = attrType;
	}


	public boolean canBeElement() {
		return mustBeElement() || this == define || this == ns;
	}

	private boolean mustBeElement() {
		return this == text || this == note;
	}

	public boolean mustBeAttribute() {
		return !canBeElement();
	}

	public boolean canBeAttribute() {
		return !mustBeElement();
	}
}
