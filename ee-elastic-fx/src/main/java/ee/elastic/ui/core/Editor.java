package ee.elastic.ui.core;

import javafx.scene.Parent;

public interface Editor<P extends Parent, E> extends View<P, E> {
	E data();
}
