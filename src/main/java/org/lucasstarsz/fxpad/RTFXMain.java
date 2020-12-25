package org.lucasstarsz.fxpad;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.lucasstarsz.fxpad.utils.DialogUtil;
import org.lucasstarsz.fxpad.utils.PropertiesUtil;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class RTFXMain extends Application {

    public static final String title = "FXPad SNAPSHOT.4";
    public static final String mainFXMLPath = "rtfxmain.fxml";
    public static final String iconPath = "/icons/fxpad_png.png";
    public static final String userDir = System.getProperty("user.dir") + File.separator;
    public static final String userPropertiesPath = userDir + String.join(File.separator, "preferences", "prefs.properties");

    public static final String baseStylePath = "/styles/base.css";
    public static final String lightStylePath = "/styles/themes/light.css";
    public static final String darcStylePath = "/styles/themes/darc.css";
    public static final String freshStylePath = "/styles/themes/fresh.css";

    private static String currentTheme = "";

    private static Stage mainStage;

    private static String fileToOpen;

    public static Stage getStage() {
        return mainStage;
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(String newTheme) {
        RTFXMain.currentTheme = newTheme;
    }

    // TODO: organize this
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;

        FXMLLoader fxml = new FXMLLoader(getClass().getResource(mainFXMLPath));
        Scene scene = new Scene(fxml.load(), 800, 590);

        String baseStyle = getClass().getResource(baseStylePath).toExternalForm();
        String themeStyle = getClass().getResource(loadPreferences()).toExternalForm();

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

    private String loadPreferences() throws IOException {
        Properties prefs = PropertiesUtil.get(userPropertiesPath);
        if (prefs.getProperty("theme") != null) return prefs.getProperty("theme");

        PropertiesUtil.set(userPropertiesPath, "theme", lightStylePath);
        prefs = PropertiesUtil.get(userPropertiesPath);

        return prefs.getProperty("theme");
    }

    public static void main(String[] args) {
        if (args.length > 0) fileToOpen = args[0];
        launch(args);
    }
}
