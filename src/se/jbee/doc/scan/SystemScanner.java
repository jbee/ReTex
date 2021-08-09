package se.jbee.doc.scan;

import se.jbee.doc.*;
import se.jbee.doc.tree.Define;
import se.jbee.doc.tree.Element;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isWhitespace;

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
	public void scan(DocumentReader src, DocumentBuilder doc, boolean headless) {
		if (headless) {
			scanElementBody(src, doc);
			return;
		}
		int cp = src.peek();
		while (cp != -1) {
			scanDetect(src, doc);
			cp = src.peek();
		}
	}

	private static void scanDetect(DocumentReader src, DocumentBuilder doc) {
		src.skipWhitespace();
		int cp = src.peek();
		if (cp == '#') {
			src.skip(SystemScanner::isNotNewLine);
			src.skip(SystemScanner::isNewLine);
			return;
		}
		if (cp == '\\') {
			scanElement(src, doc);
		} else {
			scanTextBody(src, doc);
		}
	}

	private static void scanTextBody(DocumentReader src, DocumentBuilder doc) {
		// scan text:
		// start at current position and ends
		// - with a blank line
		// - a line starting with a command
		// - a }
		// - end of input
		doc.openTextBody();
		boolean blankLine = false;
		int cp = src.peek();
		StringBuilder text = new StringBuilder(80);
		while (cp != -1) {
			if (cp == '\\') {
				if (blankLine) { // end condition 2
					addText(text, doc);
					doc.closeTextBody();
					return;
				}
				// inline command or escaped symbol
				int cp2 = src.peek(2);
				if (isNameLetter(cp2) || cp2 == '[' || cp2 == '{') { // inline command:
					addText(text, doc);
					text.setLength(0);
					scanElement(src, doc);
				} else { // assume escaped symbol: (\, #)
					src.gobble('\\');
					text.append(src.read());
				}
			} else {
				// blank line tracking:
				if (isNewLine(cp)) {
					if (blankLine) { // end condition 1
						if (trimRight(text)) // fold any number of white-space to a single space
							text.append(' ');
						addText(text, doc);
						doc.closeTextBody();
						return;
					}
					text.append(' ');
					blankLine = true;
					src.skipWhitespace();
				} else if (cp == '}') { // end condition 3
					//OBS. we do not gobble the }, this is assumed to be expected by outer element
					addText(text, doc);
					doc.closeTextBody();
					return;
				} else {
					// append text
					text.append(src.read());
					blankLine = blankLine && isWhitespace(cp);
				}
			}
			cp = src.peek();
		}
		// end condition 4
		addText(text, doc);
		doc.closeTextBody();
	}

	private static void addText(StringBuilder text, DocumentBuilder doc) {
		if (text.length() > 0)
			doc.addText(text);
	}

	private static boolean trimRight(StringBuilder text) {
		int initialLength = text.length();
		int len = initialLength;
		while (isWhitespace(text.charAt(len-1)))
			len--;
		return len < initialLength;
	}

	private static void scanElement(DocumentReader src, DocumentBuilder doc) {
		src.gobble('\\');
		int cp = src.peek();
		//TODO handle inline elements without name
		String name = src.until(SystemScanner::isNotNameLetter).toString();
		System.out.println("Reading "+name);
		DocumentContext context = doc.context();
		Define definition = context.definition(name);
		Element element = definition.newElement();
		if (definition.isOf(Nature.define)) {
			cp = src.peek();
			if (cp == '!') { // redefinition
				src.gobble('!');
				element.add(context.operation(), Operation.REDEFINE.name());
			} else if (cp == '*') { // born as a
				src.gobble('*');
				element.add(context.operation(), Operation.DERIVE.name());
			} else if (cp == '+') {
				src.gobble('+');
				element.add(context.operation(), Operation.EXTEND.name());
			}
		}
		scanElementAttributes(src, doc, element);
		doc.addElement(element);
		scanElementBody(src, doc);
	}

	private static void scanElementBody(DocumentReader src, DocumentBuilder doc) {
		int cp = src.peek();
		if (cp == '{') {
			src.gobble('{');
			doc.openElementBody();
			do {
				src.skipWhitespace();
				cp = src.peek();
				if (cp == '}') {
					src.gobble('}');
					doc.closeElementBody();
				} else {
					scanDetect(src, doc);
				}
			} while (cp != -1 && cp != '}');
			if (cp == -1)
				src.mismatch('}', cp);
		}
	}

	/**
	 * Starts after <code>\element[</code> and ends before closing <code>]</code>
	 * of the same elements attribute list.
	 *
	 * @param e the {@link Element} attributes were defined for
	 */
	private static void scanElementAttributes(DocumentReader src, DocumentBuilder doc, Element e) {
		src.skipWhitespace();
		if (src.peek() != '[')
			return;
		src.gobble('[');
		int cp;
		Define[] implicit = e.definedAs().implicit();
		int implicitIndex = 0;
		do {
			src.skipWhitespace();
			cp = src.peek();
			if (cp != -1 && cp != ']') {
				if (cp == '\\') {
					src.gobble('\\');
					Define key = doc.context().definition(scanAttributeValue(src));
					src.skipWhitespace();
					String[] value = scanAttributeValues(src);
					e.add(key, value);
				} else {
					if (implicitIndex >= implicit.length) throw src.mismatch('\\', cp);
					Define key = implicit[implicitIndex++];
					String[] value = scanAttributeValues(src);
					e.add(key, value);
				}
			}
		} while (cp != ']' && cp != -1);
		if (cp == -1) throw src.mismatch(']', -1);
	}

	/**
	 * Reads a single value for a single attribute.
	 * This value can be a list or a simple value.
	 */
	private static String[] scanAttributeValues(DocumentReader src) {
		int cp = src.peek();
		if (cp == '\\') // next key, no value defined
			return new String[0];
		if (cp != '[') return new String[]{scanAttributeValue(src)};
		src.gobble('[');
		List<String> args = new ArrayList<>();
		cp = src.peek();
		while (cp != -1 && cp != ']') {
			src.skipWhitespace();
			cp = src.peek();
			if (cp != -1 && cp != ']') {
				args.add(scanAttributeValue(src));
				cp = src.peek();
			}
		}
		if (cp == -1) throw src.mismatch(']', -1);
		src.gobble(']');
		return args.toArray(String[]::new);
	}

	/**
	 * Either a quoted string or a until the next whitespace
	 */
	private static String scanAttributeValue(DocumentReader src) {
		int cp = src.peek();
		if (cp != '"') return src.until(SystemScanner::isEndOfAttributeValue).toString();
		src.gobble('"');
		CharSequence arg = src.until(cp2 -> cp2 == '"');
		src.gobble('"');
		return arg.toString();
	}

	private static boolean isEndOfAttributeValue(int cp) {
		return isWhitespace(cp) || cp == ']';
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
}
