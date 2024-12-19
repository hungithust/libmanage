package st.librarymanagement.Controller;

import com.google.protobuf.ServiceException;
import javafx.fxml.FXML;
import st.librarymanagement.Util.LoadView;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.SQLException;

public abstract class SideBar extends Controller {

    @Override
    public void initialize() throws ServiceException, SQLException, IOException {
        LoadView loadView = new LoadView();
        btnAuthor.setOnAction(e-> load(() -> loadView.loadAuthorView(e, this)));
        btnBook.setOnAction(e -> load(() -> loadView.loadBookView(e, this)));
        btnPublisher.setOnAction(e -> load(() -> loadView.loadPublisherView(e, this)));
        btnMember.setOnAction(e -> load(() -> loadView.loadMemberView(e, this)));
        btnBorrow.setOnAction(e -> load(() -> loadView.loadBorrowView(e, this)));
        btnFine.setOnAction(e -> load(() -> loadView.loadFineView(e, this)));
        btnDashboard.setOnAction(e -> load(() -> loadView.loadDashboardView(e, this)));
    }

    private void load(Action action) {
        try {
            action.execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load: " + e.getMessage(), e);
        }
    }

    @FunctionalInterface
    private interface Action {
        void execute() throws IOException;
    }

    @FXML protected Button btnDashboard;
    @FXML protected Button btnAuthor;
    @FXML protected Button btnBook;
    @FXML protected Button btnPublisher;
    @FXML protected Button btnMember;
    @FXML protected Button btnBorrow;
    @FXML protected Button btnFine;

    @FXML protected Button btnSetting;
    @FXML protected Button btnHelp;
    @FXML protected Button btnLogout;
}


