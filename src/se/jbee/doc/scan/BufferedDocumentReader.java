package se.jbee.doc.scan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.function.IntPredicate;

public final class BufferedDocumentReader implements DocumentReader {

	private final BufferedReader in;
	private int line = 1;
	private int column = 1;
	private int character;

	public BufferedDocumentReader(Reader in) {
		this.in = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
	}

	@Override
	public int read() {
		try {
			int cp = in.read();
			updatePosition(cp);
			return cp;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public int peek() {
		try {
			in.mark(1);
			int cp = in.read();
			in.reset();
			return cp;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public int peek(IntPredicate cpTest) {
		try {
			int c = 0;
			in.mark(256);
			int cp = in.read();
			while (cp != -1 && cpTest.test(cp)) {
				c++;
				cp = in.read();
			}
			in.reset();
			return cp == -1 ? (-c - 1) : c;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public UnexpectedCharacter mismatch(int cpExpected, int cpActual) {
		return new UnexpectedCharacter(cpExpected, cpActual, new ReaderPosition(line, column, character));
	}

	private void updatePosition(int c) throws IOException {
		character++;
		if (c != '\r' && c != '\n') {
			column++;
			return;
		}
		column = 1;
		line++;
		if (c == '\n') return; // we are sure we are done
		// c must be \r, a \n might follow
		c = peek();
		if (c == '\n') {
			character++;
			in.read(); // gobble the 2nd char of CR LF
		}
	}
}
