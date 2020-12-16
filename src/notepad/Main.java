package notepad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static final String title = "FXPad";

    private static Stage mainStage;

    public static Stage getStage() {
        return mainStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("sample.fxml")), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        System.gc();

        mainStage.setScene(scene);
        mainStage.setTitle("Untitled.txt | " + title);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
