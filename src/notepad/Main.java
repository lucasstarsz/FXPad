package notepad;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import notepad.controller.Controller;

public class Main extends Application {

    public static final String title = "FXPad";

    private static Stage mainStage;

    public static Stage getStage() {
        return mainStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;

        FXMLLoader fxml = new FXMLLoader(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(fxml.load(), 800, 600);
        String stylesheet = getClass().getResource("/styles/style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        mainStage.setScene(scene);
        mainStage.setTitle("Untitled.txt | " + title);

        Platform.setImplicitExit(false);
        mainStage.setOnCloseRequest(event -> {
            Controller c = fxml.getController();

            if (!c.isUnsaved() || c.confirmUnsavedChanges("close FXPad")) {
                Platform.exit();
            } else {
                event.consume();
            }
        });

        System.gc();

        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
