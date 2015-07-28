package ee.elastic.ui.converter;

import java.util.Map;

import ee.elastic.ui.config.ColumnDef;
import ee.elastic.ui.core.StringValue;
import ee.elastic.ui.formatter.BytesToSizeFormatter;

public class BytesToSizeConverter extends ConverterAbstract {
	private BytesToSizeFormatter formatter;

	@Override
	public void setColumn(ColumnDef column) {
		super.setColumn(column);
		formatter = new BytesToSizeFormatter(parseInteger(column.converter().get("precision"), 2));
	}

	@Override
	public Object value(Map<String, Object> source) {
		return new StringValue<Long>(parseLong(resolveProperty(source, column.path()), 0L), formatter);
	}
}
