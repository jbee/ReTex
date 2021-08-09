package se.jbee.doc.tree;

import se.jbee.doc.Nature;

@FunctionalInterface
public interface Defined {

	/**
	 * @return The <code>\define[...]</code> object that defined the composition
	 * of this {@link Element} or {@link Attribute}.
	 *
	 * If this is a {@link Define} element itself this returns this itself as
	 * each definition declares the basis on which other {@link Element}s are
	 * defined.
	 */
	Define definedAs();

	default String name() {
		String[] alias = definedAs().alias();
		return alias == null ? null : alias[0];
	}

	default boolean isOf(Nature nature) {
		return nature == definedAs().nature();
	}
}
