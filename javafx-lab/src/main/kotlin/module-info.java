module org.example.javafxlab {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;
    requires jdk.management;
    requires kotlin.stdlib;


    opens org.example.javafxlab to javafx.fxml;
    exports org.example.javafxlab;
}