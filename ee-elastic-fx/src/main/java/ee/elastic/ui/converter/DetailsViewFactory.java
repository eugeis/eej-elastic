package ee.elastic.ui.converter;

import java.util.HashMap;

import ee.elastic.ui.config.ColumnDef;
import ee.elastic.ui.config.ConverterDef;
import ee.elastic.ui.converter.BytesToSizeConverter;
import ee.elastic.ui.converter.Converter;
import ee.elastic.ui.converter.DateConverter;
import ee.elastic.ui.converter.ValueConverter;

public class DetailsViewFactory {
	private final static String DEFAULT = "value";
	private HashMap<String, Class<? extends Converter>> idToConverterClass;
	private HashMap<ColumnDef, Converter> columnToConverter;

	public DetailsViewFactory() {
		super();
		idToConverterClass = new HashMap<>();
		columnToConverter = new HashMap<>();
		fillBuildIn();
	}

	private void fillBuildIn() {
		idToConverterClass.put(DEFAULT, ValueConverter.class);
		idToConverterClass.put("bytesToSize", BytesToSizeConverter.class);
		idToConverterClass.put("date", DateConverter.class);
	}

	public Converter converter(ColumnDef columnDef) {
		Converter ret = columnToConverter.get(columnDef);
		if (ret == null) {
			ret = createConverter(columnDef);
			columnToConverter.put(columnDef, ret);
		}
		return ret;
	}

	private Converter createConverter(ColumnDef columnDef) {
		Converter ret = null;
		ConverterDef converterDef = columnDef.converter();
		String converterId = converterDef != null ? converterDef.getId() : DEFAULT;
		Class<? extends Converter> clazz = idToConverterClass.get(converterId);
		ret = createConverterInstance(clazz);
		ret.setColumn(columnDef);
		return ret;
	}

	private Converter createConverterInstance(Class<? extends Converter> clazz) {
		Converter ret = null;
		if (clazz != null) {
			try {
				ret = (Converter) clazz.newInstance();
			} catch (Exception e) {
				System.err.println(String.format(
					"Exception [%s] ocurred at creation of converter from class [%s]. Use default converter.", e.getMessage(),
					clazz));
			}
		}
		if (ret == null) {
			ret = new ValueConverter();
		}
		return ret;
	}
}
