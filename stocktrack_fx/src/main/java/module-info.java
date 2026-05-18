module com.stocktrack {
    requires javafx.controls;
    requires javafx.fxml;
    opens com.stocktrack to javafx.fxml;
    opens com.stocktrack.controllers to javafx.fxml;
    exports com.stocktrack;
    exports com.stocktrack.controllers;
    exports com.stocktrack.models;
    exports com.stocktrack.services;
}
