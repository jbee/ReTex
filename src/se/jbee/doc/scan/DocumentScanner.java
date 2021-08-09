package se.jbee.doc.scan;

import se.jbee.doc.DocumentBuilder;
import se.jbee.doc.tree.Element;

/**
 * A {@link DocumentScanner} implements the semantics of a particular syntax to
 * build {@link se.jbee.doc.Document}s using a {@link DocumentBuilder}.
 */
@FunctionalInterface
public interface DocumentScanner {

	/**
	 * Continue the scanning using the {@link DocumentReader}.
	 *
	 * @param src      {@link DocumentReader} to use to read the source input
	 * @param doc      {@link DocumentBuilder} to use to build the output {@link
	 *                 se.jbee.doc.Document}
	 * @param headless true if the head {@link Element} has already
	 *                 been scanned and the scanner should expect the body next,
	 *                 else false (expect elements/auto).
	 */
	void scan(DocumentReader src, DocumentBuilder doc, boolean headless);
}
