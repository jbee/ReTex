package se.jbee.doc.scan;

public final class ReaderPosition {

	/**
	 * Line number starting with 1
	 */
	public final int line;
	/**
	 * Character offset in the current line starting with 1
	 */
	public final int column;

	/**
	 * Absolute number of characters from start of the file.
	 */
	public final int character;

	public ReaderPosition(int line, int column, int character) {
		this.line = line;
		this.column = column;
		this.character = character;
	}

	@Override
	public String toString() {
		return line + ":" + column + " [" + character + "]";
	}
}
