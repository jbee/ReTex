package se.jbee.doc;

import java.io.IOException;
import java.util.function.IntPredicate;

public interface DocumentReader {

	/**
	 *
	 * @return code point of the next character while also moving position after the read character
	 */
	int read() throws IOException;

	/**
	 *
	 * @return code point of the next character without moving position
	 */
	int peek() throws IOException;

	/**
	 *
	 * @param test function that returns true if look ahead should continue or false to end the peek
	 * @return number of characters looked ahead, return (-look-ahead - 1) in case end of input is reached
	 * (this is the number of {@link #read()} calls it takes to read these characters.
	 * This may differ from the actual number of characters in the source because of
	 * different encodings of new line as 1 or 2 characters whereas new lines are
	 * always one character for this abstraction).
	 */
	int peek(IntPredicate test) throws IOException;

	UnexpectedCharacter newUnexpectedCharacter(int expected, int actual);


	default int skipWhitespace() throws IOException {
		return skip(Character::isWhitespace);
	}

	default void gobble(int expected) throws IOException {
		int c = peek();
		if (c != expected)
			throw newUnexpectedCharacter(expected, c);
		read();
	}

	default int skip(IntPredicate test) throws IOException {
		int len = peek(test);
		if (len < 0)
			throw newUnexpectedCharacter(-2, -1);
		for (int i = 0; i < len; i++)
			read();
		return len;
	}

	default CharSequence until(IntPredicate test) throws IOException {
		StringBuilder str = new StringBuilder();
		int cp = -1;
		do {
			cp = peek();
			if (test.test(cp))
				return str;
			read();
			str.appendCodePoint(cp);
		} while (cp != -1);
		throw newUnexpectedCharacter(-2, -1);
	}

	final class Position {
		public final int lineNr;
		public final int lineOffset;
		public final int filePosition;

		public Position(int lineNr, int lineOffset, int filePosition) {
			this.lineNr = lineNr;
			this.lineOffset = lineOffset;
			this.filePosition = filePosition;
		}

		@Override
		public String toString() {
			return lineNr + ":" + lineOffset + " [" + filePosition + "]";
		}
	}
}
