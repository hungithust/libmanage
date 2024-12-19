package st.librarymanagement.Controller.Right;

import com.google.protobuf.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Dto.LoanDto;
import st.librarymanagement.Entity.Loan;
import st.librarymanagement.Entity.Status.LoanStatus;
import st.librarymanagement.Service.LoanService;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
public class LoanDetailSideController extends RightSideController<LoanDto> {

    public TextField id,bookId, memberId, bookName, loanId, loanDate, returnDate, status, dueDate, memberName;


    @FXML    private Button btnReturn, btnRenew;
    @Override
    public void initialize() {
        super.initialize();
        btnReturn.setOnAction(event -> {
            try {
                handleReturn();
            } catch (ServiceException | SQLException e) {
                e.printStackTrace();
            }
        });
        btnRenew.setOnAction(event -> {
            try {
                handeRenew();
            } catch (ServiceException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setSelectedItem(LoanDto item) {
        this.selectedItem = item;
    }

    @Override
    public void fillFields() {
        log.info("Đang điền dữ liệu");
        id.setText(String.valueOf(selectedItem.getId()));
        bookName.setText(selectedItem.getBookTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        bookId.setText(String.valueOf(selectedItem.getBookId()));
        memberId.setText(String.valueOf(selectedItem.getMemberId()));
        loanDate.setText(dateFormat.format(selectedItem.getLoanDate()));

        if (selectedItem.getReturnDate() != null) {
            returnDate.setText(dateFormat.format(selectedItem.getReturnDate()));
        } else {
            returnDate.clear();
        }

        status.setText(selectedItem.getStatus().toString());
        dueDate.setText(dateFormat.format(selectedItem.getDueDate()));
        memberName.setText(selectedItem.getMemberName());
    }

    @Override
    public void handleSave() throws SQLException, ParseException, ServiceException {
        selectedItem = LoanService.save(new Loan(
                Integer.parseInt(id.getText()),
                Integer.parseInt(bookId.getText()),
                Integer.parseInt(memberId.getText()),
                new SimpleDateFormat("yyyy-MM-dd").parse(loanDate.getText()),
                new SimpleDateFormat("yyyy-MM-dd").parse(returnDate.getText()),
                new SimpleDateFormat("yyyy-MM-dd").parse(dueDate.getText()),
                LoanStatus.valueOf(status.getText())));
    }

    @Override
    public void handleEdit() {
        bookId.setEditable(true);
        memberId.setEditable(true);
        loanDate.setEditable(true);
        returnDate.setEditable(true);
        status.setEditable(true);
        dueDate.setEditable(true);
    }

    @Override
    public void handleDelete() throws ServiceException, SQLException {
        LoanService.delete(selectedItem.getId());
        managementController.refresh();
    }

    @Override
    public void clearFields() {
        id.clear();
        bookId.clear();
        memberId.clear();
        loanDate.clear();
        returnDate.clear();
        status.clear();
        dueDate.clear();
    }

    public void handeRenew() throws ServiceException, SQLException {
        if (selectedItem != null && selectedItem.getStatus() == LoanStatus.Active) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedItem.getDueDate());
            selectedItem.setDueDate(cal.getTime());
            LoanService.renewLoan(selectedItem.getId());
            fillFields();  // Cập nhật lại các trường thông tin trên giao diện
            managementController.refresh();
        }
    }

    public void handleReturn() throws ServiceException, SQLException {
        if (selectedItem != null && selectedItem.getStatus() == LoanStatus.Active) {
            selectedItem.setReturnDate(Calendar.getInstance().getTime());
            LoanService.returnLoan(selectedItem.getId());
            fillFields();  // Cập nhật lại các trường thông tin trên giao diện
            managementController.refresh();
        }
    }
    @Override
    public void setManagementController(ManagementController<LoanDto> managementController) throws ServiceException {
        this.managementController = managementController;
    }
}
