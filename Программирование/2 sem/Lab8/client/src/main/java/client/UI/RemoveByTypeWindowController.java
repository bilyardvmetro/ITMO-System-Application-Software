package client.UI;

import CollectionObject.VehicleType;
import Network.Request;
import Network.User;
import Utils.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class RemoveByTypeWindowController {
    Client client = ApplicationClient.getClient();

    User user = client.getUser();

    private final ObservableList<VehicleType> types = FXCollections.observableArrayList(VehicleType.BOAT, VehicleType.HOVERBOARD, VehicleType.SPACESHIP);

    private MainPageController mainPageController;

    @FXML
    private ResourceBundle resources;
    private Locale locale = MainPageController.locale;

    @FXML
    private URL location;

    @FXML
    private Label messageLabel;

    @FXML
    private Button submitButton;

    @FXML
    private ChoiceBox<VehicleType> typeField;

    @FXML
    void initialize() {
        typeField.setItems(types);
        typeField.setValue(VehicleType.BOAT);
        resources = ResourceBundle.getBundle("locales", locale);
        submitButton.setText(resources.getString("submit"));

        submitButton.setOnAction(actionEvent -> {
            try {
                var response = client.sendAndReceive(new Request(user, "removeByType", typeField.getValue().toString()));
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
