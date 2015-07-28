package ee.elastic.ui.examples.two;

import java.util.Locale;


import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SampleApp extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        VBox root = new VBox(4);
        root.setStyle("-fx-padding: 10");

        final DatePicker datePicker = new DatePicker();
        datePicker.localeProperty().set(Locale.US);


        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        hBox.getChildren().add(new Label("Date:"));
        hBox.getChildren().add(datePicker);
        root.getChildren().add(hBox);
        datePicker.selectedDateProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                System.out.println(datePicker.selectedDateProperty().get());
            }
        });

        datePicker.setLocale(Locale.GERMAN);
        datePicker.getCalendarView().todayButtonTextProperty().set("Heute");
        datePicker.getCalendarView().setShowWeeks(false);
        //datePicker.getCalendarView().calendarProperty().set(new BuddhistCalendar());
        //datePicker.getCalendarView().getDisabledWeekdays().addAll(Calendar.WEDNESDAY);

        Scene scene = new Scene(root, 400, 350);
        scene.getStylesheets().add("calendarstyle.css");


        stage.setScene(scene);
        stage.show();
    }
}
