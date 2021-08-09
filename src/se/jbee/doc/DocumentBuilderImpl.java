package se.jbee.doc;

import se.jbee.doc.tree.Define;
import se.jbee.doc.tree.Element;
import se.jbee.doc.tree.Namespace;

import java.util.*;

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

	private final Deque<Namespace> targetNsStack = new LinkedList<>();
	private final List<Namespace> importedNsSequence = new ArrayList<>();

	public DocumentBuilderImpl(Document doc) {
		this.doc = doc;
		if (doc.ns("system") == null) {
			for (Namespace ns : Define.bootstrap(this)) {
				doc.add(ns);
				importedNsSequence.add(ns);
			}
		}
	}

	@Override
	public DocumentContext context() {
		return this;
	}

	@Override
	public Define definition(String alias) {
		int nsIndex = alias.indexOf(':');
		if (nsIndex < 0) {
			for (Namespace ns : targetNsStack) {
				Define res = ns.definition(alias);
				if (res != null) return res;
			}
			for (Namespace ns : importedNsSequence) {
				Define res = ns.definition(alias);
				if (res != null) return res;
			}
			throw notDefined(alias);
		}
		String ns = alias.substring(0, nsIndex);
		alias = alias.substring(nsIndex + 1);
		Define res = doc.ns(ns).definition(alias);
		if (res != null) return res;
		throw notDefined(alias);
	}

	private NoSuchElementException notDefined(String alias) {
		return new NoSuchElementException("Not defined in imported namespaces " + importedNsSequence + " : " + alias);
	}

	@Override
	public void addElement(Element header) {
		addAuto(header);
	}

	@Override
	public void addText(CharSequence text) {

	}

	@Override
	public void openElementBody() {
		//TODO this is where a namespace is eventually created when a NS element body is opened and no such space exists yet
	}

	@Override
	public void closeElementBody() {

	}

	@Override
	public void openTextBody() {

	}

	@Override
	public void closeTextBody() {

	}

	private void addAuto(Element header) {
		if (header.isOf(Nature.ns)) {
			if (header.has(Nature.ref)) {
				// opens the referenced namespace in read-only mode
				for (String ns : header.get(Nature.ref).values) {

				}
			} else { // read-write access to the namespace
				inNsReadWrite(header);
				return;
			}
		}
		if (header.isOf(Nature.define)) {
			//TODO define in NS
		} else {
			//TODO add to doc body
		}
	}

	private void inNsReadWrite(Element header) {
		Namespace ns = doc.ns(header.name());
		if (ns == null)
			throw new IllegalArgumentException("Namespace not defined: "+ header.name());
		targetNsStack.addFirst(ns);
		//TODO
		targetNsStack.removeFirst();
		return;
	}

	private void addRedefine(Element header) {
		if (!header.isOf(Nature.define))
			throw new IllegalArgumentException("Redefine must be done with element of nature define but got: " + header);
		Define existing = definition(header.name());
		if (existing == null)
			throw new IllegalStateException("Redefine used for undefined element: " + header);
		if (existing.isLocked())
			throw new IllegalStateException("Existing element cannot be redefined because it has already been used for content.");

	}

	private void addDerive(Element header) {

	}

}
