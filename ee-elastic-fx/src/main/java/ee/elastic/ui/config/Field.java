package ee.elastic.ui.config;

public class Field implements Comparable<Field> {
  private String name;
  private String type;

  public Field() {
    super();
  }

  public Field(String name, String type) {
    super();
    this.name = name;
    this.type = type;
  }

  public String name() {
    return name;
  }

  public Field name(String name) {
    this.name = name;
    return this;
  }

  public String type() {
    return type;
  }

  public Field type(String type) {
    this.type = type;
    return this;
  }

  @Override
  public int compareTo(Field o) {
    return name.compareTo(o.name);
  }

  @Override
  public String toString() {
    return "Field [name=" + name + ", type=" + type + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    Field other = (Field) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }
}
