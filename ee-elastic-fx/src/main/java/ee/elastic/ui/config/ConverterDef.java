package ee.elastic.ui.config;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class ConverterDef extends MapObject {
	private static final long serialVersionUID = 1L;

	public ConverterDef(Map source) {
		super(source);
	}

	public String getId() {
		return (String) source.get("id");
	}
}
