package se.jbee.doc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.function.IntPredicate;

public final class CharReader {

	private final BufferedReader in;
	private int line = 1;
	private int offset = 1;
	private int position;

	public CharReader(Reader in) {
		this.in = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
	}

	public int getFilePosition() {
		return position;
	}

	public int getLineOffset() {
		return offset;
	}

	public int getLineNr() {
		return line;
	}

	public int peek() throws IOException {
		in.mark(1);
		int c = in.read();
		in.reset();
		return c;
	}

	public int read() throws IOException {
		int c = in.read();
		updatePosition(c);
		return c;
	}

	/**
	 * Either a quoted string or a until the next whitespace
	 */
	public CharSequence readArg() throws IOException {
		int c = peek();
		if (c != '"')
			return until(Character::isWhitespace);
		gobble('"');
		CharSequence arg = until(cp -> cp == '"');
		gobble('"');
		return arg;
	}

	public CharSequence until(IntPredicate test) throws IOException {
		StringBuilder str = new StringBuilder();
		int c;
		boolean isEnd = false;
		do {
			in.mark(1);
			c = in.read();
			isEnd = c == -1 || test.test(c);
			if (!isEnd) {
				updatePosition(c);
				str.appendCodePoint(c);
			}
		} while (!isEnd);
		in.reset();
		if (c == -1)
			throw new UnexpectedCharacter('?', -1, this);
		return str;
	}

	public void skip(IntPredicate test) throws IOException {
		int c;
		boolean skip = true;
		do {
			in.mark(1);
			c = in.read();
			skip = c != -1 && test.test(c);
			if (skip)
				updatePosition(c);
		} while(skip);
		in.reset();
		if (c == -1)
			throw new UnexpectedCharacter('?', -1, this);
	}

	public void gobble(int expected) throws IOException {
		int c = in.read();
		if (c != expected)
			throw new UnexpectedCharacter(expected, c, this);
		updatePosition(c);
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
