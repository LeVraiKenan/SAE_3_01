module tribollojfx.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens tribollojfx.demo to javafx.fxml;
    exports tribollojfx.demo;
}