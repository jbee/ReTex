package se.jbee.doc;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	private final Document out;
	private final CharReader in;

	public Parser(Reader in, Document out) {
		this.in = new CharReader(in);
		this.out = out;
	}

	private void readElement() throws IOException {
		in.skip(Character::isWhitespace);
		int c = in.peek();
		if (c == '\\') {
			String element = in.until(Parser::isNotNameLetter).toString().substring(1);
			Define el = out.getDefinition(element);
			if (el.hasNature(Nature.define)) {
				c = in.peek();
				if (c == '!') { // redefinition
					in.gobble('!');
					//TODO
				} else if (c == '*') { // born as a
					in.gobble('*');
					//TODO
				}
			} else {
				//TODO not implemented yet, ns?
			}
		} else {
			//TODO start of plain element, check what element to start
		}
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
	 * @param element the {@link Element} attributes were defined for
	 */
	private void readAttributes(Element element) throws IOException {
		int c;
		Define[] implicit = element.definition.implicit();
		int implicitIndex = 0;
		do  {
			in.skip(Character::isWhitespace);
			c = in.peek();
			if (c != -1 && c != ']') {
				if (c == '\\') {
					Define key = out.getDefinition(in.readArg().toString().substring(1));
					in.skip(Character::isWhitespace);
					String[] value = readValue();
					element.set(key, value);
				} else {
					if (implicitIndex >= implicit.length)
						throw new UnexpectedCharacter('\\', c, in);
					Define key = implicit[implicitIndex++];
					String[] value = readValue();
					element.set(key, value);
				}
			}
		} while (c != ']' && c != -1);
		if (c == -1)
			throw new UnexpectedCharacter(']', -1, in);
	}

	/**
	 * Reads a single value for a single attribute.
	 * This value can be a list or a simple value.
	 */
	private String[] readValue() throws IOException {
		int c = in.peek();
		if (c == '\\') // next key, no value defined
			return new String[0];
		if (c != '[')
			return new String[] { in.readArg().toString() };
		in.gobble('[');
		List<String> args = new ArrayList<>();
		c = in.peek();
		while (c != -1 && c != ']') {
			in.skip(Character::isWhitespace);
			c = in.peek();
			if (c != -1 && c != ']') {
				args.add(in.readArg().toString());
				c = in.peek();
			}
		}
		if (c == -1)
			throw new UnexpectedCharacter(']', -1, in);
		in.gobble(']');
		return args.toArray(String[]::new);
	}


}
