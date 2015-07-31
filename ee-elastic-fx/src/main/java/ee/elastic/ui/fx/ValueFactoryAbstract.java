package ee.elastic.ui.fx;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

@SuppressWarnings("rawtypes")
public abstract class ValueFactoryAbstract implements Callback<CellDataFeatures, ObservableValue> {

  @SuppressWarnings({ "unchecked" })
  protected ObservableValue wrapValue(Object obj) {
    if (obj instanceof ObservableValue)
      return (ObservableValue) obj;
    if (obj instanceof Boolean)
      return new ReadOnlyBooleanWrapper(((Boolean) obj).booleanValue());
    if (obj instanceof Integer)
      return new ReadOnlyIntegerWrapper(((Integer) obj).intValue());
    if (obj instanceof Float)
      return new ReadOnlyFloatWrapper(((Float) obj).floatValue());
    if (obj instanceof Long)
      return new ReadOnlyLongWrapper(((Long) obj).longValue());
    if (obj instanceof Double)
      return new ReadOnlyDoubleWrapper(((Double) obj).doubleValue());
    if (obj instanceof String)
      return new ReadOnlyStringWrapper((String) obj);
    else
      return new ReadOnlyObjectWrapper(obj);
  }

}