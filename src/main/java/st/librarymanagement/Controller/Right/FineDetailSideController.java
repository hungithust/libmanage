package st.librarymanagement.Controller.Right;

import com.google.protobuf.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Dto.FineDto;
import st.librarymanagement.Entity.Fine;
import st.librarymanagement.Entity.Status.FineStatus;
import st.librarymanagement.Service.FineService;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FineDetailSideController extends RightSideController<FineDto> {
    public TextField id, memberId, amount, status, loanId, reason, fineDate, memberName;

    @Override
    public void initialize() {
        super.initialize();
        btnPay.setOnAction(e -> {
            try {
                handePay();
            } catch (ServiceException | SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public void setSelectedItem(FineDto item) {
        if (selectedItem == null) {
            selectedItem = new FineDto();
        }
        selectedItem.setId(item.getId());
        selectedItem.setMemberId(item.getMemberId());
        selectedItem.setAmount(item.getAmount());
        selectedItem.setStatus(item.getStatus());
        selectedItem.setLoanId(item.getLoanId());
        selectedItem.setReason(item.getReason());
        selectedItem.setFineDate(item.getFineDate());
        selectedItem.setMemberName(item.getMemberName());
    }

    @Override
    public void fillFields() {
        id.setText(String.valueOf(selectedItem.getId()));
        memberId.setText(String.valueOf(selectedItem.getMemberId()));
        amount.setText(String.valueOf(selectedItem.getAmount()));
        status.setText(selectedItem.getStatus().toString());
        loanId.setText(String.valueOf(selectedItem.getLoanId()));
        reason.setText(selectedItem.getReason());
        fineDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(selectedItem.getFineDate()));
        memberName.setText(selectedItem.getMemberName());
    }

    @Override
    public void handleSave() throws SQLException, ParseException, ServiceException {
        selectedItem = FineService.save(new Fine(
                Integer.parseInt(id.getText()),
                Integer.parseInt(loanId.getText()),
                Integer.parseInt(memberId.getText()),
                Double.parseDouble(amount.getText()),
                reason.getText(),
                new SimpleDateFormat("yyyy-MM-dd").parse(fineDate.getText()),
                FineStatus.valueOf(status.getText())
        ));
        managementController.refresh();
    }

    @Override
    public void handleEdit() {
        memberId.setEditable(true);
        amount.setEditable(true);
        status.setEditable(true);
        loanId.setEditable(true);
        reason.setEditable(true);
        fineDate.setEditable(true);

    }

    @Override
    public void handleDelete() throws ServiceException, SQLException {
        FineService.delete(selectedItem.getId());
        managementController.refresh();
    }

    @Override
    public void clearFields() {
        id.clear();
        memberId.clear();
        amount.clear();
        status.clear();
        loanId.clear();
        reason.clear();
        fineDate.clear();
    }

    @FXML
    Button btnPay;

    public void handePay() throws ServiceException, SQLException {
        FineService.pay(selectedItem.getId());
        managementController.refresh();
    }

    @Override
    public void setManagementController(ManagementController<FineDto> managementController) throws ServiceException {
        this.managementController = managementController;
    }


}
