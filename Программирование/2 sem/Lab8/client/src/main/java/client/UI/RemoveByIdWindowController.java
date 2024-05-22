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
import java.util.ResourceBundle;

public class RemoveByIdWindowController {
    Client client = ApplicationClient.getClient();

    User user = client.getUser();

    private MainPageController mainPageController;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField idField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button submitButton;

    @FXML
    void initialize() {
        submitButton.setOnAction(actionEvent -> {
            try {
                var response = client.sendAndReceive(new Request(user, "removeById", idField.getText()));
                mainPageController.printResponse(response.getMessage());
                mainPageController.RefreshObjectsTable(response.getCollection());
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
