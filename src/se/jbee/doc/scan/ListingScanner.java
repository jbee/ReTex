package se.jbee.doc.scan;

import se.jbee.doc.tree.Define;
import se.jbee.doc.DocumentBuilder;
import se.jbee.doc.tree.Element;

import java.io.IOException;

/**
 * A flexible {@link DocumentScanner} that does basic code highlighting based
 * on a configuration of keywords and literal patterns.
 * <p>
 * On the plus side inline directives can be enabled to mark code sections.
 */
public class ListingScanner implements DocumentScanner {

	/**
	 *
	 * @param definition
	 * @param usage The {@link Element} that effectivly switched to this {@link ListingScanner}
	 */
	public ListingScanner(Define definition, Element usage) {

	}

	@Override
	public void scan(DocumentReader in, DocumentBuilder builder, boolean headless) throws IOException {

	}
}
