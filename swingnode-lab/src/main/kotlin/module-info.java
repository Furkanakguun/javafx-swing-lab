module org.example.swingnodelab {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens org.example.swingnodelab to javafx.fxml;
    exports org.example.swingnodelab;
}