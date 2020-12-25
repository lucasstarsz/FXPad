package org.lucasstarsz.fxpad;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import org.lucasstarsz.fxpad.utils.PropertiesUtil;

import java.io.IOException;
import java.util.Map;

public class SettingsController {

    @FXML private ComboBox<String> themeSelector;
    private final Map<String, String> themes = Map.of(
            "Light", RTFXMain.lightStylePath,
            "Fresh", RTFXMain.freshStylePath,
            "Darc", RTFXMain.darcStylePath
    );

    @FXML
    private void setTheme() throws IOException {
        String selection = themeSelector.getSelectionModel().getSelectedItem();
        if (themes.containsKey(selection) && !selection.equals(RTFXMain.getCurrentTheme())) {
            changeTheme(themes.get(selection));
            RTFXMain.setCurrentTheme(selection);
        }
    }

    private void changeTheme(String themeResource) throws IOException {
        Scene currentScene = RTFXMain.getStage().getScene();
        String baseStyle = getClass().getResource(RTFXMain.baseStylePath).toExternalForm();
        currentScene.getStylesheets().clear();
        currentScene.getStylesheets().addAll(baseStyle, getClass().getResource(themeResource).toExternalForm());
        saveTheme(themeResource);
    }

    private void saveTheme(String themeResource) throws IOException {
        PropertiesUtil.set(RTFXMain.userPropertiesPath, "theme", themeResource);
    }
}
