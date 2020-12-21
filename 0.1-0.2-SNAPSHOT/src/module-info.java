module fxpad {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;
    requires org.fxmisc.richtext;
    requires flowless;
    requires reactfx;
    opens fxpad to javafx.fxml;
    opens fxpadrtfx to javafx.fxml;

    exports fxpad;
    exports fxpadrtfx;
}