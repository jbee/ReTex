package se.jbee.doc;

import se.jbee.doc.tree.Attribute;
import se.jbee.doc.tree.Define;
import se.jbee.doc.tree.Element;

public enum Operation {
	/**
	 * The actual operation is decided based on the {@link Element} and {@link
	 * Attribute} {@link Nature}s.
	 * <p>
	 * For {@link Element}s of {@link Nature#define} a new {@link Define} is
	 * made.
	 * <p>
	 * For {@link Element}s of {@link Nature#ns} namespace is opened
	 * <p>
	 * Otherwise the {@link Element} is added to the document tree.
	 */
	AUTO,

	/**
	 * Must be used with {@link Element}s of {@link Nature#define}.
	 * Expects the element of same {@link Nature#alias} already being defined.
	 * Then redefines that element.
	 */
	REDEFINE,

	DERIVE,

	/**
	 * Must be used with {@link Element}s of {@link Nature#define}.
	 * Adds or overrides the given {@link Attribute}s for the {@link Element}
	 * which already must exist.
	 */
	EXTEND,
}
