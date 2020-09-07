package se.jbee.doc.scan;

import se.jbee.doc.DocumentBuilder;
import se.jbee.doc.tree.Element;

import java.io.IOException;

/**
 * A {@link DocumentScanner} implements the semantics of a particular syntax to
 * build {@link se.jbee.doc.Document}s using a {@link DocumentBuilder}.
 */
@FunctionalInterface
public interface DocumentScanner {

	/**
	 * Continue the scanning using the {@link DocumentReader}.
	 *
	 * @param in       to use to read the input
	 * @param builder  to use to build the document
	 * @param headless true if the head {@link Element} has already
	 *                 been scanned and the scanner should expect the body next,
	 *                 else false (expect elements/auto).
	 * @throws IOException
	 */
	void scan(DocumentReader in, DocumentBuilder builder, boolean headless) throws IOException;
}
