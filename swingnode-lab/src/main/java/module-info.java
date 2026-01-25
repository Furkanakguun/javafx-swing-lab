module org.example.swingnodelab {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.swingnodelab to javafx.fxml;
    exports org.example.swingnodelab;
}