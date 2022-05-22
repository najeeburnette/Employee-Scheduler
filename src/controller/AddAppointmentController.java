package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.sql.*;
import java.time.*;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {

    Users currentUser;
    Customers selectedCustomer;
    int appointmentId2 = 0;
    Timestamp start = null;
    Timestamp end = null;
    private ObservableList<Contacts> contactList = FXCollections.observableArrayList();
    private ObservableList<Users> userList = FXCollections.observableArrayList();
    private ObservableList<Customers> customersList = FXCollections.observableArrayList();
    private ObservableList<LocalTime> startTimeList = FXCollections.observableArrayList();
    private ObservableList<LocalTime> endTimeList = FXCollections.observableArrayList();



    @FXML private TextField appointmentField;
    @FXML private Label appointmentLabel;

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

    /**
     * This will alert the User with an error if an appointments start or end time fall outsidebusiness hours.
     * @param startTime
     * @return
     */
    private boolean hoursOfOperation(LocalTime startTime)
    {
        LocalTime openTime = LocalTime.of(8, 00);
        LocalTime closedTime = LocalTime.of(22, 00);

        LocalDate date = datePicker.getValue();

        ZoneId zoneEST = ZoneId.of("US/Eastern");

        LocalDateTime combined = LocalDateTime.of(date, startTime);
        ZonedDateTime convertedDate = combined.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneEST);
        LocalTime easternTime = convertedDate.toLocalTime();

        if(easternTime.isBefore(openTime) || easternTime.isAfter(closedTime))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Chosen timeframe is outside hours of business operation.");
            alert.setContentText("Selected Time: " + startTime + "\nEastern Time: "
                    + easternTime + "\nBusiness Hours: 08:00 to 22:00 US/Eastern");
            alert.showAndWait();
            return false;
        }
        return true;

    }

    /**
     * This method will display an error message if an appointment being added overlaps an existing appointment.
     * @param startA
     * @param endB
     * @return
     * @throws SQLException
     */
    private boolean appointmentOverlap(Timestamp startA, Timestamp endB) throws SQLException
    {

        try
        {
            if(appointmentField.getText().isBlank())
            {
                appointmentId2 = 0;
            }
            else
            {
                appointmentId2 = Integer.parseInt(appointmentField.getText().trim());
            }

            int customerID1 = (int) customerComboBox.getValue();
            int contactID1 = getContactId((String) contactComboBox.getValue());

            System.out.println("\nSelected Appointment ID: " + appointmentId2);
            System.out.println("Selected Contact ID: " + contactID1);
            System.out.println("Selected Customer ID: " + customerID1);
            System.out.println("Selected Start: " + startA);
            System.out.println("Selected End: " + endB);

            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM appointments "
                            + "WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR ? < Start AND ? > End) "
                            + "AND (Customer_ID = ? AND Appointment_ID != ?)");

            ps.setTimestamp(1, startA);
            ps.setTimestamp(2, endB);
            ps.setTimestamp(3, startA);
            ps.setTimestamp(4, endB);
            ps.setInt(5, customerID1);
            ps.setInt(6, appointmentId2);

            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
                return true;
            }
            return false;
        }
        catch (SQLException ex) { System.out.println("SQL Error!"); }
        return false;

    }

    private int getContactId(String temp)
    {
        for(Contacts look : contactList)
        {
            if(look.getContactName().trim().toLowerCase().contains(temp.trim().toLowerCase()))
            {
                return look.getContactID();
            }
        }
        return -1;
    }

    @FXML void onActionSaveButton(ActionEvent event) throws SQLException, IOException
    {
        try
        {
            String title = "";
            String description = "";
            String location = "";
            String type = "";
            String lastUpdatedBy = ("test");
            Integer userId = 1;
            Integer customerId = 1;
            Integer contactId = 1;
            String contact = "";

            LocalTime startBox = (LocalTime) startTimeComboBox.getValue();
            LocalTime endBox = (LocalTime) endTimeComboBox.getValue();


            StringBuilder error = new StringBuilder("Error: ");

            if (titleField.getText().isBlank()) {
                error.append("\n - Title field is empty.");
            } else {
                title = titleField.getText();
            }

            if (descriptionField.getText().isBlank()) {
                error.append("\n - Description field is empty.");
            } else {
                title = descriptionField.getText();
            }

            if (locationField.getText().isBlank()) {
                error.append("\n - Location field is empty.");
            } else {
                location = locationField.getText();
            }

            if (typeField.getText().isBlank()) {
                error.append("\n - Name field is empty.");
            } else {
                type = typeField.getText();
            }

            if(customerComboBox.getValue() == null) {
                error.append("\n - Please selected a Customer");
            } else {
                customerId = (int) customerComboBox.getValue();
            }

            if(userComboBox.getValue() == null) {
                error.append("\n - Please selected a User");
            } else {
                userId = (int) userComboBox.getValue();
            }

            if(contactComboBox.getValue() == null) {
                error.append("\n - Please selected a Contact");
            } else {
                contact = (String) contactComboBox.getValue();
            }

            if(contact != null){
                contactId = getContactId(contact);
            }

            if(startBox == null) {
                error.append("\n - Please selected a Start Time");
            }

            if(endBox == null) {
                error.append("\n - Please selected an End Time");
            }

            if(startBox != null && endBox != null)
            {
                LocalDate date = datePicker.getValue();
                LocalDateTime apptStart = LocalDateTime.of(date, startBox);
                LocalDateTime apptEnd = LocalDateTime.of(date, endBox);
                Timestamp start = Timestamp.valueOf(apptStart);
                Timestamp end = Timestamp.valueOf(apptEnd);
            }

            if(datePicker == null)
            {
                error.append("\n - Please selected an Date");
            }

            if(appointmentOverlap(start, end))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Appointment Time Conflict");
                alert.setContentText("The currently selected timeslot conflicts with a pre-existing appointment.");
                alert.showAndWait();
                return;
            }

            if(endBox.isBefore(startBox))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("ERROR");
                alert.setContentText("End Time Cannot Be Before Start Time");
                alert.showAndWait();
                return;
            }

            if(!hoursOfOperation(startBox)) { return; }

            if(!hoursOfOperation(endBox)) { return; }

            PreparedStatement ps = JDBC.connection.prepareStatement("INSERT INTO appointments"
                            + "(Title, Description, Location, Type, Start, "
                            + "End, Create_Date, Created_By, Last_Update, Last_Updated_By, "
                            + "Customer_ID, User_ID, Contact_ID) "
                            + "VALUES(?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setTimestamp(5, start);
            ps.setTimestamp(6, end);
            ps.setString(7, lastUpdatedBy);
            ps.setString(8, lastUpdatedBy);
            ps.setInt(9, customerId);
            ps.setInt(10, userId);
            ps.setInt(11, contactId);

            int result = ps.executeUpdate();
            returnToMain();
        }
        catch (SQLException ex)
        {
           System.out.println("SQL Error!");
        }
    }


}