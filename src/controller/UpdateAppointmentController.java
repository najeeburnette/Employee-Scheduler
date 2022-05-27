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
import main.Appointments;
import main.Contacts;
import main.Customers;
import main.Users;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.ResourceBundle;

public class UpdateAppointmentController implements Initializable {

    Appointments selectedAppointment;
    int secondAppointment = 0;
    Timestamp start = null;
    Timestamp end = null;

    private ObservableList<Contacts> contactList = FXCollections.observableArrayList();
    private ObservableList<Users> userList = FXCollections.observableArrayList();
    private ObservableList<Customers> customersList = FXCollections.observableArrayList();
    private ObservableList<LocalTime> startTimeList = FXCollections.observableArrayList();
    private ObservableList<LocalTime> endTimeList = FXCollections.observableArrayList();


    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;

    @FXML private ComboBox contactComboBox;
    @FXML private Label contactLabel;

    @FXML private ComboBox customerComboBox;
    @FXML private Label customerLabel;

    @FXML private DatePicker datePicker;

    @FXML private TextField appointmentField;
    @FXML private Label appointmentLabel;

    @FXML private TextField descriptionField;
    @FXML private Label descriptionLabel;

    @FXML private TextField endField;
    @FXML private Label endLabel;

    @FXML private TextField locationField;
    @FXML private Label locationLabel;

    @FXML private TextField startField;
    @FXML private Label startLabel;

    @FXML private TextField titleField;
    @FXML private Label titleLabel;

    @FXML private TextField typeField;
    @FXML private Label typeLabel;

    @FXML private ComboBox userComboBox;
    @FXML private Label userLabel;

    @FXML private ComboBox endTimeComboBox;
    @FXML private ComboBox startTimeComboBox;


    /**
     *  The controller class for Update Appointment screen.
     *
     *  @author Najee Burnette
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        selectedAppointment = MainMenuController.getSelectedAppointment();

        try {
            populateContactCombobox();
            populateUserComboBox();
            populateCustomerComboBox();
            populateTimeComboBoxes();
        }
        catch (SQLException throwables) { throwables.printStackTrace(); }

        appointmentField.setText(String.valueOf(selectedAppointment.getCustomerID()));
        customerComboBox.setValue(selectedAppointment.getCustomerID());
        userComboBox.setValue(selectedAppointment.getUserID());
        titleField.setText(selectedAppointment.getTitle());
        descriptionField.setText(selectedAppointment.getDescription());
        locationField.setText(selectedAppointment.getLocation());
        contactComboBox.setValue(selectedAppointment.getContactName());
        typeField.setText(selectedAppointment.getType());
        startTimeComboBox.setValue(selectedAppointment.getStart());
        endTimeComboBox.setValue(selectedAppointment.getEnd());


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
     * This will alert the User with an error if an appointments start or end time fall outside business hours.
     * @param chosenTime
     * @return
     */
    private boolean hoursOfOperation(LocalTime chosenTime)
    {

        LocalTime openTime = LocalTime.of(8, 00);
        LocalTime closedTime = LocalTime.of(22, 00);

        LocalDate date = datePicker.getValue();

        ZoneId zoneEST = ZoneId.of("US/Eastern");

        LocalDateTime combined = LocalDateTime.of(date, chosenTime);
        ZonedDateTime convertedDate = combined.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneEST);
        LocalTime easternTime = convertedDate.toLocalTime();

        if(easternTime.isBefore(openTime) || easternTime.isAfter(closedTime)) { return false; }

        return true;
    }

    /**
     * This method will display an error message if an appointment being added overlaps an existing appointment.
     * @param startTime
     * @param endTime
     * @return
     * @throws SQLException
     */
    private boolean appointmentOverlap(Timestamp startTime, Timestamp endTime) throws SQLException
    {
        try
        {
            if(appointmentField.getText().isBlank()) { secondAppointment = 0; }
            else { secondAppointment = Integer.parseInt(appointmentField.getText().trim()); }

            int customerID1 = (int) customerComboBox.getValue();
            int contactID1 = getContactId((String) contactComboBox.getValue());

            System.out.println("\nSelected Appointment ID: " + secondAppointment);
            System.out.println("Selected Contact ID: " + contactID1);
            System.out.println("Selected Customer ID: " + customerID1);
            System.out.println("Selected Start: " + startTime);
            System.out.println("Selected End: " + endTime);

            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM appointments "
                            + "WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR ? < Start AND ? > End) "
                            + "AND (Customer_ID = ? AND Appointment_ID != ?)");

            ps.setTimestamp(1, startTime);
            ps.setTimestamp(2, endTime);
            ps.setTimestamp(3, startTime);
            ps.setTimestamp(4, endTime);
            ps.setInt(5, customerID1);
            ps.setInt(6, secondAppointment);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) { return true; }
            return false;
        }
        catch (SQLException ex) { System.out.println("SQL Error!"); }
        return false;
    }

    /**
     * Gets the contact ID of the contact that matches the string passed through
     * @param temp
     * @return
     */
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
     * Saves the appointment being added to the database
     *<p>
     * Data is pulled from the fields and combo boxes passed through multiple integrity checks.
     * Data is then inserted into the database using a prepared statement.
     *</p>
     *
     * @param event when the save button is clicked
     * @throws SQLException
     * @throws IOException
     */
    @FXML void onActionSaveButton(ActionEvent event) throws SQLException, IOException {
        try {
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
                description = descriptionField.getText();
            }

            if (locationField.getText().isBlank()) {
                error.append("\n - Location field is empty.");
            } else {
                location = locationField.getText();
            }

            if (typeField.getText().isBlank()) {
                error.append("\n - Type field is empty.");
            } else {
                type = typeField.getText();
            }

            if (customerComboBox.getValue() == null) {
                error.append("\n - Please selected a Customer");
            } else {
                customerId = (int) customerComboBox.getValue();
            }

            if (userComboBox.getValue() == null) {
                error.append("\n - Please selected a User");
            } else {
                userId = (int) userComboBox.getValue();
            }

            if (contactComboBox.getValue() == null) {
                error.append("\n - Please selected a Contact");
            } else {
                contact = (String) contactComboBox.getValue();
            }

            if (contact != null) {
                contactId = getContactId(contact);
            }

            if (startBox == null) {
                error.append("\n - Please selected a Start Time");
            }

            if (endBox == null) {
                error.append("\n - Please selected an End Time");
            }

            if (startBox != null && endBox != null) {
                LocalDate date = datePicker.getValue();
                LocalDateTime apptStart = LocalDateTime.of(date, startBox);
                LocalDateTime apptEnd = LocalDateTime.of(date, endBox);
                start = Timestamp.valueOf(apptStart);
                end = Timestamp.valueOf(apptEnd);
            }

            if (datePicker == null) {
                error.append("\n - Please selected an Date");
            }

            if (startBox != null && endBox != null && contactComboBox.getValue()!= null && customerComboBox.getValue() != null) {

                if (appointmentOverlap(start, end)) {
                    error.append("\n - Appointment time overlaps another\n   customer appointment");
                }
                if (endBox.isBefore(startBox)) {
                    error.append("\n - Appointment end time is before start time");
                }
                if (!hoursOfOperation(startBox) || !hoursOfOperation(endBox)) {
                    error.append("\n - Appointment is outside business hours\n   (8:00am-10:00pm EST)");
                }
            }
            //Show error messages if there are
            if (error.toString().compareTo("Error: ") != 0) {
                errorLabel.setText(error.toString());
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                PreparedStatement ps = JDBC.connection.prepareStatement("UPDATE appointments "
                        + "SET Title = ?, Description = ?, Location = ?, "
                        + "Type = ?, Start = ?, End = ?, Last_Update = NOW(), "
                        + "Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? "
                        + "WHERE Appointment_ID = ?");

                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, location);
                ps.setString(4, type);
                ps.setTimestamp(5, start);
                ps.setTimestamp(6, end);
                ps.setString(7, lastUpdatedBy);
                ps.setInt(8, customerId);
                ps.setInt(9, userId);
                ps.setInt(10, contactId);
                ps.setInt(11, Integer.parseInt(appointmentField.getText()));


                int result = ps.executeUpdate();
                returnToMain();
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error!");
        }
    }
}
