package ee.elastic.ui.core;

import javafx.scene.Parent;
import ee.elastic.ui.config.ViewDef;

public interface View<P extends Parent, E> {
  void data(E data);

  P root();

  ViewDef viewDef();

  void viewDef(ViewDef viewDef);
}
