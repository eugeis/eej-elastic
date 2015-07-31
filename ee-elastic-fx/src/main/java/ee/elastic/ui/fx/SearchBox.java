package ee.elastic.ui.fx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

public class SearchBox extends Region {

  private final class SearchBoxListenerDelegate implements ChangeListener<String> {
    private ChangeListener<String> changeListener;

    private SearchBoxListenerDelegate(ChangeListener<String> changeListener) {
      this.changeListener = changeListener;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      clearButton.setVisible(textBox.getText().length() != 0);
      changeListener.changed(observable, oldValue, newValue);
    }
  }

  private TextField textBox;
  private Button clearButton;

  public SearchBox(String name, ChangeListener<String> changeListener) {
    setId("search-" + name);
    getStyleClass().add("search-box");
    setMinHeight(24);
    setPrefSize(200, 24);
    setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    textBox = new TextField();
    textBox.setPromptText(name);
    clearButton = new Button();
    clearButton.setVisible(false);
    getChildren().addAll(textBox, clearButton);
    clearButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        textBox.setText("");
        textBox.requestFocus();
      }
    });
    textBox.textProperty().addListener(new SearchBoxListenerDelegate(changeListener));
  }

  @Override
  protected void layoutChildren() {
    textBox.resize(getWidth(), getHeight());
    clearButton.resizeRelocate(getWidth() - 18, 6, 12, 13);
  }
}