package st.librarymanagement.Controller.Center;

import com.google.protobuf.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.Right.AuthorDetailSideController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Dto.FineDto;
import st.librarymanagement.Entity.Author;
import st.librarymanagement.Service.AuthorService;
import st.librarymanagement.Util.LoadView;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class AuthorController extends ManagementController<Author> {
    public TableColumn<Author, String> name, email, phone;
    @FXML
    private TableView<Author> table;

    @FXML
    private TableColumn<Author, Integer> colId;
    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize() throws ServiceException, SQLException, IOException {
        super.initialize();
        setTableData();
        loadRightSide();

        txtSearch.setPromptText("Search by Name");
        txtSearch.textProperty().addListener(
                (observable, oldValue, newValue)
                        -> {
                    try {
                        search(newValue);
                    } catch (ServiceException | SQLException e) {
                        e.printStackTrace();
                    }
                });

        table.setRowFactory(tv -> {
            TableRow<Author> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                    Author clickedRow = row.getItem();
                    // Gọi phương thức để cập nhật giao diện phía bên phải
                    updateRightView(clickedRow);
                }
            });
            return row;
        });

    }

    @Override
    protected void setTableData() throws ServiceException, SQLException {
        list = FXCollections.observableArrayList(AuthorService.getAll());
        table.setItems(list);
        name.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        email.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        phone.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
    }

    @Override
    protected void search(String keyword) throws ServiceException, SQLException {
         ObservableList<Author> filteredAuthors = FXCollections.observableArrayList();
        if (keyword == null || keyword.isEmpty()) {
            // Nếu không có từ khóa, hiển thị toàn bộ danh sách gốc
            filteredAuthors.setAll(list);
        } else {
            // Lọc danh sách dựa trên từ khóa
            filteredAuthors.setAll(list.stream()
                    .filter(author -> author.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList());
        }
        table.setItems(filteredAuthors);
    }

    @Override
    protected void add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/AuthorDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            AuthorDetailSideController controller = loader.getController();
            controller.setManagementController(this);
            rightPane.getChildren().setAll(detailsPane);
            controller.handleEdit();
            controller.id.setText("0");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updatePagination(ObservableList<Author> list) {
        super.updatePagination(list);
    }

    private void loadRightSide() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/AuthorDetail.fxml"));
        if (loader.getLocation() == null) {
            log.error("Failed to load right side view");
        } else {
            AnchorPane rightPaneContent = loader.load();
            rightPane.getChildren().setAll(rightPaneContent);
        }
    }

    private void updateRightView(Author author) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/AuthorDetail.fxml"));
            AnchorPane detailsPane = loader.load();

            RightSideController<Author> controller = loader.getController();
            controller.setManagementController(this);
            if (author != null) {
                log.info("Author selected: {}", author);
                controller.setSelectedItem(author);
                controller.fillFields();
            } else {
                log.info("No author selected");
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