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
import java.net.SocketException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginRegisterPageController {

    @FXML
    public Label passwordLabel;

    @FXML
    public Label loginHereLabel;

    @FXML
    public Label welcomeLabel;

    @FXML
    public Label loginLabel;

    @FXML
    public Label loginLabelR;

    @FXML
    public Label passwordLabelR;

    @FXML
    public Label registerHereLabel;

    @FXML
    public Label welcomeLabelR;

    Client client = ApplicationClient.getClient();

    User user = client.getUser();

    private static Locale locale;

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
    private Button russianButton;

    @FXML
    private Button russianButtonR;

    @FXML
    private Button slovakianButton;

    @FXML
    private Button slovakianButtonR;

    @FXML
    private Button spainButton;

    @FXML
    private Button spainButtonR;

    @FXML
    private Button swedenButton;

    @FXML
    private Button swedenButtonR;

    @FXML
    void initialize() {
        try {
            client.connect();
        } catch (SocketException e) {
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

        russianButton.setOnAction(actionEvent -> {
            locale = new Locale("ru", "RU");
            setLocaleText();
        });

        slovakianButton.setOnAction(actionEvent -> {
            locale = new Locale("sk", "SK");
            setLocaleText();
        });

        swedenButton.setOnAction(actionEvent -> {
            locale = new Locale("sv", "SE");
            setLocaleText();
        });

        spainButton.setOnAction(actionEvent -> {
            locale = new Locale("es", "ES");
            setLocaleText();
        });

        russianButtonR.setOnAction(actionEvent -> {
            locale = new Locale("ru", "RU");
            setLocaleText();
        });

        slovakianButtonR.setOnAction(actionEvent -> {
            locale = new Locale("sk", "SK");
            setLocaleText();
        });

        swedenButtonR.setOnAction(actionEvent -> {
            locale = new Locale("sv", "SE");
            setLocaleText();
        });

        spainButtonR.setOnAction(actionEvent -> {
            locale = new Locale("es", "ES");
            setLocaleText();
        });
    }

    private void setLocaleText() {
        resources = ResourceBundle.getBundle("locales", locale);
        loginTab.setText("login");
        registerTab.setText("register");

        loginButton.setText(resources.getString("login"));
        registerButton.setText(resources.getString("register"));

        loginLabel.setText(resources.getString("login"));
        passwordLabel.setText(resources.getString("password"));
        loginHereLabel.setText(resources.getString("pls_login_here"));
        welcomeLabel.setText(resources.getString("welcome"));

        loginLabelR.setText(resources.getString("login"));
        passwordLabelR.setText(resources.getString("password"));
        registerHereLabel.setText(resources.getString("pls_register_here"));
        welcomeLabelR.setText(resources.getString("welcome"));
    }

    private void showMainPage(Button button) {
        button.getScene().getWindow().hide();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(LoginRegisterPageController.class.getResource("MainPage.fxml"));
        fxmlLoader.setResources(ResourceBundle.getBundle("locales"));

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


