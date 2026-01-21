module org.example.javafxlab {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens org.example.javafxlab to javafx.fxml;
    exports org.example.javafxlab;
}