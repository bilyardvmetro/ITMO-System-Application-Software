package client.UI;

import CollectionObject.Vehicle;
import Network.Request;
import Network.User;
import Utils.Client;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class RemoveByIdWindowController {
    Client client = ApplicationClient.getClient();

    User user = client.getUser();

    private Stack<Vehicle> vehicles = MainPageController.vehicles;

    private Long id;

    private MainPageController mainPageController;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuButton idChoiceField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button submitButton;

    @FXML
    void initialize() {
        idChoiceField.setText("id");

        for (Vehicle element : vehicles) {
            var menuItem = new MenuItem(String.valueOf(element.getId()));

            menuItem.setOnAction(actionEvent -> {
                id = Long.parseLong(menuItem.getText());
                idChoiceField.setText(element.getId().toString());
            });

            idChoiceField.getItems().addAll(menuItem);
        }

        submitButton.setOnAction(actionEvent -> {
            try {
                var response = client.sendAndReceive(new Request(user, "removeById", idChoiceField.toString()));
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
