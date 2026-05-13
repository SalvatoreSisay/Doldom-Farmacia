module com.doldom.farmacia {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.kordamp.bootstrapfx.core;

    exports com.doldom.farmacia;
    exports com.doldom.farmacia.presentation.navigation;
    exports com.doldom.farmacia.presentation.views.login;
    exports com.doldom.farmacia.presentation.views.home;
    exports com.doldom.farmacia.presentation.views.inventario;
    exports com.doldom.farmacia.presentation.components.home;
    exports com.doldom.farmacia.presentation.components.inventory;
    opens com.doldom.farmacia.presentation.views.login to javafx.fxml;
    opens com.doldom.farmacia.presentation.views.home to javafx.fxml;
    opens com.doldom.farmacia.presentation.views.inventario to javafx.fxml;
    opens com.doldom.farmacia.presentation.components.home to javafx.fxml;
    opens com.doldom.farmacia.presentation.components.inventory to javafx.fxml;
}
