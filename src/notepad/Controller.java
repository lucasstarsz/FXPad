package notepad;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class Controller {

    private static final FileChooser.ExtensionFilter textFileFilter = new FileChooser.ExtensionFilter("Text File (.txt)", "*.txt");
    private static final FileChooser.ExtensionFilter allFilesFiler = new FileChooser.ExtensionFilter("All Files", "*.*");

    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem saveAsMenuItem;

    @FXML private TextArea textArea;

    private File currentFile;
    private String currentFileContents = "";
    private String currentFileTitle = "Untitled.txt";
    private boolean isModified = false;

    @FXML
    private void initialize() {
        KeyCombination shortcutO = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.SHORTCUT_DOWN);
        KeyCombination shortcutS = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN);
        KeyCombination shortcutShiftS = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN, KeyCodeCombination.SHIFT_DOWN);

        openMenuItem.setAccelerator(shortcutO);
        saveMenuItem.setAccelerator(shortcutS);
        saveAsMenuItem.setAccelerator(shortcutShiftS);

        textArea.setOnKeyPressed((event) -> checkIfModified());
    }

    @FXML
    private void saveFile() throws IOException {
        if (currentFile == null || currentFile.createNewFile()) {
            saveFileAs();
            return;
        }

        write(currentFile);
        setCurrentFile(currentFile);
    }

    @FXML
    private void saveFileAs() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(textFileFilter, allFilesFiler);

        File file = chooser.showSaveDialog(Main.getStage());
        if (file != null) {
            write(file);
            setCurrentFile(file);
        }
    }

    @FXML
    private void openFile() throws IOException {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(Main.getStage());

        if (file != null) {
            open(file);
            setCurrentFile(file);
        }
    }

    /**
     * Reads the content of the specified file and replaces the current content of the editor.
     *
     * @param file The file to read content from.
     */
    private void open(File file) throws IOException {
        textArea.setText(Files.readString(file.toPath()));
    }

    /**
     * Writes the contents of the editor to the specified file.
     *
     * @param file The file to write to.
     */
    private void write(File file) throws IOException {
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile())) {
            fw.write(textArea.getText());
        }
    }

    /** Checks if the current file's contents have been modified. */
    private void checkIfModified() {
        boolean contentModified = !currentFileContents.equals(textArea.getText());
        if (isModified && (!textArea.isUndoable() || !contentModified)) {
            isModified = false;
            Main.getStage().setTitle(currentFileTitle + " | " + Main.title);
        } else if (!isModified && contentModified) {
            isModified = true;
            Main.getStage().setTitle(currentFileTitle + "* | " + Main.title);
        }
    }

    /**
     * Sets the editor's current file to the file specified.
     *
     * @param file The file to set the current file to.
     */
    private void setCurrentFile(File file) {
        isModified = false;

        currentFile = file;
        currentFileContents = textArea.getText();
        currentFileTitle = file.getName();

        Main.getStage().setTitle(currentFile.getName() + " | " + Main.title);
    }
}
