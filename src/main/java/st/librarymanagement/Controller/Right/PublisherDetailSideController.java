package st.librarymanagement.Controller.Right;

import com.google.protobuf.ServiceException;
import javafx.scene.control.TextField;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Entity.Publisher;
import st.librarymanagement.Service.PublisherService;

import java.sql.SQLException;

public class PublisherDetailSideController extends RightSideController<Publisher> {
    public TextField id, name, address, email, contact;

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void setSelectedItem(Publisher item) {
        if (item != null) {
            if (selectedItem == null) {
                selectedItem = new Publisher();
            }
            selectedItem.setId(item.getId());
            selectedItem.setName(item.getName());
            selectedItem.setAddress(item.getAddress());
            selectedItem.setContact(item.getContact());
            selectedItem.setEmail(item.getEmail());
        }
    }


    @Override
    public void fillFields() {
        id.setText(String.valueOf(selectedItem.getId()));
        name.setText(selectedItem.getName());
        address.setText(selectedItem.getAddress());
        contact.setText(selectedItem.getContact());
        email.setText(selectedItem.getEmail());
    }

    @Override
    public void handleSave() throws SQLException, ServiceException {
        PublisherService.save(new Publisher(
                Integer.parseInt(id.getText()),
                name.getText(),
                address.getText(),
                contact.getText(),
                email.getText()
        ));
        managementController.refresh();
    }

    @Override
    public void handleEdit() {
        name.setEditable(true);
        address.setEditable(true);
        contact.setEditable(true);
        email.setEditable(true);
    }

    @Override
    public void handleDelete() throws ServiceException, SQLException {
        PublisherService.delete(Integer.parseInt(id.getText()));
        clearFields();
        managementController.refresh();
    }

    @Override
    public void clearFields() {
        id.clear();
        name.clear();
        address.clear();
        contact.clear();
        email.clear();
    }



    @Override
    public void setManagementController(ManagementController<Publisher> managementController) throws ServiceException {
        this.managementController = managementController;
    }


}
