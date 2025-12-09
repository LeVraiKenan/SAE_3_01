module com.example.tribollo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tribollo to javafx.fxml;
    exports com.example.tribollo;
}