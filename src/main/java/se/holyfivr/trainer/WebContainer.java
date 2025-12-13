package se.holyfivr.trainer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

// if i followed these warnings, the program wouldnt work as intended. Sooooo. Shysh!
@SuppressWarnings({"java:S1104", "java:S1444", "java:S2696"}) 
public class WebContainer extends Application {

    // Will be set by TrainerApplication
    public static int port;

    // Used as a reference for the file chooser dialog in StartController
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Region background = new Region();
        background.setStyle(
                "-fx-background-color: #E3E3E3;");
        WebView webView = new WebView();
        webView.pageFillProperty().set(Color.TRANSPARENT);
        webView.getEngine().load("http://localhost:" + port + "/start");

        StackPane root = new StackPane();
        root.getChildren().addAll(background, webView);

        stage.setAlwaysOnTop(true);
        stage.onCloseRequestProperty().set(event -> {
            System.exit(0);
        });
        stage.setScene(new Scene(root, 1000, 700));
        stage.setResizable(false);
        stage.show();

    }
}
