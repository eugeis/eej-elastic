package ee.elastic.ui.integ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentChildKeyValues<P, C, K, V> extends ParentChildValues<P, C, Map<K, V>> {

	public boolean exists(P parent, C child, K key) {
		return exists(parent, child) && get(parent, child).containsKey(key);
	}

	@SuppressWarnings("unchecked")
	public <E> E get(P parent, C child, K key) {
		E ret = null;
		if (exists(parent, child, key)) {
			ret = (E) get(parent, child).get(key);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <E> E put(P parent, C child, K key, V value) {
		if (!exists(parent, child)) {
			put(parent, child, new HashMap<K, V>());
		}
		return (E) get(parent, child).put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> keyValues() {
		ArrayList<E> ret = new ArrayList<>();
		for (Map<K, V> keyValues : values()) {
			ret.addAll((Collection<? extends E>) keyValues.values());
		}
		return ret;
	}

}
