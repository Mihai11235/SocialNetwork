module com.example.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.socialnetwork to javafx.fxml;
    exports com.example.socialnetwork;

    exports com.example.socialnetwork.controllers;
    opens com.example.socialnetwork.controllers to javafx.fxml;

    opens com.example.socialnetwork.domain to javafx.base;
}