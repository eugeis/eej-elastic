package ee.elastic.ui.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import ee.elastic.ui.config.ColumnDef;

public abstract class ConverterAbstract implements Converter {
	protected ColumnDef column;

	public void setColumn(ColumnDef column) {
		this.column = column;
	}

	@SuppressWarnings("rawtypes")
	protected Object resolveProperty(Map<String, Object> source, String path) {
		Object relObj = source;
		String[] parts = path.split("\\.");
		if (parts != null && parts.length > 0) {
			for (String part : parts) {
				if (relObj instanceof Map) {
					relObj = ((Map) relObj).get(part);
				} else {
					System.out.println(String.format("Part [%s] of path [%s] can not be found in [%s] of [%s]", part, path,
						relObj, source));
					relObj = null;
					break;
				}
			}
		} else {
			relObj = source.get(path);
		}
		return relObj;
	}

	protected Long parseLong(Object value, Long defaultValue) {
		Long ret;
		if (value != null) {
			if (value instanceof Long) {
				ret = ((Long) value);
			} else if (value instanceof Integer) {
				ret = Long.valueOf(((Integer) value).longValue());
			} else {
				ret = Long.valueOf(value.toString());
			}
		} else {
			ret = defaultValue;
		}
		return ret;
	}

	protected Integer parseInteger(Object value, Integer defaultValue) {
		Integer ret;
		if (value != null) {
			if (value instanceof Long) {
				ret = Integer.valueOf(((Long) value).intValue());
			} else if (value instanceof Integer) {
				ret = (Integer) value;
			} else {
				ret = Integer.valueOf(value.toString());
			}
		} else {
			ret = defaultValue;
		}
		return ret;
	}

	protected DateFormat parseDateFormat(Object value, String defaultPattern) {
		DateFormat ret = null;
		if (value != null) {
			String pattern = String.valueOf(value);
			try {
				ret = new SimpleDateFormat(pattern);
			} catch (Exception e) {
				System.err.println(String.format("Exception [%s] to parse the date format [%s]. Use default pattern.",
					e.getMessage(), pattern));
			}
		}

		if (ret == null) {
			ret = new SimpleDateFormat(defaultPattern);
		}
		return ret;
	}
}
