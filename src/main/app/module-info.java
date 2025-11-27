module com.example.gemcollector {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires javafx.media;

    exports gemcollector.core;
    exports gemcollector.entities;
    exports com.example.gemcollector;

    opens gemcollector.core to javafx.fxml;
    opens gemcollector.entities to javafx.fxml;
    opens com.example.gemcollector to javafx.fxml;
}
