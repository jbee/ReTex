package se.jbee.doc;

public interface Defined {

	/**
	 * @return The <code>\define[...]</code> object that defined the composition of this {@link Element} or {@link Attribute}.
	 */
	Define definition();

	default String name() {
		return definition().alias()[0];
	}

	default boolean isOf(Nature nature) {
		return nature == definition().nature();
	}
}
