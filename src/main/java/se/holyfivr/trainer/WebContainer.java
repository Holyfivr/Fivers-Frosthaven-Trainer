package se.holyfivr.trainer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/static/img/art/icon.png")));

        Region background = new Region();
        String bgImage = getClass().getResource("/static/img/art/bg.png").toExternalForm();
        background.setStyle(
                "-fx-background-image: url('" + bgImage + "'); -fx-background-size: cover;");
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
