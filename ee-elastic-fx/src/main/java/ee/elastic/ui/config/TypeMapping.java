package ee.elastic.ui.config;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class TypeMapping extends MapObject {
  private static final long serialVersionUID = 1L;
  private String type;

  public TypeMapping(String type, Map source) {
    super(source);
    this.type = type;
  }

  public String type() {
    return type;
  }

  public String parentType() {
    Map parent = (Map) source.get("_parent");
    if (parent != null) {
      return (String) parent.get("type");
    } else {
      return null;
    }
  }

  public VisualConfig visualConfig() {
    VisualConfig ret = null;
    Map _meta = (Map) source.get("_meta");
    if (_meta != null) {
      Map config = (Map) _meta.get("elasticsearch-head");
      if (config != null) {
        ret = new VisualConfig(config);
      }
    }
    return ret;
  }
}
