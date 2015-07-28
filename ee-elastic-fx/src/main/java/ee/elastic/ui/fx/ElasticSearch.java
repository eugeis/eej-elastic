package ee.elastic.ui.fx;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ElasticSearch extends Application {
	private Stage stage;
	private ElasticSearchController controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Elasticsearch Browser");
		stage = primaryStage;
		replaceSceneContent("/elasticsearch.fxml");
		primaryStage.show();
	}

	private Parent replaceSceneContent(String fxml) throws Exception {
		URL location = getClass().getResource(fxml);

		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(location);
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
		Parent page = (Parent) fxmlLoader.load(location.openStream());
		controller = (ElasticSearchController) fxmlLoader.getController();
		controller.stage(stage);
		Scene scene = stage.getScene();
		if (scene == null) {
			scene = new Scene(page, 800, 600);
			stage.setScene(scene);
		} else {
			scene.setRoot(page);
		}
		stage.sizeToScene();
		return page;
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		controller.close();
	}

	public static void main(String[] args) {
		launch(args);
	}

}