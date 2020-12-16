module Notepad {
    requires javafx.fxml;
    requires javafx.controls;

    opens notepad to javafx.fxml;

    exports notepad;
}