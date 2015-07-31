package ee.elastic.ui.core;

import java.io.Serializable;

import ee.elastic.ui.formatter.Formatter;

public class StringValue<E extends Serializable> implements Comparable<StringValue<E>>, Serializable {
  private static final long serialVersionUID = 1L;

  private E value;
  private Formatter<E> formatter;

  public StringValue() {
    super();
  }

  public StringValue(E value, Formatter<E> formatter) {
    super();
    this.value = value;
    this.formatter = formatter;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public int compareTo(StringValue<E> o) {
    if (o instanceof Comparable) {
      return ((Comparable) value).compareTo(o.getValue());
    } else {
      return this.toString().compareTo(String.valueOf(o));
    }
  }

  @Override
  public String toString() {
    return formatter.string(value);
  }

  public E getValue() {
    return value;
  }

  public void setValue(E value) {
    this.value = value;
  }
}
