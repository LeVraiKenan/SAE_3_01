module tribollojfx.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens tribollojfx.demo to javafx.fxml;
    exports tribollojfx.demo;
    exports tribollojfx.model;
    opens tribollojfx.model to javafx.fxml;
    exports tribollojfx.controller;
    opens tribollojfx.controller to javafx.fxml;
    exports tribollojfx.view.components;
    opens tribollojfx.view.components to javafx.fxml;
}