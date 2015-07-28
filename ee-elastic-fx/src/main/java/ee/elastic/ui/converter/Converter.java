package ee.elastic.ui.converter;

import java.util.Map;

import ee.elastic.ui.config.ColumnDef;

public interface Converter {
	void setColumn(ColumnDef column);

	Object value(Map<String, Object> source);
}
