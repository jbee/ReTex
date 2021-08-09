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
	 * <p>
	 * Expects the element of same {@link Nature#alias} already being defined.
	 * Then redefines that element.
	 */
	REDEFINE,

	/**
	 * Must be used with {@link Element}s of {@link Nature#define}.
	 * <p>
	 * Expects an {@link Attribute} of {@link Nature#is_a} to refer to one or
	 * more
	 * elements of same nature which become its "parents".
	 * <p>
	 * Usually this is used with the "born" syntax using a <code>*</code> after
	 * the single parent the new entry should be derived from.
	 * <pre>
	 *   \ns*[foo] => \define[ns foo \is-a ns]
	 * </pre>
	 */
	DERIVE,

	/**
	 * Must be used with {@link Element}s of {@link Nature#define}.
	 * <p>
	 * Adds or overrides the given {@link Attribute}s for the {@link Element}
	 * which already must exist.
	 */
	EXTEND,
}
