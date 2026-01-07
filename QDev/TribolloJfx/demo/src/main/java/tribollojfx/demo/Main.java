package tribollojfx.demo;

import javafx.application.Application;
import javafx.stage.Stage;
import tribollojfx.controller.MainController;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainController app = new MainController(primaryStage);
        app.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}