package se.jbee.doc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.function.IntPredicate;

public final class CharReader implements DocumentReader {

	private final BufferedReader in;
	private int line = 1;
	private int offset = 1;
	private int position;

	public CharReader(Reader in) {
		this.in = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
	}

	@Override
	public int read() throws IOException {
		int cp = in.read();
		updatePosition(cp);
		return cp;
	}

	@Override
	public int peek() throws IOException {
		in.mark(1);
		int cp = in.read();
		in.reset();
		return cp;
	}

	@Override
	public int peek(IntPredicate test) throws IOException {
		int c = 0;
		in.mark(256);
		int cp = in.read();
		while (cp != -1 && test.test(cp)) {
			c++;
			cp = in.read();
		}
		return cp == -1 ? (-c -1) : c;
	}

	@Override
	public UnexpectedCharacter newUnexpectedCharacter(int expected, int actual) {
		return new UnexpectedCharacter(expected, actual, new Position(line, offset, position));
	}

	private void updatePosition(int c) throws IOException {
		position++;
		if (c != '\r' && c != '\n') {
			offset++;
			return;
		}
		offset = 1;
		line++;
		if (c == '\n')
			return; // we are sure we are done
		// c must be \r, a \n might follow
		c = peek();
		if (c == '\n') {
			position++;
			in.read(); // gobble the 2nd char of CR LF
		}
	}
}
