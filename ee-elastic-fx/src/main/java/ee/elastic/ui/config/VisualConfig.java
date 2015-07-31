package ee.elastic.ui.config;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class VisualConfig extends MapObject {
  private static final long serialVersionUID = 1L;

  public VisualConfig(Map source) {
    super(source);
  }

  public Boolean isLoadParents() {
    return (Boolean) source.get("loadParents");
  }

  public BrowserDef browser() {
    if (source.get("browser") != null) {
      return new BrowserDef((Map) source.get("browser"));
    } else {
      return null;
    }
  }

  public ViewDef detailsView() {
    if (source.get("previewConverter") != null) {
      return new ViewDef((Map) source.get("previewConverter"));
    } else {
      return null;
    }
  }
}
