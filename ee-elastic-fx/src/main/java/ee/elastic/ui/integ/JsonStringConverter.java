package ee.elastic.ui.integ;

public class JsonStringConverter<E> implements StringConverter<E> {
	private Class<E> clazz;

	public JsonStringConverter(Class<E> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public E object(String value) {
		return Utils.jsonObject(value,clazz);
	}

	@Override
	public String string(E value) {
		return Utils.jsonPretty(value);
	}

}
