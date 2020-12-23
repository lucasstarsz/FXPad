package org.lucasstarsz.fxpad;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.lucasstarsz.fxpad.utils.DialogUtil;

import java.io.IOException;

public class RTFXMain extends Application {

    public static final String title = "FXPad SNAPSHOT.3";
    public static final String mainFXMLPath = "rtfxmain.fxml";
    public static final String baseStylePath = "/styles/base.css";
    public static final String themeStylePath = "/styles/themes/darcpad.css";
    public static final String iconPath = "/icons/fxpad_png.png";

    private static Stage mainStage;

    private static String fileToOpen;

    public static Stage getStage() {
        return mainStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;

        FXMLLoader fxml = new FXMLLoader(getClass().getResource(mainFXMLPath));
        Scene scene = new Scene(fxml.load(), 800, 600);

        String baseStyle = getClass().getResource(baseStylePath).toExternalForm();
        String themeStyle = getClass().getResource(themeStylePath).toExternalForm();

        scene.getStylesheets().addAll(baseStyle, themeStyle);

        mainStage.setScene(scene);
        mainStage.setTitle("Untitled.txt | " + title);
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));
        mainStage.setMinWidth(400);
        mainStage.setMinHeight(150);

        Platform.setImplicitExit(false);
        mainStage.setOnCloseRequest(event -> {
            RTFXController c = fxml.getController();
            if (!c.isUnsaved() || DialogUtil.confirmUnsavedChanges(c.getCurrentFileTitle(), "close FXPad")) {
                Platform.exit();
            } else {
                event.consume();
            }
        });

        System.gc();
        mainStage.show();

        if (fileToOpen != null) ((RTFXController) fxml.getController()).tryOpen(fileToOpen);
    }

    public static void main(String[] args) {
        if (args.length > 0) fileToOpen = args[0];
        launch(args);
    }
}
