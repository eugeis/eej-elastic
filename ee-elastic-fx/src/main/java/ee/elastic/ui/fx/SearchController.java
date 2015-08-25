package ee.elastic.ui.fx;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.elasticsearch.action.search.SearchResponse;
import ee.elastic.ui.config.ColumnDef;
import ee.elastic.ui.config.Field;
import ee.elastic.ui.converter.ConverterFactory;
import ee.elastic.ui.core.ColumnsValuesCache;
import ee.elastic.ui.core.Model;
import ee.elastic.ui.core.Sort;
import ee.elastic.ui.core.TableModelByColumns;
import ee.elastic.ui.integ.DelayExecutor;
import ee.elastic.ui.integ.Func;
import ee.elastic.ui.integ.Utils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SearchController {

  InvalidationListener columnSortableObserver = new InvalidationListener() {

    @Override
    public void invalidated(Observable observable) {
      updateSortOrder();
    }
  };

  InvalidationListener columnSortTypeObserver = new InvalidationListener() {

    @Override
    public void invalidated(Observable observable) {
      updateSortOrder();
    }
  };

  WeakInvalidationListener weakColumnSortableObserver = new WeakInvalidationListener(columnSortableObserver);
  WeakInvalidationListener weakColumnSortTypeObserver = new WeakInvalidationListener(columnSortTypeObserver);

  private final class CellFactoryBuilder implements Func<ColumnDef, Callback<TableColumn, TableCell>> {
    private GenericCellFactory call;

    public CellFactoryBuilder(final TableView tableView, final TableModelByColumns tableModel) {
      super();

      // Create a click event that looks for double clicks
      EventHandler click = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
          if (t.getClickCount() == 2) {
            TableCell cell = (TableCell) t.getSource();
            Object item = tableView.getItems().get(cell.getIndex());
            open(item, cell);
          }
        }
      };
      MenuBuilder menuBuilder = new MenuBuilder(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
          Object cell = t.getSource();
          Object item = tableView.getSelectionModel().getSelectedItem();
          open(item, null);
        }
      });
      tableModel.addListener(menuBuilder);
      call = new GenericCellFactory(click, menuBuilder);
    }

    private void open(Object item, TableCell cell) {
      System.out.println(item);
    }

    @Override
    public Callback<TableColumn, TableCell> call(ColumnDef columnDef) {
      return call;
    }
  }

  private final class TableColumnBuilder implements Func<ColumnDef, TableColumn> {
    CellFactoryBuilder cellFactoryBuilder;

    public TableColumnBuilder(TableView tableView, TableModelByColumns tableModel) {
      super();
      this.cellFactoryBuilder = new CellFactoryBuilder(tableView, tableModel);
    }

    @Override
    public TableColumn call(ColumnDef columnDef) {
      TableColumn column = new TableColumn();

      column.setText(columnDef.label());
      column.setMinWidth(10);
      column.sortableProperty().addListener(weakColumnSortableObserver);
      column.sortTypeProperty().addListener(weakColumnSortTypeObserver);
      column.setCellValueFactory(new ColumnValueFactory(columnDef, converters.converter(columnDef), columnsValuesCache));
      column.setCellFactory(cellFactoryBuilder.call(columnDef));
      return column;
    }
  }

  private final class SortOrderColumnBuilder implements Func<TableColumn, Sort<String, SortType>> {
    @Override
    public Sort<String, SortType> call(TableColumn column) {
      SortType order = (SortType) column.sortTypeProperty().get();
      return new Sort<String, SortType>(column.getText(), order);
    }
  }

  private final class ColumnTextSelector implements Func<TableColumn, Boolean> {
    private String text;

    public ColumnTextSelector(String text) {
      super();
      this.text = text;
    }

    @Override
    public Boolean call(TableColumn column) {
      return text.equals(column.getText());
    }
  }

  final class SearchFieldChangeListener implements ChangeListener<String> {
    private Field field;

    public SearchFieldChangeListener(Field field) {
      this.field = field;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      model.searchWildcard(field, newValue);
      search();
    }
  }

  private final SortOrderColumnBuilder sortOrderColumnBuilder = new SortOrderColumnBuilder();
  private TableColumnBuilder tableColumnBuilder;

  private Model model;
  private TableModelByColumns tableModel;
  private ConverterFactory converters;
  private TableView resultTable;
  private Label searchStatus;
  private ColumnsValuesCache columnsValuesCache;
  private boolean updating = false;
  private boolean sorting = false;

  private DelayExecutor searchCommand;

  public SearchController(Model model, TableView resultTable, Label searchStatus, ChoiceBox<Integer> rowCount) {
    super();
    columnsValuesCache = new ColumnsValuesCache();
    this.model = model;
    this.resultTable = resultTable;

    addSortOrderListener();

    this.searchStatus = searchStatus;
    converters = new ConverterFactory();
    createSearchDeleayCommand();
    tableModel = new TableModelByColumns(model.metadata());
    tableColumnBuilder = new TableColumnBuilder(resultTable, tableModel);

    addRowCountListener(rowCount);
  }

  private void addSortOrderListener() {
    resultTable.getSortOrder().addListener(new ListChangeListener<TableColumn>() {
      @Override
      public void onChanged(javafx.collections.ListChangeListener.Change<? extends TableColumn> change) {
        updateSortOrder();
      }
    });
  }

  private void addRowCountListener(ChoiceBox<Integer> rowCountControl) {
    // rowCountControl.setConverter(new IntegerStringConverter());
    rowCountControl.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
      @Override
      public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        model.rowCount(newValue.intValue());
        search();
      }
    });
  }

  private void updateSortOrder() {
    sorting = true;
    //		model.sortOrder(Utils.transform(resultTable.getSortOrder(),
    //				new Func<TableColumn, Sort>() {
    //					@Override
    //					public Sort call(TableColumn source) {
    //						ColumnDef columnDef = ((ColumnValueFactory) source
    //								.getCellValueFactory()).getColumn();
    //						return new Sort(
    //								columnDef.sortField(),
    //								source.sortTypeProperty().get() == SortType.ASCENDING ? SortOrder.ASC
    //										: SortOrder.DESC);
    //					}
    //				}));
    search();
  }

  private void createSearchDeleayCommand() {
    searchCommand = new DelayExecutor(new Runnable() {
      @Override
      public void run() {
        doSearch();
      }
    }, 700, TimeUnit.MILLISECONDS);
  }

  protected void doSearch() {
    if (updating) {
      return;
    }
    updating = true;
    final SearchResponse response = model.search();
    tableModel.changeModelSource(response, model.loadParents(response));
    final ObservableList<Map<String, ?>> data = FXCollections.observableArrayList(tableModel.getItems());

    Platform.runLater(new Runnable() {

      @Override
      public void run() {
        updateTable(data);
        updateSearchStatusLabel(response);
      }

      private void updateSearchStatusLabel(final SearchResponse response) {
        String status = String.format("Searched %s of %s shards, %s hits, %s seconds.", response.getSuccessfulShards(), response.getTotalShards(), response.getHits().getTotalHits(), response.getTook().getSecondsFrac());
        searchStatus.setText(status);
      }

      private void updateTable(final ObservableList<Map<String, ?>> data) {
        columnsValuesCache.clear();

        ArrayList<Sort<String, SortType>> sortOrder = null;
        if (sorting) {
          sortOrder = Utils.transform(resultTable.getSortOrder(), sortOrderColumnBuilder);
        }
        resultTable.getItems().clear();
        if (tableModel.isColumnChanged()) {
          resultTable.getColumns().clear();
          resultTable.getColumns().addAll(Utils.transform(tableModel.getColumns(), tableColumnBuilder));
        }
        resultTable.setItems(data);
        if (sorting && sortOrder != null) {
          for (Sort<String, SortType> sort : sortOrder) {
            TableColumn column = Utils.findFirst(resultTable.getColumns(), new ColumnTextSelector(sort.element()));
            if (column != null) {
              column.sortTypeProperty().set(sort.order());
            }
          }
          sorting = false;
        }
        updating = false;
      }
    });
  }

  public void close() {
    searchCommand.close();
  }

  public void search() {
    searchCommand.trigger();
  }
}
