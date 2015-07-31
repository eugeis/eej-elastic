package ee.elastic.ui.config;

import java.util.HashMap;
import java.util.Map;

import ee.elastic.ui.integ.Func;

@SuppressWarnings("rawtypes")
public class ColumnDef extends MapObject {

  private static final String CONVERTER = "converter";
  private static final String LABEL = "label";
  private static final String PATH = "path";
  private static final String SORT_FIELD = "sortField";
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unchecked")
  public ColumnDef(String path, String label) {
    super(new HashMap());
    source.put(PATH, path);
    source.put(LABEL, label);
  }

  public ColumnDef(Map source) {
    super(source);
  }

  public String label() {
    String ret = (String) source.get(LABEL);
    return ret != null ? ret : path();
  }

  public String path() {
    return (String) source.get(PATH);
  }

  public String sortField() {
    if (source.containsKey(SORT_FIELD)) {
      return (String) source.get(SORT_FIELD);
    } else {
      return path().replaceFirst("(_source|fields)\\.", "");
    }
  }

  public ConverterDef converter() {
    return CONVERTER_TRANSFORMER.call((Map) source.get(CONVERTER));
  }

  private static Func<Map, ConverterDef> CONVERTER_TRANSFORMER = new Func<Map, ConverterDef>() {
    @Override
    public ConverterDef call(Map source) {
      if (source != null) {
        return new ConverterDef(source);
      } else {
        return null;
      }
    }
  };
}
