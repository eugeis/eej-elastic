package ee.elastic.ui.fx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.elasticsearch.cluster.metadata.MappingMetaData;
//import org.osito.javafx.dialog.message.FXOptionPane;
//import org.osito.javafx.dialog.message.Option;
//import org.osito.javafx.dialog.message.OptionType;

import ee.elastic.ui.config.Metadata;
import ee.elastic.ui.core.Model;
import ee.elastic.ui.core.View;
import ee.elastic.ui.integ.Func;
import ee.elastic.ui.integ.JsonStringConverter;
import ee.elastic.ui.integ.Utils;

public class OverviewController {
  @SuppressWarnings("rawtypes")
  private final class IndexSelectionListener implements ChangeListener {
    @SuppressWarnings({ "unchecked" })
    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
      if (newValue != null && newValue instanceof Map) {
        Map<String, Object> row = (Map) newValue;
        String index = String.valueOf(row.get("index"));
        selectIndex(index);
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private final class TypeSelectionListener implements ChangeListener {
    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
      if (newValue != null && newValue instanceof String) {
        selectType(newValue);
      }
    }
  }

  private final class IndexStatusRowBuilder implements Func<IndexStatus, Map<String, Object>> {
    @Override
    public Map<String, Object> call(IndexStatus source) {
      HashMap<String, Object> ret = new HashMap<>();
      ret.put("index", source.getIndex());
      ret.put("size", source.getPrimaryStoreSize().toString());
      ret.put("docs", source.getDocs().getNumDocs());
      return ret;
    }
  }

  private TableView<Map<String, Object>> indicesTable;
  private final Metadata metadata;
  private final Model model;
  @SuppressWarnings("unused")
  private AnchorPane detailsStatus;
  @SuppressWarnings("unused")
  private AnchorPane detailsMetadata;
  private ListView<String> detailsTypes;
  private AnchorPane detailsTypeMapping;
  private String selectedIndex;
  private String selectedType;
  private View<? extends Parent, Map<?, ?>> mappingView;
  private TextAreaEditor<Map<?, ?>> mappingEditor;
  private Stage stage;
  private boolean edit = false;

  public OverviewController(Stage stage, Model model, TableView<Map<String, Object>> indicesTable, ListView<String> detailsTypes, AnchorPane detailsTypeMapping, AnchorPane detailsStatus, AnchorPane detailsMetadata) {
    super();
    this.model = model;
    this.metadata = model.metadata();
    this.indicesTable = indicesTable;
    this.detailsTypes = detailsTypes;
    this.detailsTypeMapping = detailsTypeMapping;
    this.detailsStatus = detailsStatus;
    this.detailsMetadata = detailsMetadata;
    createMappingViews();
    switchView(false);
    fillIndices();
    addSelecteIndexListener();
    addSelectedTypeListener();
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void createMappingViews() {
    mappingView = new HtmlView<>();
    Display.fillAnchorZero(mappingView.root().getProperties());
    Class clazz = Map.class;
    JsonStringConverter<Map<?, ?>> stringConverter = new JsonStringConverter<>(clazz);
    mappingEditor = new TextAreaEditor<>(stringConverter);
    Display.fillAnchorZero(mappingEditor.root().getProperties());
  }

  private void switchView(boolean edit) {
    ObservableList<Node> children = detailsTypeMapping.getChildren();
    if (this.edit != edit || children.isEmpty()) {
      this.edit = edit;
      refreshMappingData();
      children.clear();
      if (edit) {
        children.add(mappingEditor.root());
      } else {
        children.add(mappingView.root());
      }
    }
  }

  private void selectIndex(String index) {
    selectedIndex = index;
    detailsTypes.getItems().clear();
    Set<String> types = metadata.types(index);
    if (types != null) {
      detailsTypes.getItems().addAll(Utils.sort(types));
    }
  }

  private void selectType(Object newValue) {
    selectedType = (String) newValue;
    refreshMappingData();
  }

  private void refreshMappingData() {
    if (selectedType != null) {
      MappingMetaData mapping = metadata.metadata(selectedIndex, selectedType);
      try {
        if (edit) {
          mappingEditor.data(mappingEditor.getStringConverter().object(mapping.source().string()));
        } else {
          mappingView.data(mapping.sourceAsMap());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void fillIndices() {
    addCellFactories();
    indicesTable.getItems().clear();
    indicesTable.getItems().addAll(Utils.transform(metadata.status().values(), new IndexStatusRowBuilder()));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void addCellFactories() {
    for (TableColumn column : indicesTable.getColumns()) {
      column.setCellValueFactory(new MapValueFactory());
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void addSelecteIndexListener() {
    TableViewSelectionModel selectionModel = indicesTable.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    selectionModel.selectedItemProperty().addListener(new IndexSelectionListener());
  }

  @SuppressWarnings({ "unchecked" })
  private void addSelectedTypeListener() {
    MultipleSelectionModel<String> selectionModel = detailsTypes.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    selectionModel.selectedItemProperty().addListener(new TypeSelectionListener());
  }

  public void editMapping() {
    switchView(true);
  }

  public void cancelMapping() {
    switchView(false);
  }

  public void saveMapping() {
    if (edit && selectedType != null) {
      model.updateMapping(selectedIndex, selectedType, mappingEditor.data());
      switchView(false);
    }
  }

  public void deleteIndex() {
    if (selectedIndex != null) {
      //			FXOptionPane.showConfirmDialog("All data of the index '" + selectedIndex + "' will be deleted.\n"
      //					+ "Do you want to delete the index '" + selectedIndex + "'?", OptionType.OK_CANCEL_OPTION,
      //					new Callback<Option, Object>() {
      //
      //						@Override
      //						public Object call(Option option) {
      //							if (Option.OK == option) {
      //								model.deleteIndex(selectedIndex);
      //								fillIndices();
      //								detailsTypes.getItems().clear();
      //							}
      //							return null;
      //						}
      //
      //					});
    }
  }

  public void newIndex() {

  }

  public void closeIndex() {

  }

  public void testAnalyzer() {

  }

  public void gatewaySnapshot() {

  }

  public void refreshFlush() {

  }

  public void refreshIndex() {
    model.refreshMetadata();
    fillIndices();
    detailsTypes.getItems().clear();
  }

  public void newAliasIndex() {

  }
}
