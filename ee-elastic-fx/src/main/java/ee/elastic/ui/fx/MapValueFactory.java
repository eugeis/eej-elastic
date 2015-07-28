package ee.elastic.ui.fx;

import java.util.Map;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;

@SuppressWarnings("rawtypes")
public class MapValueFactory extends ValueFactoryAbstract {
	private String key = null;

	public MapValueFactory() {
		super();
	}

	public MapValueFactory(String key) {
		super();
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public ObservableValue call(CellDataFeatures celldatafeatures) {
		if (key == null) {
			return wrapValue(((Map) celldatafeatures.getValue()).get(celldatafeatures.getTableColumn().getText()
				.toLowerCase()));
		} else {
			return wrapValue(((Map) celldatafeatures.getValue()).get(key));
		}
	}
}
