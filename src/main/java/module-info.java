module st.librarymanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires static lombok;
    requires mysql.connector.j;
    requires java.desktop;
    requires com.google.protobuf;
    requires org.slf4j;

    opens st.librarymanagement to javafx.fxml;
    exports st.librarymanagement;
    exports st.librarymanagement.Controller;
    opens st.librarymanagement.Controller to javafx.fxml;
    exports st.librarymanagement.Service;
    exports st.librarymanagement.Controller.Center;
    opens st.librarymanagement.Controller.Center to javafx.fxml;
    exports st.librarymanagement.Controller.Right;
    opens st.librarymanagement.Controller.Right to javafx.fxml;
    exports st.librarymanagement.Entity;
    exports st.librarymanagement.Repository;
    exports st.librarymanagement.Entity.Status;
    exports st.librarymanagement.Config;
    exports st.librarymanagement.Dto;
    exports st.librarymanagement.Util;
}