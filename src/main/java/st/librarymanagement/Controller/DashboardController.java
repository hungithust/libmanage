package st.librarymanagement.Controller;

import com.google.protobuf.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import st.librarymanagement.Controller.SideBar;
import st.librarymanagement.Dto.BookDto;
import st.librarymanagement.Dto.LoanDto;
import st.librarymanagement.Service.BookService;
import st.librarymanagement.Service.LoanService;
import st.librarymanagement.Service.MemberService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class DashboardController extends SideBar {
    public AnchorPane dashBoard;

    public ObservableList<LoanDto> listOverdueLoan;

    public TableView<LoanDto> overdueLoan;
    public TableColumn<LoanDto, Integer> overdueLoanId;
    public TableColumn<LoanDto, String> overdueLoanBookTitle;
    public TableColumn<LoanDto, String> overdueLoanMemberName;
    public TableColumn<LoanDto, String> overdueLoanDate;
    public TableColumn<LoanDto, String> overdueLoanDueDate;


    public ObservableList<BookDto> listNewBook;
    public TableView<BookDto> newBook;
    public TableColumn<BookDto, String> newBookTitle;
    public TableColumn<BookDto, String> newBookAuthor;
    public TableColumn<BookDto, String> newBookCategory;
    public TableColumn<BookDto, Integer> newBookYear;

    public ObservableList<BookDto> listPopularBook;
    public TableView<BookDto> popularBook;
    public TableColumn<BookDto, String> popularBookTitle;
    public TableColumn<BookDto, String> popularBookAuthor;
    public TableColumn<BookDto, String> popularBookCategory;
    public TableColumn<BookDto, Integer> popularBookYear;

    public Label countAllLoan, countAllOverDue, countAllNewMember, countAllMember;

    @FXML    private BarChart<String, Number> barChart;
    @FXML    private CategoryAxis xAxis;
    @FXML    private NumberAxis yAxis;
    private void initChart() throws SQLException {

        countAllLoan.setText(String.valueOf(LoanService.getAllLoans().size()));
        countAllOverDue.setText(String.valueOf(LoanService.getOverdueLoans().size()));
        countAllNewMember.setText(String.valueOf(MemberService.getAllMembers().size()));
        countAllMember.setText(String.valueOf(MemberService.getAllMembers().size()));

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Loan");
        series1.getData().add(new XYChart.Data<>("January", 10));
        series1.getData().add(new XYChart.Data<>("February", 15));
        series1.getData().add(new XYChart.Data<>("March", 20));

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Overdue");
        series2.getData().add(new XYChart.Data<>("January", 5));
        series2.getData().add(new XYChart.Data<>("February", 7));
        series2.getData().add(new XYChart.Data<>("March", 12));

        // Thêm series vào BarChart
        barChart.getData().addAll(series1, series2);
        barChart.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/chart.css")).toExternalForm());
    }

    private void initTable() throws SQLException {

        listOverdueLoan = FXCollections.observableArrayList(LoanService.getOverdueLoans());
        overdueLoan.setItems(listOverdueLoan);

        listNewBook = FXCollections.observableArrayList(BookService.getAllBooks());
        newBook.setItems(listNewBook);

        listPopularBook = FXCollections.observableArrayList(BookService.getAllBooks());
        popularBook.setItems(listPopularBook);

        overdueLoan.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/table.css")).toExternalForm());
        newBook.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/table.css")).toExternalForm());
        popularBook.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/table.css")).toExternalForm());

        // Initialize overdueLoan table columns
        overdueLoanId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        overdueLoanBookTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        overdueLoanMemberName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getMemberName()));
        overdueLoanDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLoanDate().toString()));
        overdueLoanDueDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDueDate().toString()));

        // Initialize newBook table columns
        newBookTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        newBookAuthor.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthorName()));
        newBookCategory.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory().toString()));
        newBookYear.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPublishYear()).asObject());

        // Initialize popularBook table columns
        popularBookTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        popularBookAuthor.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthorName()));
        popularBookCategory.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory().toString()));
        popularBookYear.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPublishYear()).asObject());
    }

    @Override
    public void initialize() throws ServiceException, SQLException, IOException {
        super.initialize();
        initChart();
        initTable();
    }
}
