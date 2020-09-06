package se.jbee.doc.scan;

import se.jbee.doc.*;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the scanner implementation that reads the build in synatx:
 *
 * <pre>
 *   \element[\attribute value \another-attribute [value value]]{
 *     text body
 *   }
 * </pre>
 */
public final class SystemScanner implements DocumentScanner {

	@Override
	public void scan(DocumentReader in, DocumentBuilder builder) throws IOException {
		int c = in.peek();
		while (c != -1 && c != '}') {
			scanPastElement(in, builder);
			c = in.peek();
		}
	}

	private static void scanPastElement(DocumentReader in, DocumentBuilder builder) throws IOException {
		in.skip(Character::isWhitespace);
		int c = in.peek();
		if (c == '#') {
			in.skip(SystemScanner::isNotNewLine);
			return;
		}
		if (c == '\\') {
			in.gobble('\\');
			c = in.peek();
			if (!isNameLetter(c)) { // escaped char as part of the inline content right at the start of the line
				//TODO
			}
			String element = in.until(SystemScanner::isNotNameLetter).toString();
			Define definition = builder.meta().getDefinition(element);
			if (definition.isDefining()) {
				Element e = definition.newElement();
				c = in.peek();
				if (c == '!') { // redefinition
					in.gobble('!');
					builder.beginRedefine(scanPastAttributes(in, builder, e));
				} else if (c == '*') { // born as a
					in.gobble('*');
					builder.beginDerive(scanPastAttributes(in, builder, e));
				} else {
					builder.begin(scanPastAttributes(in, builder, e));
				}
			} else {
				builder.begin(scanPastAttributes(in, builder, definition.newElement()));
			}
		} else {
			builder.begin(scanPastAttributes(in, builder, builder.createPlain()));
		}
		//TODO read body
		// 1. ask builder what scanner to use, might have change for the element body
	}

	private static boolean isNotNewLine(int cp) {
		return cp != '\n' && cp != '\r';
	}

	private static boolean isNotNameLetter(int cp) {
		return !isNameLetter(cp);
	}

	private static boolean isNameLetter(int cp) {
		return cp >= 'a' && cp <= 'z' || cp == '-' || cp == '_';
	}

	/**
	 * Starts after <code>\element[</code> and ends before closing <code>]</code> of the same elements attribute list.
	 *
	 * @param e the {@link Element} attributes were defined for
	 */
	private static Element scanPastAttributes(DocumentReader in, DocumentBuilder builder, Element e) throws IOException {
		int c;
		Define[] implicit = e.definition().implicit();
		int implicitIndex = 0;
		do  {
			in.skipWhitespace();
			c = in.peek();
			if (c != -1 && c != ']') {
				if (c == '\\') {
					Define key = builder.meta().getDefinition(readArg(in).toString().substring(1));
					in.skip(Character::isWhitespace);
					String[] value = readValue(in);
					e.add(key, value);
				} else {
					if (implicitIndex >= implicit.length)
						throw in.newUnexpectedCharacter('\\', c);
					Define key = implicit[implicitIndex++];
					String[] value = readValue(in);
					e.add(key, value);
				}
			}
		} while (c != ']' && c != -1);
		if (c == -1)
			throw in.newUnexpectedCharacter(']', -1);
		return e;
	}

	/**
	 * Either a quoted string or a until the next whitespace
	 */
	public static CharSequence readArg(DocumentReader in) throws IOException {
		int c = in.peek();
		if (c != '"')
			return in.until(Character::isWhitespace);
		in.gobble('"');
		CharSequence arg = in.until(cp -> cp == '"');
		in.gobble('"');
		return arg;
	}

	/**
	 * Reads a single value for a single attribute.
	 * This value can be a list or a simple value.
	 */
	private static String[] readValue(DocumentReader in) throws IOException {
		int c = in.peek();
		if (c == '\\') // next key, no value defined
			return new String[0];
		if (c != '[')
			return new String[] { readArg(in).toString() };
		in.gobble('[');
		List<String> args = new ArrayList<>();
		c = in.peek();
		while (c != -1 && c != ']') {
			in.skip(Character::isWhitespace);
			c = in.peek();
			if (c != -1 && c != ']') {
				args.add(readArg(in).toString());
				c = in.peek();
			}
		}
		if (c == -1)
			throw in.newUnexpectedCharacter(']', -1);
		in.gobble(']');
		return args.toArray(String[]::new);
	}


}
