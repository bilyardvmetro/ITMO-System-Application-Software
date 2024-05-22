package client.UI;

import Network.User;
import Utils.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginRegisterPageController {

    Client client = ApplicationClient.getClient();

    User user = client.getUser();

    static Locale locale;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane MainPane;

    @FXML
    private TabPane TabPane;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginErrorField;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private Tab loginTab;

    @FXML
    private TextField loginUsernameField;

    @FXML
    private Button registerButton;

    @FXML
    private Label registerErrorField;

    @FXML
    private AnchorPane registerPane;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    private Tab registerTab;

    @FXML
    private TextField registerUsernameField;

    @FXML
    void initialize() {
        try {
            client.connect();
        } catch (ConnectException e) {
            loginErrorField.setText("Сервер недоступен в данный момент. Пожалуйста, повторите попытку позже");
            registerErrorField.setText("Сервер недоступен в данный момент. Пожалуйста, повторите попытку позже");
        }

        loginButton.setOnAction(actionEvent -> {
            var username = loginUsernameField.getText();
            var password = loginPasswordField.getText();

            var response = client.authenticateUser(username, password);
            if (response.isUserAuthenticated()) {
                showMainPage(loginButton);
            } else {
                loginErrorField.setText(response.getMessage());
            }
        });

        registerButton.setOnAction(actionEvent -> {
            var username = registerUsernameField.getText();
            var password = registerPasswordField.getText();

            try {
                var response = client.registerUser(username, password);

                if (response.isUserAuthenticated()) {
                    showMainPage(registerButton);
                } else {
                    registerErrorField.setText(response.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
    }

    private void showMainPage(Button button) {
        button.getScene().getWindow().hide();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(LoginRegisterPageController.class.getResource("MainPage.fxml"));

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = fxmlLoader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("bilyardvmetro Lab8");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}


