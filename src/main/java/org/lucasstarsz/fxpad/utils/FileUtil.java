package org.lucasstarsz.fxpad.utils;

import javafx.stage.FileChooser;
import org.lucasstarsz.fxpad.RTFXMain;
import org.lucasstarsz.fxpad.nodes.FormatArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtil {

    private static final FileChooser.ExtensionFilter textFileFilter = new FileChooser.ExtensionFilter("Text File", "*.txt");
    private static final FileChooser.ExtensionFilter allFilesFiler = new FileChooser.ExtensionFilter("All Files", "*.*");
    private static final String os = System.getProperty("os.name").startsWith("Win") ? "Windows" : System.getProperty("os.name").startsWith("Linux") ? "Linux" : "Mac";

    /**
     * Tries to open a new file, and returns the result.
     *
     * @param currentFile The current file.
     * @return The new file, with the possibility of being null.
     */
    public static File tryOpenNewFile(File currentFile) {
        FileChooser chooser = createFileChooser(currentFile);
        return chooser.showOpenDialog(RTFXMain.getStage());
    }

    /**
     * Tries to save the specified file, returning the file that may have bene written to.
     *
     * @param textArea The container of the content to write.
     * @param file     The file to write to.
     * @return The file that was written to.
     * @throws IOException This is thrown by {@code FileUtil.write(...)} if there is an error when writing the file.
     */
    public static File trySaveFileAs(FormatArea textArea, File file) throws IOException {
        FileChooser chooser = createFileChooser(file);
        File saveFile = chooser.showSaveDialog(RTFXMain.getStage());
        if (saveFile != null) FileUtil.write(textArea, saveFile);
        return saveFile;
    }

    /**
     * Returns the content of the file, accounting for unsupported encodings.
     *
     * @param file The file to read content from.
     * @return The content of the file.
     * @throws IOException This is thrown by {@code Files.readString(...), Files.readAllBytes(...)} if there is an error reading the
     *                     contents of the file.
     */
    @SuppressWarnings("ReadWriteStringCanBeUsed")
    public static String open(File file) throws IOException {
        try {
            return Files.readString(file.toPath());
        } catch (MalformedInputException e) {
            if (os.equals("linux")) {
                DialogUtil.cantOpenFile(file.getAbsolutePath());
            } else if (DialogUtil.confirmOpenUnsupported(file)) {
                return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            DialogUtil.massiveFailure(e);
        }
        return null;
    }

    /**
     * Writes the contents of the editor to the specified file.
     *
     * @param textArea The container of the content to write.
     * @param file     The file to write to.
     * @throws IOException This is thrown by {@code new FileWriter(...), FileWriter#write(...)} if there is an error when creating the
     *                     {@link FileWriter} or while writing the file.
     */
    public static void write(FormatArea textArea, File file) throws IOException {
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile())) {
            fw.write(textArea.getText());
        }
    }

    /**
     * Creates a {@link FileChooser}, starting in the directory of the specified file.
     *
     * @param file The container of the initial directory.
     * @return The created {@link FileChooser}.
     */
    private static FileChooser createFileChooser(File file) {
        FileChooser chooser = new FileChooser();
        if (file != null && file.exists()) {
            chooser.setInitialDirectory(new File(file.getAbsoluteFile().getParent()));
        }

        chooser.getExtensionFilters().addAll(textFileFilter, allFilesFiler);
        return chooser;
    }
}
