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
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class AddWindowController {

    ObservableList<VehicleType> types = FXCollections.observableArrayList(VehicleType.BOAT, VehicleType.HOVERBOARD, VehicleType.SPACESHIP);

    Client client = ApplicationClient.getClient();

    User user = client.getUser();

    private MainPageController mainPageController;

    static Locale locale = MainPageController.locale;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane addPane;

    @FXML
    private Button addSubmitButton;

    @FXML
    private TextField capacityForm;

    @FXML
    private TextField distanceTravelledForm;

    @FXML
    private TextField enginePowerForm;

    @FXML
    private TextField nameForm;

    @FXML
    private ChoiceBox<VehicleType> typeChoiceBox;

    @FXML
    private TextField xForm;

    @FXML
    private TextField yForm;

    @FXML
    private Label messageLabel;

    @FXML
    void initialize() {
        typeChoiceBox.setItems(types);
        resources = ResourceBundle.getBundle("locales", locale);
        setLocaleText();

        addSubmitButton.setOnAction(actionEvent -> {
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

                var response = client.sendAndReceive(new Request(user, "add", "", new VehicleModel(name, coordinates, enginePower, capacity, distanceTravelled, type, user)));
                mainPageController.printResponse(response.getMessage());

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

    public void setLocaleText(){
        nameForm.setPromptText(resources.getString("name"));
        enginePowerForm.setPromptText(resources.getString("engine_power"));
        capacityForm.setPromptText(resources.getString("capacity"));
        distanceTravelledForm.setPromptText(resources.getString("distance_travelled"));
        addSubmitButton.setText(resources.getString("submit"));
    }

    public void setParent (MainPageController mainPageController){
        this.mainPageController = mainPageController;
    }
}
