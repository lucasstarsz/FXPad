package fxpad;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import fxpad.controller.Controller;

public class Main extends Application {

    public static final String title = "FXPad";

    private static Stage mainStage;

    private static String fileToOpen;

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
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/fxpad_png.png")));

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

        if (fileToOpen != null) ((Controller) fxml.getController()).tryOpen(fileToOpen);
    }

    public static void main(String[] args) {
        if (args.length > 0) fileToOpen = args[0];
        launch(args);
    }
}
