package notepad.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import notepad.Main;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Controller {

    private static final FileChooser.ExtensionFilter textFileFilter = new FileChooser.ExtensionFilter("Text File (.txt)", "*.txt");
    private static final FileChooser.ExtensionFilter allFilesFiler = new FileChooser.ExtensionFilter("All Files", "*.*");

    // File Menu
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem saveAsMenuItem;

    // Edit Menu
    @FXML private MenuItem undoMenuItem;
    @FXML private MenuItem redoMenuItem;
    @FXML private MenuItem copyMenuItem;
    @FXML private MenuItem cutMenuItem;
    @FXML private MenuItem pasteMenuItem;
    @FXML private MenuItem selectAllMenuItem;

    @FXML private MenuItem indentLeftMenuItem;
    @FXML private MenuItem indentRightMenuItem;

    @FXML private TextArea textArea;

    private File currentFile;
    private String currentFileContents = "";
    private String currentFileTitle = "Untitled.txt";
    private boolean unsavedContent = false;

    @FXML
    private void initialize() {
        Map<MenuItem, KeyCombination> editMenuMnemonics = Map.of(
                undoMenuItem, new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.SHORTCUT_DOWN),
                redoMenuItem, new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.SHORTCUT_DOWN),
                copyMenuItem, new KeyCodeCombination(KeyCode.C, KeyCodeCombination.SHORTCUT_DOWN),
                cutMenuItem, new KeyCodeCombination(KeyCode.X, KeyCodeCombination.SHORTCUT_DOWN),
                pasteMenuItem, new KeyCodeCombination(KeyCode.V, KeyCodeCombination.SHORTCUT_DOWN),
                selectAllMenuItem, new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN),

                indentLeftMenuItem, new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHIFT_DOWN),
                indentRightMenuItem, new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN)
        );

        Map<MenuItem, KeyCombination> fileMenuMnemonics = Map.of(
                openMenuItem, new KeyCodeCombination(KeyCode.O, KeyCodeCombination.SHORTCUT_DOWN),
                saveMenuItem, new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN),
                saveAsMenuItem, new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN, KeyCodeCombination.SHIFT_DOWN)
        );

        editMenuMnemonics.forEach(MenuItem::setAccelerator);
        fileMenuMnemonics.forEach(MenuItem::setAccelerator);

        textArea.setOnKeyTyped(event -> checkIfModified());
        textArea.setOnKeyPressed(this::keepTabs);
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
            if (!unsavedContent || confirmUnsavedChanges("open " + file.getName())) {
                open(file);
                setCurrentFile(file);
            }
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

        if (unsavedContent && !textArea.isUndoable()) {
            unsavedContent = false;
            Main.getStage().setTitle(currentFileTitle + " | " + Main.title);
        } else if (!unsavedContent && contentModified) {
            unsavedContent = true;
            Main.getStage().setTitle(currentFileTitle + "* | " + Main.title);
        }
    }

    private void keepTabs(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Map.Entry<String, int[]> currLine = getCurrentLine(textArea);
            int caretPos = textArea.getCaretPosition() - 1;
            int tabCount = 0;

            for (char c : currLine.getKey().toCharArray()) {
                if (c == '\t') tabCount++;
                else break;
            }

            textArea.replaceText(caretPos, caretPos + 1, "\n" + "\t".repeat(tabCount));
            textArea.positionCaret(currLine.getValue()[1] + tabCount + 1);
        }
    }

    private void shift(int shiftAmount) {
        if (textArea.getSelectedText().isEmpty()) {
            Map.Entry<String, int[]> currLine = getCurrentLine(textArea);

            if (currLine.getKey().startsWith("\t") || shiftAmount > 0) {
                int newCaretPos = textArea.getCaretPosition() + shiftAmount;

                if (shiftAmount > 0) {
                    textArea.replaceText(currLine.getValue()[0], currLine.getValue()[1], "\t".repeat(shiftAmount) + currLine.getKey());
                } else if (shiftAmount < 0) {
                    textArea.replaceText(currLine.getValue()[0], currLine.getValue()[1], currLine.getKey().substring(Math.abs(shiftAmount)));
                }

                textArea.positionCaret(newCaretPos);
            }
        } else {
            int selectionStart = textArea.getSelection().getStart();
            int selectionEnd = textArea.getSelection().getEnd();

            int fullSelectionStart = textArea.getText().lastIndexOf('\n', selectionStart) + 1;
            int fullSelectionEnd = textArea.getText().indexOf('\n', selectionEnd);

            if (textArea.getSelectedText().startsWith("\n")) {
                fullSelectionStart = textArea.getText().lastIndexOf('\n', selectionStart + 1) + 1;
            }
            if (fullSelectionEnd == -1) fullSelectionEnd = textArea.getLength();

            String fullSelection = textArea.getText(fullSelectionStart, fullSelectionEnd);
            String[] lines = fullSelection.split("\n");

            int totalTabMovement = 0;
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith("\t") || shiftAmount > 0) {
                    if (shiftAmount > 0) {
                        lines[i] = "\t".repeat(shiftAmount) + lines[i];
                        totalTabMovement += shiftAmount;
                    } else if (shiftAmount < 0) {
                        lines[i] = lines[i].substring(Math.abs(shiftAmount));
                        totalTabMovement += Math.abs(shiftAmount);
                    }
                }
            }

//            FXPad lets you control your indents.
//            Ctrl/Command + Tab to indent selected content to the right.
//                    Shift + Tab to indent selected content to the left.

            textArea.replaceText(fullSelectionStart, Math.min(fullSelectionEnd, textArea.getText().length()), String.join("\n", lines));
            textArea.selectRange(fullSelectionStart, fullSelectionEnd + (shiftAmount * totalTabMovement));
        }
    }

    private static Map.Entry<String, int[]> getCurrentLine(TextArea tx) {
        String text = tx.getText();
        int caretPos = tx.getCaretPosition() - 1;
        int first = text.lastIndexOf('\n', caretPos - 1);
        int last = text.indexOf('\n', caretPos);

        if (first < 0) first = 0;
        if (text.charAt(first) == '\n') first++;
        if (last < 0) last = text.length();

        return Map.entry(tx.getText(first, last), new int[]{first, last});
    }

    /**
     * Sets the editor's current file to the file specified.
     *
     * @param file The file to set the current file to.
     */
    private void setCurrentFile(File file) {
        unsavedContent = false;

        currentFile = file;
        currentFileContents = textArea.getText();
        currentFileTitle = file.getName();

        Main.getStage().setTitle(currentFile.getName() + " | " + Main.title);
        System.gc();
    }

    /**
     * Creates a confirmation Dialog that confirms whether or not the specified action should be completed.
     *
     * @param possibleAction The action to be completed.
     * @return whether or not the action should be completed.
     */
    public boolean confirmUnsavedChanges(String possibleAction) {
        AtomicReference<Boolean> confirmation = new AtomicReference<>(false);
        Alert confirmationAlert = new Alert(Alert.AlertType.WARNING);
        confirmationAlert.initModality(Modality.APPLICATION_MODAL);
        confirmationAlert.initOwner(Main.getStage());

        confirmationAlert.getButtonTypes().clear();
        confirmationAlert.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.YES);

        confirmationAlert.setTitle("Unsaved Changes");
        confirmationAlert.setHeaderText("");
        confirmationAlert.setContentText(
                "You have unsaved changes in " + currentFileTitle + ".\nAre you sure you want to " + possibleAction + "?"
        );

        Toolkit.getDefaultToolkit().beep();

        confirmationAlert.showAndWait().ifPresent((button) -> {
            if (button == ButtonType.YES) {
                confirmation.set(true);
            } else if (button == ButtonType.CANCEL) {
                confirmation.set(false);
            }
        });

        return confirmation.get();
    }

    @FXML
    private void undo() {
        textArea.undo();
    }

    @FXML
    private void redo() {
        textArea.redo();
    }

    @FXML
    private void copy() {
        textArea.copy();
    }

    @FXML
    private void cut() {
        textArea.cut();
    }

    @FXML
    private void paste() {
        textArea.paste();
    }

    @FXML
    private void selectAll() {
        textArea.selectAll();
    }

    @FXML
    private void shiftRight() {
        shift(1);
    }

    @FXML
    private void shiftLeft() {
        shift(-1);
    }

    public boolean isUnsaved() {
        return unsavedContent;
    }
}
