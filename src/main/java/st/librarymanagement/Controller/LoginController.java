package st.librarymanagement.Controller;

import com.google.protobuf.ServiceException;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import st.librarymanagement.Config.DatabaseConnection;
import st.librarymanagement.Util.LoadView;

import java.io.IOException;
import java.sql.*;

public class LoginController extends Controller {
    @FXML
    private TextField user_name;

    @FXML
    private PasswordField user_password;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;


    public PreparedStatement prepare;

    public void login(){
        String name = user_name.getText();
        String pwd = user_password.getText();
        String sql = "Select * from librarians where username = ? and password = ?";

        try{
            DatabaseConnection connection = new DatabaseConnection();
            prepare = connection.prepareStatement(sql);
            prepare.setString(1, name);
            prepare.setString(2, pwd);
            ResultSet result = prepare.executeQuery();

            if( name.isEmpty() || pwd.isEmpty() ) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Admin");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields.");
                alert.showAndWait();
            }else {
                if(result.next()){

                    Stage currentStage = (Stage) loginButton.getScene().getWindow();
                    currentStage.close();

//                    LoadView.load("/View/Center/Dashboard.fxml", new DashboardController());
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Admin");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong Username or Password");
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonLoginActionPerformed(ActionEvent event) {
        login();
    }

    @Override
    public void initialize() throws ServiceException, SQLException, IOException {

    }
}





