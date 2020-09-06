package se.jbee.doc;

import java.io.IOException;
import java.io.Reader;

public interface DocumentScanner {

	//TODO most likely it is better to change the Reader parameter to a custom
	// type so that wrapping it with nested scanner semantics is not so difficult
	// to implement since there are only a few methods supported:
	// - read char/code-point
	// - read line
	// - mark
	// - reset

	void scan(DocumentReader in, DocumentBuilder builder) throws IOException;
}
