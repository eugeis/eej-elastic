package ee.elastic.ui.fx;

import javafx.scene.control.TextArea;
import ee.elastic.ui.core.Editor;
import ee.elastic.ui.integ.StringConverter;

public class TextAreaEditor<E> extends BaseView<TextArea, E> implements Editor<TextArea, E> {
  private StringConverter<E> stringConverter;

  public TextAreaEditor(StringConverter<E> stringConverter) {
    super();
    root = new TextArea();
    root.setEditable(true);
    root.setWrapText(true);
    this.stringConverter = stringConverter;
  }

  @Override
  public void data(E data) {
    root.setText(stringConverter.string(data));
  }

  @Override
  public E data() {
    return stringConverter.object(root.getText());
  }

  public StringConverter<E> getStringConverter() {
    return stringConverter;
  }
}