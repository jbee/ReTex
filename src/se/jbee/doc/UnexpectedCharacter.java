package se.jbee.doc;

import java.text.ParseException;

public final class UnexpectedCharacter extends IllegalArgumentException {

	public UnexpectedCharacter(int expected, int actual, CharReader reader) {
		super("Expected `" + print(expected)
				+ "` but found `" + print(actual)
				+ "` at " + reader.getLineNr() + ":" + reader.getLineOffset()
				+ " [" + reader.getFilePosition() + "]");
	}

	private static String print(int codePoint) {
			return codePoint == -1 ? "EOS" : Character.toString(codePoint);
	}
}
