package ee.elastic.ui.fx;

import java.util.List;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ee.elastic.ui.config.ColumnDef;

@SuppressWarnings("rawtypes")
public class MenuBuilder implements Callback<TableColumn, ContextMenu>, ChangeListener<List<ColumnDef>> {
  private ContextMenu menu;
  private EventHandler<ActionEvent> click;
  private List<ColumnDef> columns;
  private Pattern filePathMatcher = Pattern.compile("([A-Za-z]:|((//|\\\\\\\\)[A-Za-z])).*");

  public MenuBuilder(EventHandler<ActionEvent> click) {
    super();
    this.click = click;
  }

  @Override
  public ContextMenu call(TableColumn column) {
    if (menu == null) {
      build();
    }
    return menu;
  }

  private void build() {
    // Create a context menu
    menu = new ContextMenu();
    for (ColumnDef columnDef : columns) {
      //if (isFilePath(columnDef.label())) {
      MenuItem item = new MenuItem(columnDef.label());
      item.setOnAction(click);
      menu.getItems().addAll(item);
      //}
    }
  }

  @Override
  public void changed(ObservableValue<? extends List<ColumnDef>> value, List<ColumnDef> oldColumns, List<ColumnDef> columns) {
    this.columns = columns;
    menu = null;
  }

  private boolean isFilePath(String stringValue) {
    return filePathMatcher.matcher(stringValue).matches();
  }
}
