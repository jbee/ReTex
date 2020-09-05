package se.jbee.doc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class Document {

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
	private String[] nsSeq;

	public static Document newDocument() {
		Document doc = new Document();
		doc.namespaces.put("system", new Namespace()
				.add(Define.DEFINE)
				.add(Define.NATURE)
				.add(Define.IS));
		doc.nsSeq = new String[] { "system" };
		return doc;
	}

	public Define getDefinition(String alias) {
		if (alias.indexOf(':') < 0) {
			for (String ns : nsSeq) {
				Define res = namespaces.get(nsSeq).getDefinition(alias);
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
		return new IllegalArgumentException("Not defined in visible namespaces "+ Arrays.toString(nsSeq) +" : " + alias);
	}

}
