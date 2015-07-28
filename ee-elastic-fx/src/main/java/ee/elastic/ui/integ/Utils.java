package ee.elastic.ui.integ;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static <S, T> ArrayList<T> transform(Collection<S> source, Func<S, T> transformer) {
		ArrayList<T> ret = null;
		if (source != null) {
			ret = new ArrayList<>();
			for (S item : source) {
				ret.add(transformer.call(item));
			}
		}
		return ret;
	}

	public static <E> E findFirst(Collection<E> source, Func<E, Boolean> selector) {
		E ret = null;
		if (source != null) {
			for (E item : source) {
				if (selector.call(item)) {
					ret = item;
					break;
				}
			}
		}
		return ret;
	}

	public static <E extends Comparable<? super E>> List<E> sort(Set<E> collection) {
		ArrayList<E> sortedIndices = new ArrayList<>(collection);
		Collections.sort(sortedIndices);
		return sortedIndices;
	}

	public static String jsonPretty(Object data) {
		StringWriter stringWriter = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(stringWriter).useDefaultPrettyPrinter();
			generator.writeObject(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringWriter.toString();
	}

	public static String jsonPretty(String json) {
		StringWriter stringWriter = new StringWriter();
		try {
			JsonGenerator generator = MAPPER.getJsonFactory().createJsonGenerator(stringWriter).useDefaultPrettyPrinter();
			Map<?, ?> value = MAPPER.readValue(json, Map.class);
			generator.writeObject(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringWriter.toString();
	}

	public static <E> E jsonObject(String json, Class<E> clazz) {
		E ret = null;
		try {
			ret = MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Map<?, ?> jsonMap(String json) {
		return jsonObject(json, Map.class);
	}

	public static String jsonCompact(String json) {
		String ret = json;
		try {
			Map<?, ?> value = MAPPER.readValue(json, Map.class);
			ret = MAPPER.writeValueAsString(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
