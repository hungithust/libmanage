package st.librarymanagement.Controller;

import com.google.protobuf.ServiceException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.SQLException;

public abstract class ManagementController<T> extends SideBar {

    @FXML    protected AnchorPane rightPane;

    @FXML    protected BorderPane borderPane;

    protected static int maxRow = 15;

    protected ObservableList<T> list;
    protected abstract void setTableData() throws ServiceException, SQLException;

    @FXML protected Pagination pagination;
    @FXML protected TableColumn<T, Integer> colId;
    @FXML protected TableView<T> table;

//    @FXML protected Button btnRefresh;
    public void refresh() throws ServiceException, SQLException {
        setTableData();
    }

    @FXML protected TextField txtSearch;

    protected abstract void search(String keyword) throws ServiceException, SQLException;

    @FXML protected Button btnAdd;
    protected abstract void add();

    protected void updatePagination(ObservableList<T> list){
        pagination.setPageCount((list.size() / maxRow) + 1);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * maxRow;
            int toIndex = Math.min(fromIndex + maxRow, list.size());
            table.setItems((ObservableList<T>) list.subList(fromIndex, toIndex));
            return table;
        });
    }

    protected TableView<T> getTable(){
        int startIdx = pagination.getCurrentPageIndex() * maxRow;
        int endIdx = Math.min(startIdx + maxRow, list.size());
        ObservableList<T> subList = FXCollections.observableList(list.subList(startIdx, endIdx));
        table.setItems(subList);
        colId.setCellValueFactory(cellData -> {
            return new SimpleIntegerProperty(list.indexOf(cellData.getValue()) + 1).asObject();
        });
        return table;
    }

    public void initialize() throws ServiceException, SQLException, IOException {
        super.initialize();
        btnAdd.setOnAction(event -> add());
        pagination.setStyle("-fx-page-information-visible: false; -fx-page-button-pref-height: 50px; -fx-background-color: #FFFFFF;" +
                " -fx-border-radius: 10; -fx-background-radius: 10; -fx-text-fill: #002060; -fx-font-size: 15;");
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * maxRow;
            int toIndex = Math.min(fromIndex + maxRow, list.size());
            table.setItems(FXCollections.observableList(list.subList(fromIndex, toIndex)));
            return new BorderPane(table);
        });
    }


    public void setRightPanel(Pane rightPanel) {
        borderPane.setRight(rightPanel);
    }
}