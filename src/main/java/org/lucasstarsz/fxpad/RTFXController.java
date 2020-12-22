package org.lucasstarsz.fxpad;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.LineNumberFactory;
import org.lucasstarsz.fxpad.nodes.FormatArea;
import org.lucasstarsz.fxpad.utils.DialogUtil;
import org.lucasstarsz.fxpad.utils.FileUtil;
import org.lucasstarsz.fxpad.utils.TextUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RTFXController {

    @FXML private VBox container;

    // File Menu
    @FXML private MenuItem newFileMenuItem;
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
    @FXML private MenuItem findMenuItem;

    @FXML private FormatArea textArea;

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
                indentRightMenuItem, new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHORTCUT_DOWN),
                findMenuItem, new KeyCodeCombination(KeyCode.F, KeyCodeCombination.SHORTCUT_DOWN)
        );

        Map<MenuItem, KeyCombination> fileMenuMnemonics = Map.of(
                newFileMenuItem, new KeyCodeCombination(KeyCode.N, KeyCodeCombination.SHORTCUT_DOWN),
                openMenuItem, new KeyCodeCombination(KeyCode.O, KeyCodeCombination.SHORTCUT_DOWN),
                saveMenuItem, new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN),
                saveAsMenuItem, new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN, KeyCodeCombination.SHIFT_DOWN)
        );

        editMenuMnemonics.forEach(MenuItem::setAccelerator);
        fileMenuMnemonics.forEach(MenuItem::setAccelerator);

        textArea.setOnKeyPressed(this::keepTabs);
        textArea.setOnKeyTyped(event -> checkIfModified());
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));

        container.setOnDragOver(event -> {
            if (event.getGestureSource() != container && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        container.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = dragboard.hasFiles();
            if (success) {
                try {
                    tryOpen(dragboard.getString().replaceFirst("file:/", ""));
                } catch (IOException e) {
                    DialogUtil.massiveFailure(e);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void tryOpen(String fileLocation) throws IOException {
        File toOpen = new File(fileLocation);

        if (!toOpen.exists()) {
            DialogUtil.cantOpenFile(fileLocation);
            return;
        }

        textArea.replaceText(FileUtil.open(toOpen));
        setCurrentFile(toOpen);
    }

    @FXML
    private void makeNewFile() {
        if (!unsavedContent ||
                DialogUtil.confirmUnsavedChanges(currentFileTitle, "create a new file")) {
            createNewFile();
        }
    }

    @FXML
    private void saveFile() throws IOException {
        if (currentFile == null || currentFile.createNewFile()) {
            saveFileAs();
            return;
        }

        FileUtil.write(textArea, currentFile);
        setCurrentFile(currentFile);
    }

    @FXML
    private void saveFileAs() throws IOException {
        File f = FileUtil.trySaveFileAs(textArea, currentFile);
        if (f != null) setCurrentFile(f);
    }

    @FXML
    private void openFile() throws IOException {
        File openedFile = FileUtil.tryOpenNewFile(currentFile);
        if (openedFile != null) {
            if (!unsavedContent || DialogUtil.confirmUnsavedChanges(currentFileTitle, "open " + openedFile.getName())) {
                String text = FileUtil.open(openedFile);
                if (text == null) return;

                textArea.replaceText(text);
                setCurrentFile(openedFile);
            }
        }
    }

    /** Creates a new file. */
    private void createNewFile() {
        unsavedContent = false;

        currentFile = null;
        currentFileContents = "";
        currentFileTitle = "Untitled.txt";
        textArea.replaceText("");

        RTFXMain.getStage().setTitle("Untitled.txt | " + RTFXMain.title);
        System.gc();
    }

    /** Checks if the current file's contents have been modified. */
    private void checkIfModified() {
        boolean contentModified = !currentFileContents.equals(textArea.getText());

        if (unsavedContent && !textArea.isUndoAvailable()) {
            unsavedContent = false;
            RTFXMain.getStage().setTitle(currentFileTitle + " | " + RTFXMain.title);
        } else if (!unsavedContent && contentModified) {
            unsavedContent = true;
            RTFXMain.getStage().setTitle(currentFileTitle + "* | " + RTFXMain.title);
        }
    }

    /**
     * Adds a new line, with the same amount of leading tabs as the current line.
     * <p>
     * This method only executes if the specified KeyEvent is KeyCode.ENTER.
     *
     * @param event The key event to check for. If this is KeyCode.ENTER, then the method will execute.
     */
    private void keepTabs(KeyEvent event) {
        switch (event.getCode()) {
            case TAB -> {
                if (event.isShiftDown()) {
                    event.consume();
                    shiftLeft();
                } else if (event.isControlDown()) {
                    event.consume();
                    shiftRight();
                }
            }
            case ENTER -> {
                event.consume();
                TextUtil.enterWithTabs(textArea);
            }
        }
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

        RTFXMain.getStage().setTitle(currentFile.getName() + " | " + RTFXMain.title);
        System.gc();
    }

    @FXML private void undo() {
        textArea.undo();
    }

    @FXML private void redo() {
        textArea.redo();
    }

    @FXML private void copy() {
        textArea.copy();
    }

    @FXML private void cut() {
        textArea.cut();
    }

    @FXML private void paste() {
        textArea.paste();
    }

    @FXML private void selectAll() {
        textArea.selectAll();
    }

    @FXML private void shiftRight() {
        TextUtil.shift(textArea, 1);
    }

    @FXML private void shiftLeft() {
        TextUtil.shift(textArea, -1);
    }

    // @TODO: Implement find, replace methods.
    @FXML private void find() {
    }

    public boolean isUnsaved() {
        return unsavedContent;
    }

    public String getCurrentFileTitle() {
        return currentFileTitle;
    }
}
