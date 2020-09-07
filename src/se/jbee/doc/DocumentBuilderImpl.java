package se.jbee.doc;

import se.jbee.doc.tree.Define;
import se.jbee.doc.tree.Element;
import se.jbee.doc.tree.Namespace;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class DocumentBuilderImpl implements DocumentBuilder, DocumentContext {

	private final Document doc;

	/**
	 * Nesting from the current top level element down to the current inner most
	 * nested element.
	 * <p>
	 * Each time a new element is added with unclear parent the hierarchy is used
	 * to determine which element becomes the new parent and the hierarchy is
	 * updated accordingly to end with the new parent element.
	 */
	private final Deque<Element> hierarchy = new LinkedList<>();

	private final Deque<String> openedNsStack = new LinkedList<>();
	private String[] visibleNsSeq = {"system"};

	public DocumentBuilderImpl(Document doc) {
		this.doc = doc;
		if (doc.ns("system") == null) {
			doc.add(Define.bootstrap(this));
		}
	}

	@Override
	public DocumentContext context() {
		return this;
	}

	@Override
	public Define getDefinition(String alias) {
		if (alias.indexOf(':') < 0) {
			for (String ns : openedNsStack) {
				Define res = doc.ns(ns).getDefinition(alias);
				if (res != null) return res;
			}
			for (String ns : visibleNsSeq) {
				Define res = doc.ns(ns).getDefinition(alias);
				if (res != null) return res;
			}
			throw notDefined(alias);
		}
		String ns = alias.substring(0, alias.indexOf(':'));
		alias = alias.substring(alias.indexOf(':' + 1));
		Define res = doc.ns(ns).getDefinition(alias);
		if (res != null) return res;
		throw notDefined(alias);
	}

	private NoSuchElementException notDefined(String alias) {
		return new NoSuchElementException("Not defined in visible namespaces " + Arrays.toString(visibleNsSeq) + " : " + alias);
	}

	private void openNamespace(String ns) {
		openedNsStack.addFirst(ns);
	}

	private void closeNamsespace() {
		openedNsStack.removeFirst();
	}

	@Override
	public void add(Element header, Runnable body) {
		add(header);
	}

	private void add(Element header) {
		if (header.isOf(Nature.ns)) {
			if (header.has(Nature.ref)) {
				// opens the referenced namespace in read-only mode

			} else { // read-write access to the namespace
				String ns = header.name();
				//TODO check namespace exist (created when defined)
			}
		}
		if (header.isOf(Nature.define)) {

		} else {
			//TODO add to doc body
		}
	}

	private void redefine(Element header) {
		if (!header.isOf(Nature.define))
			throw new IllegalArgumentException("Redefine must be done with element of nature define but got: " + header);
		Define existing = getDefinition(header.name());
		if (existing == null)
			throw new IllegalStateException("Redefine used for undefined element: " + header);

	}

	private void derive(Element header) {

	}

	@Override
	public Element createPlain() {
		return null;
	}
}
