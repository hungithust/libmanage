package st.librarymanagement.Controller.Right;

import com.google.protobuf.ServiceException;
import javafx.scene.control.TextField;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Entity.Member;
import st.librarymanagement.Entity.Publisher;
import st.librarymanagement.Entity.Status.MemberStatus;
import st.librarymanagement.Service.MemberService;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MemberDetailSideController extends RightSideController<Member> {
    public TextField id, name, address, phone, email, status, joinDate;
    @Override
    public void initialize() {
        super.initialize();

    }

    @Override
    public void setSelectedItem(Member item) {
        if (item != null) {
            if (selectedItem == null) {
                selectedItem = new Member();
            }
            selectedItem.setId(item.getId());
            selectedItem.setName(item.getName());
            selectedItem.setAddress(item.getAddress());
            selectedItem.setPhone(item.getPhone());
            selectedItem.setEmail(item.getEmail());
            selectedItem.setStatus(item.getStatus());
            selectedItem.setJoinDate(item.getJoinDate());
        }
    }

    @Override
    public void fillFields() {
        id.setText(String.valueOf(selectedItem.getId()));
        name.setText(selectedItem.getName());
        address.setText(selectedItem.getAddress());
        phone.setText(selectedItem.getPhone());
        email.setText(selectedItem.getEmail());
        status.setText(selectedItem.getStatus().toString());
        joinDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(selectedItem.getJoinDate()));
    }

    @Override
    public void handleSave() throws ParseException, SQLException, ServiceException {
        selectedItem = MemberService.save(new Member(
                Integer.parseInt(id.getText()),
                name.getText(),
                address.getText(),
                phone.getText(),
                email.getText(),
                new SimpleDateFormat("yyyy-MM-dd").parse(joinDate.getText()),
                MemberStatus.valueOf(status.getText())));
        managementController.refresh();
    }

    @Override
    public void handleEdit() {
        name.setEditable(true);
        address.setEditable(true);
        phone.setEditable(true);
        email.setEditable(true);
        status.setEditable(true);
        joinDate.setEditable(true);

    }

    @Override
    public void handleDelete() throws ServiceException, SQLException {
        MemberService.delete(Integer.parseInt(id.getText()));
        managementController.refresh();
    }

    @Override
    public void clearFields() {
        id.clear();
        name.clear();
        address.clear();
        phone.clear();
        email.clear();
        status.clear();
        joinDate.clear();
    }

    @Override
    public void setManagementController(ManagementController<Member> managementController) throws ServiceException {
        this.managementController = managementController;
    }
}
