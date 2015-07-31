package ee.elastic.ui.integ;

public interface StringConverter<E> {
  E object(String value);

  String string(E value);
}
