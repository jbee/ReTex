package se.jbee.doc.scan;

import se.jbee.doc.Document;
import se.jbee.doc.DocumentBuilder;
import se.jbee.doc.DocumentBuilderImpl;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class TestSystemScanner {

	public static void main(String[] args) throws FileNotFoundException {
		DocumentBuilder builder = new DocumentBuilderImpl(new Document());
		SystemScanner scanner = new SystemScanner();
		DocumentReader reader = new BufferedDocumentReader(new FileReader("doc/system/system.detex"));
		scanner.scan(reader, builder, false);
	}

}
