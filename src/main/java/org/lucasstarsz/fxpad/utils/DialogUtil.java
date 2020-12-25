package org.lucasstarsz.fxpad.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.lucasstarsz.fxpad.RTFXMain;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class for working with {@link Dialog}s, and their {@link Alert} subclass.
 *
 * @author andrewrcdey
 */
public class DialogUtil {

    private static final String issueForm = "https://github.com/lucasstarsz/FXPad/issues/new?assignees=lucasstarsz&labels=crash&template=fxpad-crashed.md&title=%5BCRASH+REPORT%5D";

    /**
     * Confirms that the user wants to overwrite the file they're currently editing.
     *
     * @param possibleAction The action to be completed.
     * @return The user's decision, to either proceed or cancel the operation.
     */
    public static boolean confirmUnsavedChanges(String fileTitle, String possibleAction) {
        Alert confirmUnsavedAlert = createWarningAlert();

        confirmUnsavedAlert.setTitle("Unsaved Changes");
        confirmUnsavedAlert.setHeaderText("");
        confirmUnsavedAlert.setContentText(
                "You have unsaved changes in " + fileTitle + ".\nAre you sure you want to " + possibleAction + "?"
        );

        return checkConfirmation(confirmUnsavedAlert);
    }

    /**
     * Confirms that the user wants to open a file with an unsupported encoding.
     *
     * @param file The file to possibly open.
     * @return The user's decision, to either proceed or cancel the operation.
     */
    public static boolean confirmOpenUnsupported(File file) {
        Alert confirmOpenAlert = createWarningAlert();

        confirmOpenAlert.setTitle("Unsupported File");
        confirmOpenAlert.setHeaderText("");
        confirmOpenAlert.setContentText("The file at " + file.getAbsolutePath() + " contains unsupported text encoding. Do you want to open it anyways?");

        return checkConfirmation(confirmOpenAlert);
    }

    /**
     * Show error dialogue, displaying failure to open the file at the specified path.
     *
     * @param fileName The path of the file that failed to open.
     */
    public static void cantOpenFile(String fileName) {
        Alert cantOpenFileAlert = createErrorAlert();

        cantOpenFileAlert.setTitle("Can't open file");
        cantOpenFileAlert.setContentText("Sorry, I couldn't open the file at " + fileName + ".");

        cantOpenFileAlert.showAndWait();
    }

    public static void massiveFailure(Exception e) {
        System.gc();
        Throwable error = e.fillInStackTrace();

        Alert errorAlert = createErrorAlert();
        errorAlert.setTitle("FXPad Error");
        errorAlert.setContentText("Hm... it seems FXPad ran into an issue: " + error.toString());

        Map.Entry<VBox, TextArea> exceptionContainer = createExceptionContainer(e);

        Button createIssueLink = new Button("Let the developer know");
        createIssueLink.setOnAction(event -> {
            exceptionContainer.getValue().selectAll();
            exceptionContainer.getValue().copy();
            exceptionContainer.getValue().deselect();

            try {
                Desktop.getDesktop().browse(new URI(issueForm));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        });

        exceptionContainer.getKey().getChildren().add(createIssueLink);
        errorAlert.getDialogPane().setExpandableContent(exceptionContainer.getKey());
        errorAlert.showAndWait();
    }

    /**
     * Creates a warning alert with {@link ButtonType}{@code .NO} and {@link ButtonType}{@code .YES}.
     *
     * @return The newly created warning alert.
     */
    private static Alert createWarningAlert() {
        Alert confirmationAlert = new Alert(Alert.AlertType.WARNING);
        confirmationAlert.initModality(Modality.APPLICATION_MODAL);
        confirmationAlert.initOwner(RTFXMain.getStage());

        confirmationAlert.getButtonTypes().clear();
        confirmationAlert.getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
        return confirmationAlert;
    }

    /**
     * Creates an error alert with default button implementation.
     *
     * @return The newly created error alert.
     */
    private static Alert createErrorAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.initModality(Modality.APPLICATION_MODAL);
        errorAlert.initOwner(RTFXMain.getStage());
        errorAlert.setHeaderText("");

        return errorAlert;
    }

    public static Dialog<Node> createEmptyDialog() {
        Dialog<Node> result = new Dialog<>();
        result.initModality(Modality.APPLICATION_MODAL);
        result.initOwner(RTFXMain.getStage());

        Window resultWindow = result.getDialogPane().getScene().getWindow();
        resultWindow.setOnCloseRequest(event -> resultWindow.hide());

        return result;
    }

    private static Map.Entry<VBox, TextArea> createExceptionContainer(Exception e) {
        return createExceptionContainer(e, new Insets(5));
    }

    private static Map.Entry<VBox, TextArea> createExceptionContainer(Exception e, Insets padding) {
        VBox container = new VBox();
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(padding);

        String[] errorLog = Arrays.stream(e.getStackTrace())
                .map(Object::toString)
                .toArray(String[]::new);

        TextArea area = new TextArea(
                "Exception: " + e.toString()
                        + System.lineSeparator()
                        + System.lineSeparator()
                        + "at: " + String.join("\nat: ", errorLog)
        );
        area.setWrapText(false);
        area.setEditable(false);
        VBox.setVgrow(area, Priority.SOMETIMES);
        container.getChildren().add(area);

        return Map.entry(container, area);
    }

    /**
     * Checks user decision on {@link Alert} of alert type {@link Alert.AlertType}{@code .WARNING}.
     *
     * @param confirmationAlert The {@link Alert} to check.
     * @return The user's decision, to either proceed or cancel the operation.
     */
    private static boolean checkConfirmation(Alert confirmationAlert) {
        assert confirmationAlert.getAlertType() == Alert.AlertType.WARNING;
        AtomicReference<Boolean> confirmation = new AtomicReference<>(false);
        Toolkit.getDefaultToolkit().beep();

        confirmationAlert.showAndWait().ifPresent((button) -> {
            if (button == ButtonType.YES) {
                confirmation.set(true);
            } else if (button == ButtonType.NO) {
                confirmation.set(false);
            }
        });

        return confirmation.get();
    }
}
