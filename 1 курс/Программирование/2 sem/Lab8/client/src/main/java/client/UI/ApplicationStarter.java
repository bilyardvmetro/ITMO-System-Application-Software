package client.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class ApplicationStarter extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationStarter.class.getResource("LoginRegisterPage.fxml"));
        fxmlLoader.setResources(ResourceBundle.getBundle("locales"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("bilyardvmetro Lab8");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}