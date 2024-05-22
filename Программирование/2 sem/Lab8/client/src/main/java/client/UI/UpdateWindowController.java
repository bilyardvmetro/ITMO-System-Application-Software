package client.UI;

import CollectionObject.Coordinates;
import CollectionObject.VehicleModel;
import CollectionObject.VehicleType;
import Exceptions.EmptyFieldException;
import Exceptions.NegativeFieldException;
import Network.Request;
import Network.User;
import Utils.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UpdateWindowController {
    Client client = ApplicationClient.getClient();

    User user = client.getUser();

    private MainPageController mainPageController;

    ObservableList<VehicleType> types = FXCollections.observableArrayList(VehicleType.BOAT, VehicleType.HOVERBOARD, VehicleType.SPACESHIP);

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField capacityForm;

    @FXML
    private TextField distanceTravelledForm;

    @FXML
    private TextField enginePowerForm;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField nameForm;

    @FXML
    private ChoiceBox<VehicleType> typeChoiceBox;

    @FXML
    private Button updateSubmitButton;

    @FXML
    private TextField xForm;

    @FXML
    private TextField yForm;

    @FXML
    void initialize() {
        typeChoiceBox.setItems(types);

        updateSubmitButton.setOnAction(actionEvent -> {
            try {
                var name = nameForm.getText();
                if (name.isBlank()) throw new EmptyFieldException("Name cannot be empty");

                var x = Float.parseFloat(xForm.getText());
                var y = Double.parseDouble(yForm.getText());
                var coordinates = new Coordinates(x, y);

                var enginePower = Double.parseDouble(enginePowerForm.getText());
                if (enginePower < 0) throw new NegativeFieldException("Engine power cannot be negative");

                var capacity = Float.parseFloat(capacityForm.getText());
                if (capacity < 0) throw new NegativeFieldException("Capacity cannot be negative");

                var distanceTravelled = Float.parseFloat(distanceTravelledForm.getText());
                if (distanceTravelled < 0) throw new NegativeFieldException("Distance travelled cannot be negative");

                var type = typeChoiceBox.getValue();

                var response = client.sendAndReceive(new Request(user, "update", "", new VehicleModel(name, coordinates, enginePower, capacity, distanceTravelled, type, user)));
                mainPageController.printResponse(response.getMessage());
                mainPageController.RefreshObjectsTable(response.getCollection());

            } catch (NegativeFieldException | EmptyFieldException e) {
                messageLabel.setText(e.getMessage());
            } catch (NumberFormatException e) {
                messageLabel.setText("Incorrect number input");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public void setParent(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }
}
