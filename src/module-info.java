module fxpad {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;

    opens fxpad.controller to javafx.fxml;

    exports fxpad;
}