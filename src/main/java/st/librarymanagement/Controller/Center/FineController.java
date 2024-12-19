package st.librarymanagement.Controller.Center;

import com.google.protobuf.ServiceException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.Right.FineDetailSideController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Dto.FineDto;
import st.librarymanagement.Entity.Member;
import st.librarymanagement.Entity.Publisher;
import st.librarymanagement.Service.FineService;
import javafx.collections.FXCollections;
import st.librarymanagement.Util.LoadView;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class FineController extends ManagementController<FineDto> {

    public TableColumn<FineDto, String> loanId, memberName, bookTitle, amount, status, reason, date;
    public ChoiceBox<String> statusChoice;
    @FXML
    private TableView<FineDto> table;
    @FXML
    private BorderPane borderPane;
    @Override
    protected void setTableData() {
        list = FXCollections.observableArrayList(FineService.getAllFines());
        table.setItems(list);
        loanId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getLoanId())));
        memberName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getMemberName()));
        bookTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        amount.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getAmount())));
        status.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));
        reason.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getReason()));
        date.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFineDate().toString()));
    }

    @Override
    protected void add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/FineDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            FineDetailSideController controller = loader.getController();
            controller.setManagementController(this);
            rightPane.getChildren().setAll(detailsPane);
            controller.handleEdit();
            controller.id.setText("0");
            controller.status.setText("Unpaid");
            controller.fineDate.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void search(String keyword) {
        // Implement search logic here
        // Example:
        ObservableList<FineDto> filteredFines = FXCollections.observableArrayList();
        if (keyword == null || keyword.isEmpty()) {
            // Nếu không có từ khóa, hiển thị toàn bộ danh sách gốc
            filteredFines.setAll(list);
        } else {
            // Lọc danh sách dựa trên từ khóa
            filteredFines.setAll(list.stream()
                    .filter(fine -> Integer.toString(fine.getId()).contains(keyword) ||
                            String.valueOf(fine.getLoanId()).contains(keyword) ||
                            fine.getMemberName().toLowerCase().contains(keyword.toLowerCase()) ||
                            fine.getBookTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .toList());
        }
        table.setItems(filteredFines);
    }

    @Override
    public void initialize() throws ServiceException, SQLException, IOException {
        super.initialize();
        setTableData();
        loadRightSide();
        table.setRowFactory(tv -> {
            TableRow<FineDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                    FineDto clickedRow = row.getItem();
                    updateRightView(clickedRow);
                }
            });
            return row;
        });
        txtSearch.setPromptText("Search by Id, LoanId, Member Name or Book Title");
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
        });
        statusChoice.setItems(FXCollections.observableArrayList("Status", "Paid", "Unpaid"));
        statusChoice.setValue("Status");
        statusChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equalsIgnoreCase("Status")) {
                    table.setItems(list);
                } else if (newValue.equalsIgnoreCase("Paid")) {
                    filterByStatus("Paid");
                } else if (newValue.equalsIgnoreCase("Unpaid")) {
                    filterByStatus("Unpaid");
                }
            }
        });
    }

    @Override
    protected void updatePagination(ObservableList<FineDto> list) {
        super.updatePagination(list);
    }

    private void updateRightView(FineDto fine) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/FineDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            RightSideController<FineDto> controller = loader.getController();
            controller.setManagementController(this);
            controller.setSelectedItem(fine);
            if (fine != null) {
                log.info("Selected Fine: {}", fine);
                controller.setSelectedItem(fine);
                controller.fillFields();
            } else {
                log.info("No Fine selected");
                controller.clearFields(); // Giả sử bạn có phương thức để xóa hiển thị
            }
            rightPane.getChildren().setAll(detailsPane);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    private void filterByStatus(String unpaid) {
        ObservableList<FineDto> filteredFines = FXCollections.observableArrayList();
        filteredFines.setAll(list.stream()
                .filter(fine -> fine.getStatus().toString().equalsIgnoreCase(unpaid))
                .toList());
        table.setItems(filteredFines);
    }

    private void loadRightSide() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/FineDetail.fxml"));
        if (loader.getLocation() == null) {
            log.error("Failed to load right side view");
        } else {
            AnchorPane rightPaneContent = loader.load();
            rightPane.getChildren().setAll(rightPaneContent);
        }
    }

}

