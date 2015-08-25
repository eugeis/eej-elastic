package ee.elastic.ui.fx;

import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ee.elastic.ui.core.View;

public class Display {
  public static Stage open(View<?, ?> view, Object parentWindow, Modality modality) {
    Stage stage = new Stage();
    // Initialize the Stage with type of modal
    if (modality != null) {
      stage.initModality(modality);
    }
    // Set the owner of the Stage
    if (parentWindow != null && parentWindow instanceof Window) {
      stage.initOwner((Window) parentWindow);
    }
    stage.setTitle("Details");

    Scene scene = new Scene(view.root(), 600, 400, Color.TRANSPARENT);

    stage.setScene(scene);
    stage.sizeToScene();

    stage.show();
    return stage;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void fillAnchorZero(ObservableMap properties) {
    properties.put("pane-right-anchor", 0.0);
    properties.put("pane-left-anchor", 0.0);
    properties.put("pane-bottom-anchor", 0.0);
    properties.put("pane-top-anchor", 0.0);
  }
}
