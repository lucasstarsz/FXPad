module formatfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires flowless;
    requires java.desktop;

    opens org.lucasstarsz.fxpad to javafx.fxml;
    opens org.lucasstarsz.fxpad.nodes to javafx.fxml;
    exports org.lucasstarsz.fxpad;
}
