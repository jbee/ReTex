package se.jbee.doc;

import java.util.*;

public class Namespaces implements DocumentMetadata {

	static final class Namespace {

		private final Map<String, Define> definitions = new HashMap<>();

		Namespace add(Define def) {
			for (String alias : def.get(Nature.alias).values)
				definitions.put(alias, def);
			return this;
		}

		Define getDefinition(String alias) {
			return definitions.get(alias);
		}

	}

	private final Map<String, Namespace> namespaces = new HashMap<>();
	private final Deque<String> openedNsStack = new LinkedList<>();
	private String[] visibleNsSeq;

	public static Namespaces createWithSystem() {
		Namespaces res = new Namespaces();
		res.namespaces.put("system", new Namespace()
				.add(Define.DEFINE)
				.add(Define.NATURE)
				.add(Define.IS));
		res.visibleNsSeq = new String[] { "system" };
		return res;
	}

	public void openNamespace(String ns) {
		openedNsStack.addFirst(ns);
	}

	public void closeNamsespace() {
		openedNsStack.removeFirst();
	}

	@Override
	public Define getDefinition(String alias) {
		if (alias.indexOf(':') < 0) {
			for (String ns : openedNsStack) {
				Define res = namespaces.get(visibleNsSeq).getDefinition(alias);
				if (res != null)
					return res;
			}
			for (String ns : visibleNsSeq) {
				Define res = namespaces.get(visibleNsSeq).getDefinition(alias);
				if (res != null)
					return res;
			}
			throw notDefined(alias);
		}
		String ns = alias.substring(0, alias.indexOf(':'));
		alias = alias.substring(alias.indexOf(':' + 1));
		Define res = namespaces.get(ns).getDefinition(alias);
		if (res != null)
			return res;
		throw notDefined(alias);
	}

	private IllegalArgumentException notDefined(String alias) {
		return new IllegalArgumentException("Not defined in visible namespaces "+ Arrays.toString(visibleNsSeq) +" : " + alias);
	}

}
