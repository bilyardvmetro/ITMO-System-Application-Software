package client.UI;

import Network.Request;
import Network.User;
import Utils.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;


public class CountGreaterThanEnginePowerWindowController {

    Client client = ApplicationClient.getClient();

    User user = client.getUser();

    private MainPageController mainPageController;

    private Locale locale = MainPageController.locale;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField enginePowerField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button submitButton;

    @FXML
    void initialize() {
        resources = ResourceBundle.getBundle("locales", locale);
        enginePowerField.setPromptText(resources.getString("type_engine_power_here"));
        submitButton.setText(resources.getString("submit"));

        submitButton.setOnAction(actionEvent -> {
            try {
                var response = client.sendAndReceive(new Request(user, "countGreaterThanEnginePower", enginePowerField.getText()));
                mainPageController.printResponse(response.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setParent(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }
}
