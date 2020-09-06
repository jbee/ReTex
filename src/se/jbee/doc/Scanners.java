package se.jbee.doc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for managing the state of {@link DocumentScanner} implementations.
 */
public final class Scanners {

	private final Map<String, DocumentScanner> scanners = new HashMap<>();

	public DocumentScanner provide(Element scanner) {
		if (!scanner.isOf(Nature.scanner))
			throw new IllegalArgumentException("Argument must be of nature "+Nature.scanner+" but was "+ Arrays.toString(scanner.natures()) + " for element "+ scanner);
		String name = scanner.name();
		return scanners.computeIfAbsent(name, key -> {
			String className = scanner.get(Nature.code).values[0];
			try {
				Class<?> type = Class.forName(className);
				if (!DocumentScanner.class.isAssignableFrom(type))
					throw new IllegalArgumentException("Not a scanner implementation: "+type);
				try {
					return (DocumentScanner) type.getConstructor(Element.class).newInstance(scanner);
				} catch (NoSuchMethodException e1) {
					// fall
				}
				return (DocumentScanner) type.getConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		});
	}
}
