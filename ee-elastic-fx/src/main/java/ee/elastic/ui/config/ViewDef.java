package ee.elastic.ui.config;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class ViewDef extends MapObject {
  private static final long serialVersionUID = 1L;

  public ViewDef(Map source) {
    super(source);
  }

  public String getId() {
    return (String) source.get("id");
  }
}
