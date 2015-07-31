package ee.elastic.ui.config;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class MapObject implements Serializable {
  private static final long serialVersionUID = 1L;
  protected Map source;

  public MapObject() {
    super();
  }

  public MapObject(Map source) {
    super();
    this.source = source;
  }

  public Map source() {
    return source;
  }

  public void source(Map source) {
    this.source = source;
  }

  public Object get(String name) {
    return source.get(name);
  }

  public Object resolveProperty(String path) {
    Object relObj = source;
    String[] parts = path.split("\\.");
    if (parts != null && parts.length > 0) {
      for (String part : parts) {
        if (relObj instanceof Map) {
          relObj = ((Map) relObj).get(part);
        } else {
          System.out.println(String.format("Part [%s] of path [%s] can not be found in [%s] of [%s]", part, path, relObj, source));
          relObj = null;
          break;
        }
      }
    } else {
      relObj = source.get(path);
    }
    return relObj;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MapObject other = (MapObject) obj;
    if (source == null) {
      if (other.source != null)
        return false;
    } else if (!source.equals(other.source))
      return false;
    return true;
  }
}