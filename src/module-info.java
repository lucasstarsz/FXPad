module Notepad {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;

    opens notepad.controller to javafx.fxml;

    exports notepad.controller;
    exports notepad;
}