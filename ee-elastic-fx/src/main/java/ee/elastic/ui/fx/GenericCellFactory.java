package ee.elastic.ui.fx;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

@SuppressWarnings("rawtypes")
public class GenericCellFactory implements Callback<TableColumn, TableCell> {

  protected Callback<TableColumn, ContextMenu> menuBuilder;
  protected EventHandler click;

  public GenericCellFactory(EventHandler click, Callback<TableColumn, ContextMenu> menuBuilder) {
    this.menuBuilder = menuBuilder;
    this.click = click;
  }

  @Override
  @SuppressWarnings("unchecked")
  public TableCell call(TableColumn column) {
    TableCell cell = new TableCell() {
      @Override
      protected void updateItem(Object item, boolean empty) {
        // calling super here is very important - don't skip this!
        super.updateItem(item, empty);
        if (item != null) {
          setText(item.toString());
        }
      }
    };

    // Right click
    ContextMenu menu = menuBuilder.call(column);
    if (menu != null) {
      cell.setContextMenu(menu);
    }
    // Double click
    if (click != null) {
      cell.setOnMouseClicked(click);
    }

    return cell;
  }
}