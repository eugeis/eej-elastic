package ee.elastic.ui.fx;

import java.util.Map;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import ee.elastic.ui.config.ColumnDef;
import ee.elastic.ui.converter.Converter;
import ee.elastic.ui.core.ColumnsValuesCache;

@SuppressWarnings("rawtypes")
public class ColumnValueFactory extends ValueFactoryAbstract {
  private final ColumnsValuesCache<ObservableValue> cache;
  private final ColumnDef column;
  private final Converter converter;

  public ColumnValueFactory(ColumnDef column, Converter converter, ColumnsValuesCache<ObservableValue> cache) {
    this.column = column;
    this.converter = converter;
    this.cache = cache;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ObservableValue call(CellDataFeatures celldatafeatures) {
    Map row = (Map) celldatafeatures.getValue();
    if (cache.exists(row, column)) {
      return cache.get(row, column);
    } else {
      ObservableValue ret = wrapValue(converter.value(row));
      cache.put(row, column, ret);
      return ret;
    }
  }

  public ColumnDef getColumn() {
    return column;
  }
}
