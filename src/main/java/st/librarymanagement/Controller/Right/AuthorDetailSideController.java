package st.librarymanagement.Controller.Right;

import com.google.protobuf.ServiceException;
import javafx.scene.control.TextField;
import st.librarymanagement.Controller.Center.AuthorController;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Entity.Author;
import st.librarymanagement.Service.AuthorService;

import java.sql.SQLException;

public class AuthorDetailSideController extends RightSideController<Author> {
    public TextField id, name, address, phone, email, description;

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void setSelectedItem(Author item) {
        this.selectedItem = item;
    }

    @Override
    public void fillFields() {
        id.setText(String.valueOf(selectedItem.getId()));
        name.setText(selectedItem.getName());
        address.setText(selectedItem.getAddress());
        phone.setText(selectedItem.getPhone());
        email.setText(selectedItem.getEmail());
        description.setText(selectedItem.getDescription());
    }

    @Override
    public void handleSave() throws ServiceException, SQLException {
        selectedItem = AuthorService.save(new Author(
                Integer.parseInt(id.getText()),
                name.getText(),
                address.getText(),
                phone.getText(),
                email.getText(),
                description.getText()
        ));
        managementController.refresh();
    }

    @Override
    public void handleEdit() throws ServiceException {
        name.setEditable(true);
        address.setEditable(true);
        phone.setEditable(true);
        email.setEditable(true);
        description.setEditable(true);
    }

    @Override
    public void handleDelete() throws ServiceException, SQLException {
        AuthorService.delete(selectedItem.getId());
        managementController.refresh();
    }

    @Override
    public void clearFields() {
        id.clear();
        name.clear();
        address.clear();
        phone.clear();
        email.clear();
        description.clear();
    }


    @Override
    public void setManagementController(ManagementController<Author> managementController) {
        this.managementController = managementController;
    }
}
