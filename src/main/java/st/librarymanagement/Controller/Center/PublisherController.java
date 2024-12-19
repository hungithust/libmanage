package st.librarymanagement.Controller.Center;

import com.google.protobuf.ServiceException;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.Right.PublisherDetailSideController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Entity.Publisher;
import st.librarymanagement.Service.PublisherService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.SimpleIntegerProperty;
import st.librarymanagement.Util.LoadView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
public class PublisherController extends ManagementController<Publisher> {
    public TableColumn<Publisher, String> name, email, address, contact;

    @FXML
    private TableView<Publisher> table;
    @FXML
    private TableColumn<Publisher, Integer> colId;
    @FXML
    private BorderPane borderPane;

    @Override
    protected void setTableData() throws SQLException {
        list = FXCollections.observableArrayList(PublisherService.getAll());
        table.setItems(list);
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        name.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        email.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        contact.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getContact()));
    }

    @Override
    protected void add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/PublisherDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            PublisherDetailSideController controller = loader.getController();
            controller.setManagementController(this);
            rightPane.getChildren().setAll(detailsPane);
            controller.handleEdit();
            controller.id.setText("0");
        } catch (IOException | ServiceException e) {
            log.error("Failed to load the right side view", e);
        }
    }

    @Override
    protected void search(String keyword) throws ServiceException, SQLException {
        ObservableList<Publisher> filteredPublishers = FXCollections.observableArrayList();
        if (keyword == null || keyword.isEmpty()) {
            filteredPublishers.setAll(list);
        } else {
            filteredPublishers.setAll(list.stream()
                    .filter(publisher -> Integer.toString(publisher.getId()).contains(keyword) ||
                            publisher.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                            publisher.getEmail().toLowerCase().contains(keyword.toLowerCase()))
                    .toList());
        }
        table.setItems(filteredPublishers);
    }

    @Override
    protected void updatePagination(ObservableList<Publisher> list) {
        super.updatePagination(list);
    }


    @Override
    public void initialize() {
        try {
            super.initialize();
            setTableData();
            loadRightSide();
            table.setRowFactory(tv -> {
                TableRow<Publisher> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                        Publisher clickedRow = row.getItem();
                        updateRightView(clickedRow);
                    }
                });
                return row;
            });

            txtSearch.setPromptText("Search by name or email");
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    search(newValue);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Initialization failure: ", e);
        }
    }

    private void loadRightSide() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/PublisherDetail.fxml"));
        if (loader.getLocation() == null) {
            log.error("Failed to load right side view");
        } else {
            AnchorPane rightPaneContent = loader.load();
            rightPane.getChildren().setAll(rightPaneContent);
        }
    }

    private void updateRightView(Publisher publisher) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/PublisherDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            RightSideController<Publisher> controller = loader.getController();
            controller.setManagementController(this);
            if (publisher != null) {
                log.info("Selected publisher: {}", publisher);
                controller.setSelectedItem(publisher);
                controller.fillFields();
            } else {
                log.info("No publisher selected");
                controller.clearFields(); // Giả sử bạn có phương thức để xóa hiển thị
            }
            rightPane.getChildren().setAll(detailsPane);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
}