package se.jbee.doc.scan;

public final class UnexpectedCharacter extends IllegalArgumentException {

	public UnexpectedCharacter(int expected, int actual, ReaderPosition position) {
		super("Expected " + print(expected) + " but found " + print(actual) + " at " + position.toString());
	}

	private static String print(int codePoint) {
		return codePoint == -1 ? "end of stream" : codePoint == -2 ? "more input" : "`" + Character.toString(codePoint) + "`";
	}
}
