package se.jbee.doc;

import java.text.ParseException;

public final class UnexpectedCharacter extends IllegalArgumentException {

	public UnexpectedCharacter(int expected, int actual, DocumentReader.Position position) {
		super("Expected `" + print(expected)
				+ "` but found `" + print(actual)
				+ "` at " + position.toString());
	}

	private static String print(int codePoint) {
			return codePoint == -1 ? "(eos)" : codePoint == -2 ? "(fn)" : Character.toString(codePoint);
	}
}
