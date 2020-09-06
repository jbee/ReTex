package se.jbee.doc.scan;

import se.jbee.doc.DocumentBuilder;

import java.io.IOException;

@FunctionalInterface
public interface DocumentScanner {

	void scan(DocumentReader in, DocumentBuilder builder) throws IOException;
}
