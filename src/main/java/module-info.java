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
    exports com.doldom.farmacia.presentation.views.ventas;
    exports com.doldom.farmacia.presentation.views.compras;
    exports com.doldom.farmacia.presentation.views.caja;
    exports com.doldom.farmacia.presentation.views.calendario;
    exports com.doldom.farmacia.presentation.views.contabilidad;
    exports com.doldom.farmacia.presentation.components.home;
    exports com.doldom.farmacia.presentation.components.inventory;
    exports com.doldom.farmacia.presentation.components.sales;
    exports com.doldom.farmacia.presentation.components.purchases;
    exports com.doldom.farmacia.presentation.components.cashregister;
    exports com.doldom.farmacia.presentation.components.calendar;
    exports com.doldom.farmacia.presentation.components.accounting;
    opens com.doldom.farmacia.presentation.views.login to javafx.fxml;
    opens com.doldom.farmacia.presentation.views.home to javafx.fxml;
    opens com.doldom.farmacia.presentation.views.inventario to javafx.fxml;
    opens com.doldom.farmacia.presentation.views.ventas to javafx.fxml;
    opens com.doldom.farmacia.presentation.views.compras to javafx.fxml;
    opens com.doldom.farmacia.presentation.views.caja to javafx.fxml;
    opens com.doldom.farmacia.presentation.views.calendario to javafx.fxml;
    opens com.doldom.farmacia.presentation.views.contabilidad to javafx.fxml;
    opens com.doldom.farmacia.presentation.components.home to javafx.fxml;
    opens com.doldom.farmacia.presentation.components.inventory to javafx.fxml;
    opens com.doldom.farmacia.presentation.components.sales to javafx.fxml;
    opens com.doldom.farmacia.presentation.components.purchases to javafx.fxml;
    opens com.doldom.farmacia.presentation.components.cashregister to javafx.fxml;
    opens com.doldom.farmacia.presentation.components.calendar to javafx.fxml;
    opens com.doldom.farmacia.presentation.components.accounting to javafx.fxml;
}
