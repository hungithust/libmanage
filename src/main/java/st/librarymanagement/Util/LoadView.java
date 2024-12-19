package st.librarymanagement.Util;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import st.librarymanagement.Controller.Controller;
import st.librarymanagement.Controller.ManagementController;

import java.io.IOException;

public class LoadView {


    // Simplified method to load different views with Controller setup
    public void loadViewWithController(Event event, String viewPath, Controller controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void loadRightView(String view, ManagementController controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(LoadView.class.getResource("/View/Right/" + view + ".fxml"));
        Pane rightPanel = loader.load();
        controller.setRightPanel(rightPanel);
    }

    // Example usage methods for specific views
    public void loadDashboardView(Event event, Controller controller) throws IOException {
        loadViewWithController(event, "/View/Center/DashboardView.fxml", controller);
    }

    public void loadAuthorView(Event event, Controller controller) throws IOException {
        loadViewWithController(event, "/View/Center/AuthorView.fxml", controller);
    }

    public void loadBookView(Event event, Controller controller) throws IOException {
        loadViewWithController(event, "/View/Center/BookView.fxml", controller);
    }

    public void loadPublisherView(Event event, Controller controller) throws IOException {
        loadViewWithController(event, "/View/Center/PublisherView.fxml", controller);
    }

    public void loadMemberView(Event event, Controller controller) throws IOException {
        loadViewWithController(event, "/View/Center/MemberView.fxml", controller);
    }

    public void loadBorrowView(Event event, Controller controller) throws IOException {
        loadViewWithController(event, "/View/Center/LoanView.fxml", controller);
    }

    public void loadFineView(Event event, Controller controller) throws IOException {
        loadViewWithController(event, "/View/Center/FineView.fxml", controller);
    }

    public void loadCustomView(Event event, String view, Controller controller) throws IOException {
        loadViewWithController(event, "/View/" + view + "View.fxml", controller);
    }
}
