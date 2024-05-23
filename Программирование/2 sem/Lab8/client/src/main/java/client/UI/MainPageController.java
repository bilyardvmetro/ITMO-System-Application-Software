package client.UI;

import CollectionObject.Vehicle;
import CollectionObject.VehicleType;
import Network.Request;
import Network.User;
import Utils.Client;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainPageController {

    private final Client client = ApplicationClient.getClient();

    private final User user = client.getUser();

    private CountGreaterThanEnginePowerWindowController CGTEPController;

    private FilterStartsWithNameWindowController FSWNController;

    private AddWindowController addWindowController;

    private UpdateWindowController updateWindowController;

    private RemoveByIdWindowController removeByIdWindowController;

    private RemoveByTypeWindowController removeByTypeWindowController;

    private RemoveGreaterWindowController removeGreaterWindowController;

    public static Stack<Vehicle> vehicles;

    private static Stack<Vehicle> vehiclesCopy;

    {
        vehiclesCopy = new Stack<>();
        try {
            vehicles = client.sendAndReceive(new Request(user, "ping", "")).getCollection();
            vehiclesCopy.addAll(vehicles);
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
    private Pane ObjectCanvas;

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
        initializeTable(collection);

        Timeline pingServerTimeline = getTimeline();
        pingServerTimeline.play();

        addButton.setOnAction(actionEvent -> {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(AddWindowController.class.getResource("AddWindow.fxml"));

            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = fxmlLoader.getRoot();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("AddWindow.fxml".replace(".fxml", ""));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            addWindowController = fxmlLoader.getController();
            addWindowController.setParent(this);
        });

        updateButton.setOnAction(actionEvent ->     {
            loadUpdateWindow();
        });

        removeByIdButton.setOnAction(actionEvent -> {
            loadRemoveByIdWindow();
        });

        removeAllByTypeButton.setOnAction(actionEvent -> {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(RemoveByTypeWindowController.class.getResource("RemoveByTypeWindow.fxml"));

            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = fxmlLoader.getRoot();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("RemoveByTypeWindow.fxml".replace(".fxml", ""));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            removeByTypeWindowController = fxmlLoader.getController();
            removeByTypeWindowController.setParent(this);
        });

        removeGreaterButton.setOnAction(actionEvent -> {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(RemoveGreaterWindowController.class.getResource("RemoveGreaterWindow.fxml"));

            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = fxmlLoader.getRoot();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("RemoveGreaterWindow.fxml".replace(".fxml", ""));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            removeGreaterWindowController = fxmlLoader.getController();
            removeGreaterWindowController.setParent(this);
        });

        countGreaterThanEPButton.setOnAction(actionEvent -> {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(CountGreaterThanEnginePowerWindowController.class.getResource("CountGreaterThanEnginePowerWindow.fxml"));

            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = fxmlLoader.getRoot();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("CountGreaterThanEnginePowerWindow.fxml".replace(".fxml", ""));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            CGTEPController = fxmlLoader.getController();
            CGTEPController.setParent(this);
        });

        nameFilterButton.setOnAction(actionEvent -> {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(FilterStartsWithNameWindowController.class.getResource("FilterStartsWithNameWindow.fxml"));

            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = fxmlLoader.getRoot();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("FilterStartsWithNameWindow.fxml".replace(".fxml", ""));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            FSWNController = fxmlLoader.getController();
            FSWNController.setParent(this);
        });

        exitButton.setOnAction(actionEvent -> System.exit(0));

        helpButton.setOnAction(actionEvent -> processUserCommand("help"));

        reorderButton.setOnAction(actionEvent -> processUserCommand("reorder"));

//        scriptButton.setOnAction(actionEvent -> );

        showButton.setOnAction(actionEvent -> processUserCommand("show"));

        historyButton.setOnAction(actionEvent -> processUserCommand("history"));

        infoButton.setOnAction(actionEvent -> processUserCommand("info"));

        clearButton.setOnAction(actionEvent -> processUserCommand("clear"));

        idColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(Vehicle::getId))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

        nameColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(Vehicle::getName))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

        xColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(vehicle -> vehicle.getCoordinates().getX()))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

        yColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(vehicle -> vehicle.getCoordinates().getY()))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

        dateColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(Vehicle::getCreationDate))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

        EPColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(Vehicle::getEnginePower))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

        capacityColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(Vehicle::getCapacity))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

        DTColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(Vehicle::getDistanceTravelled))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

        typeColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(Vehicle::getType))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

        creatorColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ObjectTable.setItems(collection.stream()
                    .sorted(Comparator.comparing(Vehicle::getCreator))
                    .collect(Collectors.toCollection(() -> FXCollections.observableList(collection)))
            );
            ObjectTable.refresh();
        });

    }

    private void loadRemoveByIdWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(RemoveByIdWindowController.class.getResource("RemoveByIdWindow.fxml"));

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = fxmlLoader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("RemoveByIdWindow.fxml".replace(".fxml", ""));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        removeByIdWindowController = fxmlLoader.getController();
        removeByIdWindowController.setParent(this);
    }

    private Timeline getTimeline() {
        Timeline pingServerTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            try {
                vehicles = client.sendAndReceive(new Request(user, "ping", "")).getCollection();
                RefreshObjectsTable(vehicles);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }));

        pingServerTimeline.setCycleCount(Animation.INDEFINITE);
        return pingServerTimeline;
    }

    public void RefreshObjectsTable(Stack<Vehicle> vehicles) {
        if (vehicles != null){
            if (!vehicles.equals(vehiclesCopy)) {
                ObjectCanvas.getChildren().clear();

                drawVehicles(vehicles);
                vehiclesCopy = vehicles;
            }

            collection = FXCollections.observableList(vehicles);
            ObjectTable.setItems(collection);
            ObjectTable.refresh();
        }
    }

    private void processUserCommand(String command) {
        try {
            var response = client.sendAndReceive(new Request(user, command, ""));
            printResponse(response.getMessage());

            if (response.getCollection() != null) {
                vehicles = response.getCollection();
                RefreshObjectsTable(vehicles);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void printResponse(String message) {
        responseArea.setText(message);
    }

    private void drawVehicles(Stack<Vehicle> vehicles) {
        var x = 0;
        var y = 0;

        for (Vehicle element : vehicles) {
            if (x >= 1200) {
                x = 0;
                y += 74;
            }

            Circle circle = new Circle(15);
            circle.setCenterX(x);
            circle.setCenterY(y);

            if (element.getCreator().equals(user.getUsername())) {
                circle.setFill(Color.DEEPSKYBLUE);
            } else {
                circle.setFill(Color.ORANGERED);
            }

            ScaleTransition circleAnimation = new ScaleTransition(Duration.millis(800), circle);
            circleAnimation.setFromX(0.2);
            circleAnimation.setToX(1);
            circleAnimation.setFromY(0.2);
            circleAnimation.setToY(1);

            var text = getCircleText(element, x, y);

            circleAnimation.play();

            ObjectCanvas.getChildren().addAll(circle, text);

            x += 40;
        }

    }

    private Text getCircleText(Vehicle element, int x, int y) {
        var text = new Text(x - 13, y + 4, element.getId().toString());

        text.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if (mouseEvent.getClickCount() == 2){
                    loadUpdateWindow();
                }
                InfoArea.setText(element.toString());
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                loadRemoveByIdWindow();
            }
        });
        return text;
    }

    private void loadUpdateWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(UpdateWindowController.class.getResource("UpdateWindow.fxml"));

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = fxmlLoader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("UpdateWindow.fxml".replace(".fxml", ""));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        updateWindowController = fxmlLoader.getController();
        updateWindowController.setParent(this);
    }

    private void initializeTable(ObservableList<Vehicle> collection) {
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

}
