package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.Appointments;
import main.Users;

/**
 * FXML controller class for Login Screen.
 *
 * @author Najee Burnette
 */

public class LoginController implements Initializable
{
    Users currentUser;
    ObservableList<Appointments> alertList = FXCollections.observableArrayList();
    private boolean loginAttempt = false;
    DateTimeFormatter loginTrackerFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

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

        rb = ResourceBundle.getBundle("properties/lang", Locale.getDefault());
        userIdLabel.setText(rb.getString("UserID"));
        loginButton.setText(rb.getString("Login"));
        labelLocale.setText(rb.getString("Locale"));
        exitButton.setText(rb.getString("Exit"));
        labelLocaleStatus.setText(rb.getString("LocaleStatus"));
        passwordLabel.setText(rb.getString("Password"));
    }


    /**
     * Reads user ID and password and provides an appropriate error messages when criteria are met.
     * <p>
     * The user ID and password is matched with the database using a prepared
     * statement and a SELECT query. An error message is provided if no match is
     * found or if any of the fields are left blank.
     *</p>
     *
     * @param event when the use  presses the "Login" Button
     * @throws IOException throws when input or output operation is failed or error interpreted
     * @throws SQLException throws when sql operation is failed or error interpreted
     */
    @FXML
    void onActionLogin(ActionEvent event) throws IOException, SQLException
    {



        if(useridField.getText().trim().isBlank() || passwordField.getText().trim().isBlank()) {
            errorPopup(1);
        }

        int userID = Integer.parseInt(useridField.getText().trim());
        String passwordText = passwordField.getText().trim();


        PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM users WHERE User_ID = ? AND Password = ?");

        ps.setInt(1, userID);
        ps.setString(2, passwordText);

        ResultSet rs = ps.executeQuery();

        if(!rs.next()) {
            loginAttempt = false;
            loginTracker(loginAttempt);
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
                loginTracker(loginAttempt);
                appointmentAlert();
                toMain();
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

        ResourceBundle rb = ResourceBundle.getBundle("properties/lang", Locale.getDefault());

        Alert alert = new Alert(Alert.AlertType.ERROR);

        switch (alertNum) {
            case 1:
                alert.setTitle(rb.getString("errorTitle1"));
                alert.setHeaderText(rb.getString("errorPopup1"));
                alert.showAndWait();
                break;
            case 2:
                alert.setTitle("errorTitle2");
                alert.setHeaderText("errorPopup2");
                alert.showAndWait();
                break;
        }
    }

    /**
     * Switches screens to the login menu.
     * @throws IOException
     */
    private void toMain() throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Main Menu.fxml"));
        Stage stage = (Stage)loginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Alerts the user upon a successful of any appointments that are are are scheduled
     * withing 15 minutes of logging in
     */
    public void appointmentAlert()
    {
        DateTimeFormatter alertFormat = DateTimeFormatter.ofPattern("dd MMM'.' yyyy 'at' HH:mm'.'");
        try
        {
            PreparedStatement ps =JDBC.connection.prepareStatement(
                    "SELECT * FROM APPOINTMENTS WHERE Start BETWEEN NOW() "
                            + "AND ADDDATE(NOW(), INTERVAL 15 MINUTE)");
            ResultSet rs = ps.executeQuery();

            if(!rs.next())
            {
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Appointment Reminder");
                alert2.setHeaderText("No Upcoming Appointments");
                alert2.showAndWait();
            }
            else
            {
                do
                {
                    int appointmentID = rs.getInt("Appointment_ID");
                    LocalDateTime alertDate = rs.getTimestamp("Start").toLocalDateTime();

                    alertList.add(new Appointments(appointmentID, alertDate));

                    String formatted = alertDate.format(alertFormat);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointment Reminder");
                    alert.setHeaderText("Appointment starting within 15 minutes!");
                    alert.setContentText("Appointment: #" + appointmentID  + "\n Scheduled on: " + formatted);
                    alert.showAndWait();
                }
                while(rs.next());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error with SQL!");
        }
    }

    /**
     * Tracks user login activity and saves successful and failed login attempts to
     * login_activity.
     *
     * @param attempt the login ID
     * @throws IOException when the file cannot be found
     */
    private void loginTracker(boolean attempt) throws IOException
    {
        FileWriter fileName = new FileWriter("login_activity.txt", true);
        BufferedWriter loginAttempt = new BufferedWriter(fileName);

        if(attempt == true)
        {
            loginAttempt.append(LocalDateTime.now().format(loginTrackerFormat)
                    + " Login Attempt on User: " + currentUser.getuserName() + " -- Successful!");
            loginAttempt.newLine();
            loginAttempt.close();
        }
        else if(attempt == false)
        {
            loginAttempt.append(LocalDateTime.now().format(loginTrackerFormat)
                    + " Login Attempt on User: " + currentUser.getuserName() + " -- Failed Attempt!");

            loginAttempt.newLine();
            loginAttempt.close();
        }
    }
}
