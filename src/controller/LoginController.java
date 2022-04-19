package controller;

import helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    @FXML private TextField passwordField;
    @FXML private TextField useridField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


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

       /*
        try
        {

        }
        catch ()
        {

        }
        */
    }

    /**
     * Displays error message prompts for various errors.
     */
    private void errorPopup(int alertNum) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Alert alertError = new Alert(Alert.AlertType.ERROR);

        switch (alertNum) {
            case 1:
                alertError.setTitle("Error: Empty Field");
                alertError.setHeaderText("User ID or Password field is empty.");
                alertError.showAndWait();
                break;
            case 2:
                alertError.setTitle("Error");
                alertError.setHeaderText("Login Attempt Fail");
                alertError.showAndWait();
                break;
            case 3:
                alertError.setTitle("Success");
                alertError.setHeaderText("Login Attempt Succesful");
                alertError.showAndWait();
                break;
        }
    }
}
