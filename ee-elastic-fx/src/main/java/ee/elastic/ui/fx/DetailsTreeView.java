package ee.elastic.ui.fx;

import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

import org.elasticsearch.search.SearchHitField;

public class DetailsTreeView<E> extends BaseView<AnchorPane, E> {
	public DetailsTreeView() {
		super();
		root = new AnchorPane();
	}

	private Node createNode(Map<?, ?> value) {
		TreeItem<String> treeRoot = new TreeItem<String>("Root node");
		treeRoot.setExpanded(true);
		createPropertyRecursive(treeRoot, value);
		TreeView<?> ret = new TreeView<>(treeRoot);
		ret.setShowRoot(false);
		return ret;
	}

	@SuppressWarnings({ "rawtypes" })
	private void createPropertyRecursive(TreeItem<String> parent, Map<?, ?> spec) {
		for (Entry prop : spec.entrySet()) {
			TreeItem<String> child = createNode(prop.getKey());
			parent.getChildren().add(child);
			if (prop.getValue() instanceof Map) {
				createPropertyRecursive(child, Map.class.cast(prop.getValue()));
			} else if (prop.getValue() instanceof SearchHitField) {
				SearchHitField searchHitField = (SearchHitField) prop.getValue();
				TreeItem<String> fieldChild;
				if (searchHitField.getValue() instanceof Map) {
					fieldChild = createNode(searchHitField.getName());
					createPropertyRecursive(fieldChild, ((Map) searchHitField.getValue()));
				} else {
					fieldChild = createNode(searchHitField.getName() + " = " + String.valueOf(searchHitField.getValue()));
				}
				child.getChildren().add(fieldChild);
			} else {
				child.setValue(child.getValue() + " = " + String.valueOf(prop.getValue()));
			}
		}
	}

	private TreeItem<String> createNode(Object name) {
		TreeItem<String> ret = new TreeItem<>(String.valueOf(name));
		ret.setExpanded(true);
		return ret;
	}

	@SuppressWarnings({ "rawtypes" })
	public void data(E data) {
		root.getChildren().clear();
		if (data instanceof Map) {
			root.getChildren().add(createNode((Map) data));
		}
	}
}