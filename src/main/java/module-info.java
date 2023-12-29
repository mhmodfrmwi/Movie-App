module com.example.movieapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires org.json;
    requires java.sql;
    opens com.example.movieapp to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.movieapp;

}