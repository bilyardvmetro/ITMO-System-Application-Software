module client.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens client.UI to javafx.fxml;
    exports client.UI;
}