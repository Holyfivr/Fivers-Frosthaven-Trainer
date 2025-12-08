package se.holyfivr.trainer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebContainer extends Application{
    
    
    @Override
    public void start(Stage stage) throws Exception {

        Region background = new Region();
        background.setStyle("-fx-background-color: linear-gradient( #adadadff, #eeeeee);");
        WebView webView = new WebView();
        webView.pageFillProperty().set(Color.TRANSPARENT);
        webView.getEngine().load("http://localhost:8080/start");
        
        StackPane root = new StackPane();
        root.getChildren().addAll(background, webView);

        stage.setAlwaysOnTop(true);
        stage.setScene(new Scene(root, 1000,700));
        stage.setResizable(false);
        stage.show();

    }
}
