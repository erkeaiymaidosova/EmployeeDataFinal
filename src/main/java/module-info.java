module com.example.databasefinal {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.databasefinal to javafx.fxml;
    exports com.example.databasefinal;
}