package client.UI;

import CollectionObject.Vehicle;
import CollectionObject.VehicleType;
import Network.Request;
import Network.User;
import Utils.Client;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;

public class MainPageController {

    private Client client = ApplicationClient.getClient();

    private User user = client.getUser();

    public static Stack<Vehicle> vehicles;

    {
        try {
            vehicles = client.sendAndReceive(new Request(user, "show", "")).getCollection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Vehicle> collection = FXCollections.observableList(vehicles);

    static Locale locale;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<Vehicle, Long> idColumn;

    @FXML
    private TableColumn<Vehicle, Float> capacityColumn;

    @FXML
    private TableColumn<Vehicle, String> nameColumn;

    @FXML
    private TableColumn<Vehicle, VehicleType> typeColumn;

    @FXML
    private Separator CanvasSep;

    @FXML
    private Tab CanvasTab;

    @FXML
    private AnchorPane CanvasTabAnchor;

    @FXML
    private TableColumn<Vehicle, Float> DTColumn;

    @FXML
    private TableColumn<Vehicle, Double> EPColumn;

    @FXML
    private TextArea InfoArea;

    @FXML
    private AnchorPane MainPageAnchor;

    @FXML
    private Tab MainTab;

    @FXML
    private Canvas ObjectCanvas;

    @FXML
    private TableView<Vehicle> ObjectTable;

    @FXML
    private TabPane TabPanel;

    @FXML
    private MenuItem addButton;

    @FXML
    private ImageView avatar;

    @FXML
    private MenuItem clearButton;

    @FXML
    private Menu commandMenu;

    @FXML
    private MenuItem countGreaterThanEPButton;

    @FXML
    private TableColumn<Vehicle, String> creatorColumn;

    @FXML
    private TableColumn<Vehicle, Date> dateColumn;

    @FXML
    private Menu executeScriptMenu;

    @FXML
    private Button exitButton;

    @FXML
    private MenuItem helpButton;

    @FXML
    private MenuItem historyButton;

    @FXML
    private MenuItem infoButton;

    @FXML
    private AnchorPane mainAnchor;

    @FXML
    private Separator mainPageSep;

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem nameFilterButton;

    @FXML
    private Label prikolLabel;

    @FXML
    private MenuItem removeAllByTypeButton;

    @FXML
    private MenuItem removeByIdButton;

    @FXML
    private MenuItem removeGreaterButton;

    @FXML
    private MenuItem reorderButton;

    @FXML
    private TextArea responseArea;

    @FXML
    private MenuItem scriptButton;

    @FXML
    private MenuItem showButton;

    @FXML
    private MenuItem updateButton;

    @FXML
    private TextField usernameBar;

    @FXML
    private TableColumn<Vehicle, Float> xColumn;

    @FXML
    private TableColumn<Vehicle, Double> yColumn;

    @FXML
    void initialize() {
        Image image = new Image("avatar.jpg");
        avatar.setImage(image);
        usernameBar.setText(user.getUsername());
        initializeTables(collection);

        addButton.setOnAction(actionEvent -> {
            showWindow("AddWindow.fxml");
        });
    }

    private void initializeTables(ObservableList<Vehicle> collection){
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        xColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCoordinates().getX()));
        yColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCoordinates().getY()));
        dateColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCreationDate()));
        EPColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getEnginePower()));
        capacityColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCapacity()));
        DTColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDistanceTravelled()));
        typeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getType()));
        creatorColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCreator()));

        ObjectTable.setItems(collection);
    }

    private void showWindow(String view) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(LoginRegisterPageController.class.getResource(view));

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = fxmlLoader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle(view.replace(".fxml", ""));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}
