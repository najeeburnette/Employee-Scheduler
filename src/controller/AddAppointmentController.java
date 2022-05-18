package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.Contacts;
import main.Customers;
import main.Users;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {

    Users currentUser;
    Customers selectedCustomer;
    private ObservableList<Contacts> contactList = FXCollections.observableArrayList();
    private ObservableList<Users> userList = FXCollections.observableArrayList();
    private ObservableList<Customers> customersList = FXCollections.observableArrayList();
    private ObservableList<LocalTime> startTimeList = FXCollections.observableArrayList();
    private ObservableList<LocalTime> endTimeList = FXCollections.observableArrayList();

    @FXML private Button cancelButton;

    @FXML private ComboBox<String> contactComboBox;
    @FXML private Label contactLabel;

    @FXML private ComboBox<Integer> customerComboBox;
    @FXML private Label customerLabel;

    @FXML private DatePicker datePicker;

    @FXML private TextField descriptionField;
    @FXML private Label descriptionLabel;

    @FXML private ComboBox<LocalTime> endTimeComboBox;
    @FXML private Label endLabel;

    @FXML private Label errorLabel;

    @FXML private TextField locationField;
    @FXML private Label locationLabel;

    @FXML private Button saveButton;

    @FXML private ComboBox<LocalTime> startTimeComboBox;
    @FXML private Label startLabel;

    @FXML private TextField titleField;
    @FXML private Label titleLabel;

    @FXML private TextField typeField;
    @FXML private Label typeLabel;

    @FXML private ComboBox<Integer> userComboBox;
    @FXML private Label userLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try { populateContactCombobox(); }
        catch (SQLException throwables) { throwables.printStackTrace(); }

        try { populateUserComboBox(); }
        catch (SQLException throwables) { throwables.printStackTrace(); }

        try { populateCustomerComboBox(); }
        catch (SQLException throwables) { throwables.printStackTrace(); }

        populateTimeComboBoxes();

    }


    /**
     * This method returns the user back to the main controller without adding an Appointment
     * @param actionEvent applied to Cancel Button
     * @throws IOException
     */
    public void onActionCancelButton (javafx.event.ActionEvent actionEvent) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Main Menu.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method returns the user back to the Main Menu after an Appointment is added.
     * @throws IOException
     */
    private void returnToMain() throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Main Menu.fxml"));
        Stage stage = (Stage)saveButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Populates contact combo box with data pulled from the database using a prepared statement.
     * @throws SQLException
     */
    @FXML private void populateContactCombobox()  throws SQLException
    {
        ObservableList<String> contactCombo = FXCollections.observableArrayList();

        try
        {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * " + "FROM contacts");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int contactID = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                String email = rs.getString("Email");

                contactCombo.add(rs.getString("Contact_Name"));

                contactList.add(new Contacts(contactID, contactName, email));
            }
            contactComboBox.setItems(contactCombo);
        }
        catch(SQLException e) { System.out.println("SQL Error!"); }
    }



    /**
     * Populates the start and end time combo boxes with times of 30 minute intervals.
     * As well as sets the date picker to the current date by default
     */
    @FXML private void populateTimeComboBoxes()
    {
        LocalTime times = LocalTime.of(0,0);

        while(!times.equals(LocalTime.of(23, 30)))
        {
            startTimeList.add(times);
            endTimeList.add(times);
            times = times.plusMinutes(30);
        }

        datePicker.setValue(LocalDate.now());
        startTimeComboBox.setItems(startTimeList);
        endTimeComboBox.setItems(endTimeList);
    }

    /**
     * pulls all the customers from the database to populate the customers combo box.
     * @throws SQLException
     */

    @FXML private void populateCustomerComboBox() throws SQLException
    {
        ObservableList<Integer> customerCombo = FXCollections.observableArrayList();

        try
        {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT customers.Customer_ID "
                    + "FROM CUSTOMERS ORDER BY Customer_ID");

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                customerCombo.add(rs.getInt("Customer_ID"));
            }

            customerComboBox.setItems(customerCombo);
        }
        catch (SQLException ex) { System.out.println("SQL Error"); }
    }

    /**
     * Populates user combo box with data pulled from the database using a prepared statement.
     * @throws SQLException
     */
    @FXML private void populateUserComboBox() throws SQLException
    {
        ObservableList<Integer> userCombo = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT users.User_ID "
                    + "FROM USERS ORDER BY User_ID");
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                userCombo.add(rs.getInt("User_ID"));
            }

            userComboBox.setItems(userCombo);
        }
        catch (SQLException e) { System.out.println("SQL Error"); }
    }
}