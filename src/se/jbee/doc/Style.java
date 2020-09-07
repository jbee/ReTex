package se.jbee.doc;

import se.jbee.doc.tree.Define;
import se.jbee.doc.tree.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Style {

	private final Define target;
	private final Map<String, List<Element>> styles = new HashMap<>();

	public Style(Define target) {
		this.target = target;
	}

	public void add(Element style) {
		if (!style.isOf(Nature.style))
			throw new IllegalArgumentException("Must be a style element");
		Define[] ref = style.ref();
		if (ref.length == 0)
			throw new IllegalArgumentException("A style definition needs a reference to the target declaration type");
		if (!ref[ref.length - 1].name().equals(target.name()))
			throw new IllegalArgumentException("Last reference element must be the target element of this style collection.");
		String system = style.definition().name();
		styles.computeIfAbsent(system, key -> new ArrayList<>()).add(style);
	}
}
