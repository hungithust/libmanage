package st.librarymanagement.Controller;

import com.google.protobuf.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import st.librarymanagement.Controller.Center.AuthorController;
import st.librarymanagement.Controller.Center.BookController;

import java.sql.SQLException;
import java.text.ParseException;

public abstract class RightSideController<T> {
    protected T selectedItem;
    @FXML    protected Button btnSave;
    @FXML    protected Button btnEdit;
    @FXML    protected Button btnDelete;

    public ManagementController<T> managementController;

    @FXML    public void initialize() {
        btnSave.setOnAction(e -> {
            try {
                if (selectedItem == null) {
                    handleSave();

                } else {
                    handleSave();
                }
            } catch (SQLException | ParseException | ServiceException ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        btnEdit.setOnAction(e -> {
            try {
                handleEdit();
            } catch (ServiceException ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        btnDelete.setOnAction(e -> {
            try {
                handleDelete();
            } catch (ServiceException | SQLException ex) {
                showAlert("Error", ex.getMessage());
            }
        });
    }

    public abstract void setSelectedItem(T item);
    public abstract void fillFields();
    public abstract void handleSave() throws SQLException, ParseException, ServiceException;
    public abstract void handleEdit() throws ServiceException;
    public abstract void handleDelete() throws ServiceException, SQLException;
    public abstract void clearFields();

    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public abstract void setManagementController(ManagementController<T> managementController) throws ServiceException;

}
