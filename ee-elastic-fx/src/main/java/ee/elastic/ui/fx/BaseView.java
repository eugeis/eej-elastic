package ee.elastic.ui.fx;

import javafx.scene.Parent;
import ee.elastic.ui.config.ViewDef;
import ee.elastic.ui.core.View;

public abstract class BaseView<P extends Parent, D> implements View<P, D> {

  protected ViewDef viewDef;
  protected P root;

  public BaseView() {
    super();
  }

  @Override
  public ViewDef viewDef() {
    return viewDef;
  }

  @Override
  public void viewDef(ViewDef view) {
    this.viewDef = view;
  }

  @Override
  public P root() {
    return root;
  }

}