package st.librarymanagement.Controller.Center;

import com.google.protobuf.ServiceException;
import javafx.collections.FXCollections;
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
import st.librarymanagement.Controller.Right.MemberDetailSideController;
import st.librarymanagement.Controller.RightSideController;
import st.librarymanagement.Entity.Member;
import st.librarymanagement.Entity.Publisher;
import st.librarymanagement.Entity.Status.MemberStatus;
import st.librarymanagement.Service.MemberService;
import st.librarymanagement.Util.LoadView;


import java.io.IOException;
import java.sql.SQLException;
@Slf4j
public class MemberController extends ManagementController<Member> {

    public TableColumn<Member, String> name, email, phone, joinDate, status;
    @FXML
    private TableView<Member> table;
    @FXML
    private TableColumn<Member, Integer> colId;
    @FXML
    private BorderPane borderPane;

    @Override
    protected void setTableData() throws ServiceException, SQLException {
        list = FXCollections.observableArrayList(MemberService.getAllMembers());
        table.setItems(list);
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        name.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        email.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        phone.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        joinDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getJoinDate().toString()));
        status.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));

    }

    @Override
    protected void add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/MemberDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            MemberDetailSideController controller = loader.getController();
            controller.setManagementController(this);
            rightPane.getChildren().setAll(detailsPane);
            controller.handleEdit();
            controller.id.setText("0");
            controller.joinDate.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis())));
            controller.status.setText(MemberStatus.Active.toString());
        } catch (IOException | ServiceException e) {
            log.error("Failed to load the right side view", e);
        }
    }

    @Override
    protected void updatePagination(ObservableList<Member> list) {
        super.updatePagination(list);
    }

    @Override
    protected void search(String keyword) throws ServiceException, SQLException {
        ObservableList<Member> filteredMembers = FXCollections.observableArrayList();
        if (keyword == null || keyword.isEmpty()) {
            filteredMembers.setAll(list);
        } else {
            filteredMembers.setAll(list.stream()
                    .filter(member -> member.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                            member.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                            member.getPhone().toLowerCase().contains(keyword.toLowerCase()))
                    .toList());
        }
        table.setItems(filteredMembers);
    }

    @Override
    public void initialize() {
        try {
            super.initialize();
            setTableData();
            loadRightSide();
            table.setRowFactory(tv -> {
                TableRow<Member> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                        Member clickedRow = row.getItem();
                        updateRightView(clickedRow);
                    }
                });
                return row;
            });
            txtSearch.setPromptText("Search by Name, Email, Phone");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/MemberDetail.fxml"));
        if (loader.getLocation() == null) {
            log.error("Failed to load right side view");
        } else {
            AnchorPane rightPaneContent = loader.load();
            rightPane.getChildren().setAll(rightPaneContent);
        }
    }

    private void updateRightView(Member member) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Right/MemberDetail.fxml"));
            AnchorPane detailsPane = loader.load();
            RightSideController<Member> controller = loader.getController();
            controller.setManagementController(this);
            controller.setSelectedItem(member);
            if (member != null) {
                log.info("Selected publisher: {}", member);
                controller.setSelectedItem(member);
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
