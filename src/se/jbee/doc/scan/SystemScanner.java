package se.jbee.doc.scan;

import se.jbee.doc.*;
import se.jbee.doc.tree.Define;
import se.jbee.doc.tree.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the scanner implementation that reads the build in syntax:
 *
 * <pre>
 *   \element[
 *   	\attribute value \another-attribute [value value]]{
 *   	text body
 *   }
 * </pre>
 */
public final class SystemScanner implements DocumentScanner {

	@Override
	public void scan(DocumentReader in, DocumentBuilder builder, boolean headless) throws IOException {
		int c = in.peek();
		while (c != -1 && c != '}') {
			scanElementHeader(in, builder);
			c = in.peek();
		}
	}

	private static void scanElementHeader(DocumentReader in, DocumentBuilder builder) throws IOException {
		in.skipWhitespace();
		int c = in.peek();
		if (c == '#') {
			in.skip(SystemScanner::isNotNewLine);
			in.skip(SystemScanner::isNewLine);
			return;
		}
		Element element;
		if (c == '\\') {
			in.gobble('\\');
			c = in.peek();
			if (!isNameLetter(c)) { // escaped char as part of the inline content right at the start of the line
				//TODO
			}
			String name = in.until(SystemScanner::isNotNameLetter).toString();
			DocumentContext context = builder.context();
			Define definition = context.getDefinition(name);
			element = definition.newElement();
			if (definition.isOf(Nature.define)) {
				c = in.peek();
				if (c == '!') { // redefinition
					in.gobble('!');
					element.add(context.operation(), Operation.REDEFINE.name());
				} else if (c == '*') { // born as a
					in.gobble('*');
					element.add(context.operation(), Operation.DERIVE.name());
				} else if (c == '+') {
					in.gobble('+');
					element.add(context.operation(), Operation.EXTEND.name());
				}
			}
		} else {
			element = builder.createPlain();
		}
		scanElementAttributes(in, builder, element);
		builder.add(element, () -> scanElementBody(in, builder));
	}

	private static void scanElementBody(DocumentReader in, DocumentBuilder builder) {
		in.skipWhitespace();
		int c = in.peek();
		if (c == '{') {
			in.gobble('{');
			//TODO
			in.gobble('}');
		}
	}

	private static boolean isNotNewLine(int cp) {
		return !isNewLine(cp);
	}

	private static boolean isNewLine(int cp) {
		return cp == '\n' || cp == '\r';
	}

	private static boolean isNotNameLetter(int cp) {
		return !isNameLetter(cp);
	}

	private static boolean isNameLetter(int cp) {
		return cp >= 'a' && cp <= 'z' || cp == '-' || cp == '_';
	}

	/**
	 * Starts after <code>\element[</code> and ends before closing <code>]</code>
	 * of the same elements attribute list.
	 *
	 * @param e the {@link Element} attributes were defined for
	 */
	private static void scanElementAttributes(DocumentReader in, DocumentBuilder builder, Element e) {
		int c;
		Define[] implicit = e.definition().implicit();
		int implicitIndex = 0;
		do {
			in.skipWhitespace();
			c = in.peek();
			if (c != -1 && c != ']') {
				if (c == '\\') {
					in.gobble('\\');
					Define key = builder.context().getDefinition(scanValue(in));
					in.skipWhitespace();
					String[] value = scanValues(in);
					e.add(key, value);
				} else {
					if (implicitIndex >= implicit.length) throw in.mismatch('\\', c);
					Define key = implicit[implicitIndex++];
					String[] value = scanValues(in);
					e.add(key, value);
				}
			}
		} while (c != ']' && c != -1);
		if (c == -1) throw in.mismatch(']', -1);
	}

	/**
	 * Either a quoted string or a until the next whitespace
	 */
	public static String scanValue(DocumentReader in) {
		int c = in.peek();
		if (c != '"') return in.until(Character::isWhitespace).toString();
		in.gobble('"');
		CharSequence arg = in.until(cp -> cp == '"');
		in.gobble('"');
		return arg.toString();
	}

	/**
	 * Reads a single value for a single attribute.
	 * This value can be a list or a simple value.
	 */
	private static String[] scanValues(DocumentReader in) {
		int c = in.peek();
		if (c == '\\') // next key, no value defined
			return new String[0];
		if (c != '[') return new String[]{scanValue(in)};
		in.gobble('[');
		List<String> args = new ArrayList<>();
		c = in.peek();
		while (c != -1 && c != ']') {
			in.skipWhitespace();
			c = in.peek();
			if (c != -1 && c != ']') {
				args.add(scanValue(in));
				c = in.peek();
			}
		}
		if (c == -1) throw in.mismatch(']', -1);
		in.gobble(']');
		return args.toArray(String[]::new);
	}


}
