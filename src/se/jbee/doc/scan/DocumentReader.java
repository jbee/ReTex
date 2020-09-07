package se.jbee.doc.scan;

import java.util.function.IntPredicate;

public interface DocumentReader {

	/*
		4 fundamental character stream operations: read, peek(1), peek(fn), throw
	 */

	/**
	 * @return code point of the next character while also moving position after
	 * the read character
	 */
	int read();

	/**
	 * @return code point of the next character without moving position
	 */
	int peek();

	/**
	 * @param cpTest function that returns true if look ahead should continue or
	 *               false to end the peek for the given code point
	 * @return number of characters looked ahead, return (-look-ahead - 1) in case
	 * end of input is reached
	 * (this is the number of {@link #read()} calls it takes to read these
	 * characters.
	 * This may differ from the actual number of characters in the source because
	 * of
	 * different encodings of new line as 1 or 2 characters whereas new lines are
	 * always one character for this abstraction).
	 */
	int peek(IntPredicate cpTest);

	UnexpectedCharacter mismatch(int cpExpected, int cpActual);


	/*
		Utilities based on the 4 fundamental operations
	 */

	default int skipWhitespace() {
		return skip(Character::isWhitespace);
	}

	default void gobble(int cpExpected) {
		int c = peek();
		if (c != cpExpected) throw mismatch(cpExpected, c);
		read();
	}

	default int skip(IntPredicate cpTest) {
		int len = peek(cpTest);
		if (len < 0) throw mismatch(-2, -1);
		for (int i = 0; i < len; i++)
			read();
		return len;
	}

	default CharSequence until(IntPredicate cpTest) {
		StringBuilder str = new StringBuilder();
		int cp = -1;
		do {
			cp = peek();
			if (cpTest.test(cp)) return str;
			read();
			str.appendCodePoint(cp);
		} while (cp != -1);
		throw mismatch(-2, -1);
	}

}
