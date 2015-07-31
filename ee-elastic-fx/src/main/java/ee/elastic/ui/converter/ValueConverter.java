package ee.elastic.ui.converter;

import java.util.Map;

public class ValueConverter extends ConverterAbstract {

  @Override
  public Object value(Map<String, Object> source) {
    return resolveProperty(source, column.path());
  }

}
