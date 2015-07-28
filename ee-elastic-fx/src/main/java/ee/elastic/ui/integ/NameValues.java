package ee.elastic.ui.integ;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NameValues<E> {
	private HashMap<String, HashSet<E>> nameToValues = new HashMap<>();

	public boolean exists(String index) {
		return get(index) != null;
	}

	public Set<E> get(String index) {
		HashSet<E> ret = nameToValues.get(index);
		return ret;
	}

	public void add(String name, E value) {
		HashSet<E> values = nameToValues.get(name);
		if (values == null) {
			values = new HashSet<>();
			nameToValues.put(name, values);
		}
		values.add(value);
	}

	public Set<E> values() {
		HashSet<E> ret = new HashSet<>();
		for (HashSet<E> values : nameToValues.values()) {
			ret.addAll((Collection<E>) values);
		}
		return ret;
	}

	public Set<String> names() {
		return nameToValues.keySet();
	}
}
