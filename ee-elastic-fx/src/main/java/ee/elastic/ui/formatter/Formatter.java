package ee.elastic.ui.formatter;

import java.io.Serializable;

public interface Formatter<E> extends Serializable {
	String string(E value);
}
