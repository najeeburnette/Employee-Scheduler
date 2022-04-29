package controller;

import com.mysql.cj.Messages;
import helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.Users;

/**
 * FXML controller class for Login Screen.
 *
 * @author Najee Burnette
 */

public class LoginController implements Initializable
{
    private boolean loginAttempt = false;
    Users currentUser;

    @FXML private Button loginButton;
    @FXML private PasswordField passwordField;
    @FXML private TextField useridField;
    @FXML private Button exitButton;
    @FXML private Label labelLocale;
    @FXML private Label labelLocaleStatus;
    @FXML private Label passwordLabel;
    @FXML private Label userIdLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        rb = ResourceBundle.getBundle("properties/loginForm", Locale.getDefault());
        userIdLabel.setText(rb.getString("UserID"));
        labelLocale.setText(rb.getString("Locale"));
        exitButton.setText(rb.getString("Exit"));
        labelLocaleStatus.setText(rb.getString("LocaleStatus"));
        //passwordLabel.setText(rb.getString("Password"));
    }


    /**
     * Reads user ID and password and provides an appropriate error messages when criteria are met.
     * <p>
     * The user ID and password is matched with the database using a prepared
     * statement and a SELECT query. An error message is provided if no match is
     * found or if any of the fields are left blank.
     *
     * @param event when the use  presses the "Login" Button
     * @throws IOException throws when input or output operation is failed or error interpreted
     * @throws SQLException throws when sql operation is failed or error interpreted
     */
    @FXML
    void onActionLogin(ActionEvent event) throws IOException, SQLException {


        // Prompts an error message if fields are empty
        if(useridField.getText().trim().isBlank() || passwordField.getText().trim().isBlank()) {
            errorPopup(1);
        }

        int userID = Integer.parseInt(useridField.getText().trim());
        String passwordText = passwordField.getText().trim();

        //  Match user ID and Password with database
        PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM users WHERE User_ID = ? AND Password = ?");

        ps.setInt(1, userID);
        ps.setString(2, passwordText);

        ResultSet rs = ps.executeQuery();

        if(!rs.next()) {
            loginAttempt = false;
            errorPopup(2);
        }
        else
        {
            do
            {
                currentUser.setuserID(rs.getInt("User_ID"));
                currentUser.setPassword(rs.getString("Password"));
                currentUser.setuserName(rs.getString("User_Name"));

                loginAttempt = true;
                errorPopup(3);
            }
            while (rs.next());
        }
    }

    /**
     * Exits the application upon button click.
     *
     * @param event when the user clicks on "Exit"
     */
    @FXML
    void onActionExit(ActionEvent event) {
        JDBC.closeConnection();
        System.exit(0);
    }

    /**
     * Displays error message prompts for various errors.
     */
    private void errorPopup(int alertNum) {

        ResourceBundle rb = ResourceBundle.getBundle("properties/loginForm", Locale.getDefault());

        Alert alert = new Alert(Alert.AlertType.ERROR);

        switch (alertNum) {
            case 1:
                alert.setTitle(rb.getString("errorTitle1"));
                alert.setHeaderText(rb.getString("errorPopup1"));
                alert.showAndWait();
                break;
            case 2:
                alert.setTitle("Error");
                alert.setHeaderText("Login Attempt Fail");
                alert.showAndWait();
                break;
            case 3:
                alert.setTitle("Success");
                alert.setHeaderText("Login Attempt Succesful");
                alert.showAndWait();
                break;
        }
    }
}
