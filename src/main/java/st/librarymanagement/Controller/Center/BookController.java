package st.librarymanagement.Controller.Center;

import com.google.protobuf.ServiceException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import st.librarymanagement.Controller.ManagementController;
import st.librarymanagement.Controller.Right.BookDetailSideController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Service.BookService;
import javafx.collections.FXCollections;
import st.librarymanagement.Util.LoadView;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

@Slf4j
public class BookController extends ManagementController<BookDto> {

    public TableColumn<BookDto, String> title, author, publisher, category, status;
    public TableColumn<BookDto, Integer> bookAvailable;
    public ChoiceBox<String> statusChoice, categoryChoice;
    public void initialize() throws ServiceException, SQLException, IOException {
        super.initialize();
        setTableData();
        loadRightSide();
        table.setRowFactory(tv -> {
            TableRow<BookDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                    BookDto clickedRow = row.getItem();
                    // Gọi phương thức để cập nhật giao diện phía bên phải
                    updateRightView(clickedRow);
                }
            });
            return row;
        });
        statusChoice.setItems(FXCollections.observableArrayList("Status", "Available", "Not Available"));
        statusChoice.setValue("Status");
        statusChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equalsIgnoreCase("Status")) {
                    table.setItems(list);
                } else if (newValue.equalsIgnoreCase("Available")) {
                    filterByStatus("Available");
                } else if (newValue.equalsIgnoreCase("Not Available")) {
                    filterByStatus("Not Available");
                }
            }
        });

        categoryChoice.setItems(FXCollections.observableArrayList("Category","Science", "Art",
                "History", "Technology", "Science Fiction", "Non-Fiction", "Horror", "Fantasy", "Mystery"));
        categoryChoice.setValue("Category");
        categoryChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if(newValue.equalsIgnoreCase("Category")) {
                    table.setItems(list);
                }
                else if (newValue.equalsIgnoreCase("Science")) {
                    filterByCategory("Science");
                } else if (newValue.equalsIgnoreCase("Art")) {
                    filterByCategory("Art");
                } else if (newValue.equalsIgnoreCase("History")) {
                    filterByCategory("History");
                } else if (newValue.equalsIgnoreCase("Technology")) {
                    filterByCategory("Technology");
                } else if (newValue.equalsIgnoreCase("Science Fiction")) {
                    filterByCategory("ScienceFiction");
                } else if (newValue.equalsIgnoreCase("Non-Fiction")) {
                    filterByCategory("Non-Fiction");
                } else if (newValue.equalsIgnoreCase("Horror")) {
                    filterByCategory("Horror");
                } else if (newValue.equalsIgnoreCase("Fantasy")) {
                    filterByCategory("Fantasy");
                } else if (newValue.equalsIgnoreCase("Mystery")) {
                    filterByCategory("Mystery");
                }
            }
        });
        txtSearch.setPromptText("Search by Title or Author");
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                search(newValue);
            } catch (ServiceException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void setTableData() throws SQLException {
        list = FXCollections.observableArrayList(BookService.getAllBooks());
        table.setItems(list);
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        title.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        author.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthorName()));
        publisher.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPublisherName()));
        category.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory().toString()));
        status.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));
        bookAvailable.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantityAvailable()).asObject());
    }
    @Override
    protected void add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/BookDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            BookDetailSideController controller = loader.getController();
            controller.setManagementController(this);
            rightPane.getChildren().setAll(detailsPane);
            controller.handleEdit();
            controller.id.setText("0");
            controller.status.setText("Available");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void search(String keyword) throws ServiceException, SQLException {
        ObservableList<BookDto> filteredBooks = FXCollections.observableArrayList();
        if (keyword == null || keyword.isEmpty()) {
            // Nếu không có từ khóa, hiển thị toàn bộ danh sách gốc
            filteredBooks.setAll(list);
        } else {
            // Lọc danh sách dựa trên từ khóa
            filteredBooks.setAll(list.stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase())
                            || book.getAuthorName().toLowerCase().contains(keyword.toLowerCase())
                            || book.getPublisherName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList());
        }
        table.setItems(filteredBooks);
    }
    private void filterByStatus(String status) {
        ObservableList<BookDto> filteredBooks = FXCollections.observableArrayList();
        filteredBooks.setAll(list.stream()
                .filter(book -> book.getStatus().toString().equalsIgnoreCase(status))
                .toList());
        table.setItems(filteredBooks);
    }
    private void filterByCategory(String category) {
        ObservableList<BookDto> filteredBooks = FXCollections.observableArrayList();
        filteredBooks.setAll(list.stream()
                .filter(book -> book.getCategory().toString().equalsIgnoreCase(category))
                .toList());
        table.setItems(filteredBooks);
    }

    @Override
    protected void updatePagination(ObservableList<BookDto> list) {
        super.updatePagination(list);
    }

    private void loadRightSide() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/BookDetail.fxml"));
        if (loader.getLocation() == null) {
            log.error("Failed to load right side view");
        } else {
            AnchorPane rightPaneContent = loader.load();
            rightPane.getChildren().setAll(rightPaneContent);
        }
    }
    private void updateRightView(BookDto book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/BookDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            RightSideController<BookDto> controller = loader.getController();
            controller.setManagementController(this);
            if (book != null) {
                log.info("Selected book: {}", book);
                controller.setSelectedItem(book);
                controller.fillFields();
            } else {
                log.info("No book selected");
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