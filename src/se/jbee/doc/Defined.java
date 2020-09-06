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
		for (Nature n : definition().natures())
			if (n == nature)
				return true;
		return false;
	}
}
