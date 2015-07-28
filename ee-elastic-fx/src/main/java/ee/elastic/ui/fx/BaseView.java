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

	public ViewDef viewDef() {
		return viewDef;
	}

	public void viewDef(ViewDef view) {
		this.viewDef = view;
	}

	public P root() {
		return root;
	}

}