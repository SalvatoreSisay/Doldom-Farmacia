module com.resdev.doldom.doldomfarmacia {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.kordamp.bootstrapfx.core;

    opens com.resdev.doldom.doldomfarmacia to javafx.fxml;
    exports com.resdev.doldom.doldomfarmacia;
}