package st.librarymanagement.Controller.Center;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.Right.LoanDetailSideController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Dto.LoanDto;
import st.librarymanagement.Entity.Status.LoanStatus;
import st.librarymanagement.Service.LoanService;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleIntegerProperty;
import com.google.protobuf.ServiceException;
import st.librarymanagement.Util.LoadView;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Slf4j
public class LoanController extends ManagementController<LoanDto> {
    public TableColumn<LoanDto, String> loanBookColumn, loanMemberColumn,
            loanDateColumn, loanDueDateColumn, loanReturnDateColumn, loanStatusColumn;
    public ChoiceBox<String> statusChoice;


    @Override
    public void initialize() throws ServiceException, SQLException, IOException {
        super.initialize();
        setTableData();
        loadRightSide();
        table.setRowFactory(tv -> {
            TableRow<LoanDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                    LoanDto clickedRow = row.getItem();
                    // Gọi phương thức để cập nhật giao diện phía bên phải
                    updateRightView(clickedRow);
                }
            });
            return row;
        });

        txtSearch.setPromptText("Search by LoanId, Book Title or Member Name");
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                search(newValue);
            } catch (ServiceException | SQLException e) {
                e.printStackTrace();
            }
        });
        statusChoice.setItems(FXCollections.observableArrayList("Status", "Active", "Returned", "Overdue"));
        statusChoice.setValue("Status");
        statusChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equalsIgnoreCase("Status")) {
                    table.setItems(list);
                } else if (newValue.equalsIgnoreCase("Active")) {
                    filterByStatus("Active");
                } else if (newValue.equalsIgnoreCase("Returned")) {
                    filterByStatus("Returned");
                } else if (newValue.equalsIgnoreCase("Overdue")) {
                    filterByStatus("Overdue");
                }
            }
        });
    }

    @Override
    protected void setTableData() throws SQLException {
        list = FXCollections.observableArrayList(LoanService.getAllLoans());
        table.setItems(list);
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId()).asObject());
        loanBookColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        loanMemberColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getMemberName()));
        loanDateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLoanDate().toString()));
        loanDueDateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDueDate().toString()));
        loanReturnDateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getReturnDate() != null ? data.getValue().getReturnDate().toString() : ""));
        loanStatusColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));
    }

    @Override
    protected void add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/LoanDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            LoanDetailSideController controller = loader.getController();
            controller.setManagementController(this);
            rightPane.getChildren().setAll(detailsPane);
            controller.handleEdit();
            controller.id.setText("0");
            controller.loanDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis())));
            controller.dueDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)));
            controller.status.setText(LoanStatus.Active.toString());
            } catch (IOException e) {
            e.printStackTrace();
            } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void search(String keyword) throws ServiceException, SQLException {
        // Search implementation
        ObservableList<LoanDto> filteredLoans = FXCollections.observableArrayList();
        if (keyword == null || keyword.isEmpty()) {
            filteredLoans.setAll(list);
        } else {
            filteredLoans.setAll(list.stream()
                    .filter(loan -> Integer.toString(loan.getId()).contains(keyword) ||
                            loan.getBookTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                            loan.getMemberName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList());
        }
        table.setItems(filteredLoans);
    }

    @Override
    protected void updatePagination(ObservableList<LoanDto> list) {
        super.updatePagination(list);
    }

    private void filterByStatus(String status) {
        ObservableList<LoanDto> filteredLoans = FXCollections.observableArrayList();
        filteredLoans.setAll(list.stream()
                .filter(loan -> loan.getStatus().toString().equalsIgnoreCase(status))
                .toList());
        table.setItems(filteredLoans);
    }

    private void loadRightSide() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/LoanDetail.fxml"));
        if (loader.getLocation() == null) {
            log.error("Failed to load right side view");
        } else {
            AnchorPane rightPaneContent = loader.load();
            rightPane.getChildren().setAll(rightPaneContent);
        }
    }

    private void updateRightView(LoanDto loan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/LoanDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            RightSideController<LoanDto> controller = loader.getController();
            controller.setManagementController(this);
            if (loan != null) {
                log.info("Selected loan: {}", loan);
                controller.setSelectedItem(loan);
                controller.fillFields();
            } else {
                log.info("No loan selected");
                controller.clearFields(); // Giả sử bạn có phương thức để xóa hiển thị
            }
            // Giả sử bạn có một pane là rightPane là nơi để hiển thị chi tiết
            rightPane.getChildren().setAll(detailsPane);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

}