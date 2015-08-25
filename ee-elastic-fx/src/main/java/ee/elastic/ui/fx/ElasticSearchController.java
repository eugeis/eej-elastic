package ee.elastic.ui.fx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleButtonBuilder;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ee.elastic.ui.config.Field;
import ee.elastic.ui.config.Metadata;
import ee.elastic.ui.config.ViewDef;
import ee.elastic.ui.core.Model;
import ee.elastic.ui.core.View;
import ee.elastic.ui.integ.Utils;

@SuppressWarnings("rawtypes")
public class ElasticSearchController implements Initializable {

  private final class AllIndicesChangeListener implements ChangeListener<String> {
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      if (isRealValue(newValue)) {
        model.clearSearchIndices();
        model.searchIndex(newValue, true);
      } else {
        model.clearSearchIndices();
      }
      fillIndices();
    }

    private boolean isRealValue(String value) {
      return value != null && !value.isEmpty() && !value.equals(ALL_INDICES);
    }
  }

  private final class TableSelectionListener implements ChangeListener {
    @SuppressWarnings("unchecked")
    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
      if (newValue != null && newValue instanceof Map) {
        Map row = (Map<String, ? extends Object>) newValue;
        ViewDef viewDef = model.metadata().view(String.valueOf(row.get("_index")), String.valueOf(row.get("_type")));
        if (lastDetailsView == null || lastDetailsView.viewDef() != viewDef) {
          lastDetailsView = viewFactory.view(viewDef);
          details.getChildren().clear();
          Parent root = lastDetailsView.root();
          Display.fillAnchorZero(root.getProperties());
          details.getChildren().add(root);
        }
        lastDetailsView.data(row);
      }
    }
  }

  final class SearchIndexChangeistener implements ChangeListener<Boolean> {
    private String name;

    public SearchIndexChangeistener(String name) {
      this.name = name;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
      model.searchIndex(name, newValue);
      fillTypes();
      searchController.search();
    }
  }

  final class SearchTypeChangeistener implements ChangeListener<Boolean> {
    private String name;

    public SearchTypeChangeistener(String name) {
      this.name = name;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
      model.searchType(name, newValue);
      fillFiedls();
      searchController.search();
    }
  }

  private final static String ALL_INDICES = "[ALL INDICES]";

  @FXML
  private TableView resultTable;
  @FXML
  private ChoiceBox<String> allIndices;
  @FXML
  private VBox indices;
  @FXML
  private VBox types;
  @FXML
  private VBox fields;
  @FXML
  private Label searchStatus;
  @FXML
  private ChoiceBox<Integer> rowCount;
  @FXML
  private TextField address;
  @FXML
  private Label connectStatus;
  @FXML
  private AnchorPane details;
  @FXML
  private TableView<Map<String, Object>> indicesTable;
  @FXML
  private AnchorPane detailsStatus;
  @FXML
  private AnchorPane detailsMetadata;
  @FXML
  private ListView<String> detailsTypes;
  @FXML
  private AnchorPane detailsTypeMapping;

  private Stage stage;

  private Model model;

  private SearchController searchController;
  private OverviewController overviewController;
  private ViewFactory viewFactory;
  private View lastDetailsView = null;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    addSelectedIndexListener();
    addSelectedRowListener();
    resultTable.setFixedCellSize(30.0);
  }

  @SuppressWarnings("unchecked")
  private void addSelectedRowListener() {
    TableViewSelectionModel selectionModel = resultTable.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    viewFactory = new ViewFactory();
    selectionModel.selectedItemProperty().addListener(new TableSelectionListener());
  }

  public void stage(Stage stage) {
    this.stage = stage;
  }

  private void addSelectedIndexListener() {
    allIndices.getSelectionModel().selectedItemProperty().addListener(new AllIndicesChangeListener());
  }

  private void fillIndices() {
    clearChildren(indices);
    ObservableList<Node> children = indices.getChildren();
    if (model.getSearchIndices().isEmpty()) {
      for (String name : Utils.sort(model.metadata().indices())) {
        children.add(createToggleButton(name, new SearchIndexChangeistener(name)));
      }
    }
    fillTypes();
  }

  private void fillTypes() {
    clearChildren(types);
    model.clearSearchTypes();
    ObservableList<Node> children = types.getChildren();
    for (String type : Utils.sort(model.metadata().types(model.getSearchIndices()))) {
      children.add(createToggleButton(type, new SearchTypeChangeistener(type)));
    }
    fillFiedls();
  }

  private void fillFiedls() {
    clearChildren(fields);
    ObservableList<Node> children = fields.getChildren();
    model.clearSearchFields();
    for (Field field : Utils.sort(model.metadata().fields(model.getSearchIndices(), model.getSearchTypes()))) {
      children.add(new SearchBox(field.name(), searchController.new SearchFieldChangeListener(field)));
    }
  }

  private void clearChildren(Pane... panes) {
    for (Pane pane : panes) {
      pane.getChildren().clear();
    }
  }

  private ToggleButton createToggleButton(String name, ChangeListener<Boolean> changeListener) {
    ToggleButton ret = ToggleButtonBuilder.create().id(name).text(name).prefWidth(200.0).build();
    ret.selectedProperty().addListener(changeListener);
    return ret;
  }

  @FXML
  protected void connect() {
    model = new Model();
    URL url = getUrl();
    if (url != null) {
      try {
        model.connect(url);
        if (model.isConnected()) {
          searchController = new SearchController(model, resultTable, searchStatus, rowCount);
          overviewController = new OverviewController(stage, model, indicesTable, detailsTypes, detailsTypeMapping, detailsStatus, detailsMetadata);
          fillAllIndices();
        }
        connectStatus.setText(model.connectionStatus());
      } catch (Exception e) {
        e.printStackTrace();
        changeConnectionStatus(e);
      }
    }
  }

  @FXML
  protected void editMapping() {
    overviewController.editMapping();
  }

  @FXML
  protected void cancelMapping() {
    overviewController.cancelMapping();
  }

  @FXML
  protected void saveMapping() {
    overviewController.saveMapping();
  }

  @FXML
  protected void newAliasIndex() {
    overviewController.newAliasIndex();
  }

  @FXML
  protected void refreshIndex() {
    overviewController.refreshIndex();
  }

  @FXML
  protected void refreshFlush() {
    overviewController.refreshFlush();
  }

  @FXML
  protected void gatewaySnapshot() {
    overviewController.gatewaySnapshot();
  }

  @FXML
  protected void testAnalyzer() {
    overviewController.testAnalyzer();
  }

  @FXML
  protected void closeIndex() {
    overviewController.closeIndex();
  }

  @FXML
  protected void newIndex() {
    overviewController.newIndex();
  }

  @FXML
  protected void deleteIndex() {
    overviewController.deleteIndex();
  }

  private void fillAllIndices() {
    allIndices.getItems().clear();
    allIndices.getItems().add(ALL_INDICES);
    Metadata metadata = model.metadata();
    for (String name : Utils.sort(metadata.indices())) {
      allIndices.getItems().add(name);
      indices.getChildren().add(createToggleButton(name, new SearchIndexChangeistener(name)));
    }
    allIndices.getSelectionModel().select(0);
  }

  private URL getUrl() {
    URL url = null;
    try {
      url = new URL(address.getText());
    } catch (MalformedURLException e) {
      changeConnectionStatus(e);
    }
    return url;
  }

  private void changeConnectionStatus(Exception e) {
    connectStatus.setText(e.getMessage());
  }

  public void close() {
    if (searchController != null) {
      searchController.close();
    }
    if (model != null) {
      model.close();
    }
  }
}
