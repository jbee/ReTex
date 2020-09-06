package se.jbee.doc.scan;

import se.jbee.doc.DocumentBuilder;
import se.jbee.doc.DocumentReader;
import se.jbee.doc.DocumentScanner;
import se.jbee.doc.Element;

import java.io.IOException;
import java.io.Reader;

/**
 * A flexible {@link DocumentScanner} that does basic code highlighting based
 * on a configuration of keywords and literal patterns.
 *
 * On the plus side inline directives can be enabled to mark code sections.
 */
public class ListingScanner implements DocumentScanner {

	public ListingScanner(Element scanner) {

	}

	@Override
	public void scan(DocumentReader in, DocumentBuilder builder) throws IOException {

	}
}
