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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.Appointments;
import main.Contacts;
import main.Report;
import main.Users;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * FXML controller class for Reports page.
 *
 * @author Najee Burnette
 */
public class ReportsController implements Initializable {

    ObservableList<Report> reportList = FXCollections.observableArrayList();
    ObservableList<Contacts> contactListReport = FXCollections.observableArrayList();
    ObservableList<Users> userListReport = FXCollections.observableArrayList();
    ObservableList<Appointments> filteredAppointmentList = FXCollections.observableArrayList();


    @FXML private TableView<Report> reportTable;
    @FXML private TableColumn<Report, String> monthColumn;
    @FXML private TableColumn<Report, String> typeColumn;
    @FXML private TableColumn<Report, Integer> totalColumn;

    @FXML private ComboBox<String> contactComboBox;

    @FXML private TableView<Appointments> contactTable;
    @FXML private TableColumn<Appointments, Integer> appointmentColumn;
    @FXML private TableColumn<Appointments, String> titleColumn;
    @FXML private TableColumn<Appointments, String> typeColumn2;
    @FXML private TableColumn<Appointments, String> descriptionColumn;
    @FXML private TableColumn<Appointments, LocalDateTime> startColumn;
    @FXML private TableColumn<Appointments, LocalDateTime> endColumn;
    @FXML private TableColumn<Appointments, String> customerColumn;

    @FXML private ComboBox<String> userComboBox;

    @FXML private TableView<Appointments> userTable;
    @FXML private TableColumn<Appointments, Integer> appointmentColumn2;
    @FXML private TableColumn<Appointments, String> titleColumn2;
    @FXML private TableColumn<Appointments, String> typeColumn3;
    @FXML private TableColumn<Appointments, String> descriptionColumn2;
    @FXML private TableColumn<Appointments, LocalDateTime> startColumn2;
    @FXML private TableColumn<Appointments, LocalDateTime> endColumn2;
    @FXML private TableColumn<Appointments, String> customerColumn2;


    @Override public void initialize(URL url, ResourceBundle rb) {
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));


        appointmentColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn2.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        appointmentColumn2.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn2.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn2.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn3.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn2.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn2.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerColumn2.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        try {
            populateReportTable();
            populateContactTable();
            populateUserTable();
            populateContactCombobox();
            populateUserCombobox();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * This returns the user back to the Main Menu
     *
     * @param actionEvent applied to Main Menu Button
     * @throws IOException
     */
    public void onActionMainMenu (javafx.event.ActionEvent actionEvent) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Main Menu.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Scheduler");
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Populates the month/type appointments reports table.
     * <p>
     * Data is pulled from database to populate the table using a prepared statement
     * Data is put inside an observable list to display in the table view.
     *</p>
     *
     *@throws SQLException throws when sql operation is failed or error interpreted
     */
    private void populateReportTable() throws SQLException
    {
        try
        {
            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT MONTHNAME(Start) AS Month, Type, "
                            + "COUNT(Type) AS TOTAL FROM APPOINTMENTS "
                            + "GROUP BY Month, Type");

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                String month = rs.getString("Month");
                String type = rs.getString("Type");
                int total = rs.getInt("TOTAL");

                reportList.add(new Report(month, type, total));
            }
            reportTable.setItems(reportList);
        }
        catch(SQLException e) { System.out.println("SQL error!"); }
    }
    /**
     *
     * Populates the contact appointments reports table.
     * <p>
     * Data is pulled from database to populate the table using a prepared statement
     * Data is put inside an observable list to display in the table view.
     *</p>
     *
     *@throws SQLException throws when sql operation is failed or error interpreted
     */
    private void populateContactTable() throws SQLException
    {
        try
        {
            filteredAppointmentList.clear();

            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM appointments, customers, users, contacts "
                            + "WHERE appointments.User_ID = users.User_ID "
                            + "AND appointments.Contact_ID = contacts.Contact_ID "
                            + "AND appointments.Customer_ID = customers.Customer_ID "
                            + "ORDER BY Start");

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {

                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
                int customerID = rs.getInt("Customer_ID");
                String contactName = rs.getString("Contact_Name");

                LocalDateTime createdDate = rs.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int contactID = rs.getInt("Contact_ID");
                String email = rs.getString("Email");

                int userID = 1;

                filteredAppointmentList.add(new Appointments(appointmentID, title, description,
                        location, type, start, end, createdDate, createdBy, lastUpdate,
                        lastUpdatedBy, customerID, userID, contactID, contactName));
            }
            contactTable.setItems(filteredAppointmentList);
        }
        catch(SQLException e) { System.out.println("SQL error!"); }
    }

    /**
     * Populates contact combo box with data pulled from the database using a prepared statement.
     * @throws SQLException
     */
    private void populateContactCombobox() throws SQLException
    {
        ObservableList<String> contactCombo = FXCollections.observableArrayList();
        try
        {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * "
                    + "FROM contacts");

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                int contactId = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                String email = rs.getString("Email");

                contactCombo.add(rs.getString("Contact_Name"));

                contactListReport.add(new Contacts(contactId, contactName, email));
            }
            contactComboBox.setItems(contactCombo);
        }
        catch (SQLException ex) { System.out.println("SQL error!");}
    }


    /**
     * Populates user combo box with data pulled from the database using a prepared statement.
     * @throws SQLException
     */
    private void populateUserCombobox() throws SQLException
    {
        ObservableList<String> userCombo = FXCollections.observableArrayList();
        try
        {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * "
                    + "FROM users");

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                int userID = rs.getInt("User_ID");
                String userName = rs.getString("User_Name");
                String password = rs.getString("Password");

                userCombo.add(rs.getString("User_Name"));

                userListReport.add(new Users(userID, userName, password));
            }
            userComboBox.setItems(userCombo);
        }
        catch (SQLException ex) { System.out.println("SQL error!");}
    }

    /**
     * Filters appointments based on the contact selected in the contact combo box.
     * @param event contact selected from combo box.
     * @throws SQLException
     */
    @FXML void onActionContactCombo(ActionEvent event) throws SQLException{
        try
        {
            filteredAppointmentList.clear();

            String selectedContact = contactComboBox.getValue();

            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM appointments, customers, users, contacts "
                            + "WHERE appointments.User_ID = users.User_ID "
                            + "AND appointments.Contact_ID = contacts.Contact_ID "
                            + "AND appointments.Customer_ID = customers.Customer_ID AND "
                            + "Contact_Name = ? ORDER BY Start");

            ps.setString(1, selectedContact);
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {

                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
                int customerID = rs.getInt("Customer_ID");

                String contactName = rs.getString("Contact_Name");
                LocalDateTime createdDate = rs.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int contactId = rs.getInt("Contact_ID");

                int userID = 1;

                filteredAppointmentList.add(new Appointments(appointmentID, title, description,
                        location, type, start, end, createdDate, createdBy, lastUpdate,
                        lastUpdatedBy, customerID, userID, contactId, contactName));
            }

            contactTable.setItems(filteredAppointmentList);
        }
        catch(SQLException e) { System.out.println("SQL error!"); }
    }


    /**
     * Filters appointments based on the user selected in the user combo box.
     * @param event contact selected from combo box.
     * @throws SQLException
     */
    @FXML void onActionUserCombo(ActionEvent event) throws SQLException{
        try
        {
            filteredAppointmentList.clear();

            String selectedUser = userComboBox.getValue();
            System.out.println("Should be a user name: " + selectedUser);

            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM appointments, customers, users, contacts "
                            + "WHERE appointments.User_ID = users.User_ID "
                            + "AND appointments.Contact_ID = contacts.Contact_ID "
                            + "AND appointments.Customer_ID = customers.Customer_ID AND "
                            + "User_Name = ? ORDER BY Start");

            ps.setString(1, selectedUser);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {

                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
                int customerID = rs.getInt("Customer_ID");

                String contactName = rs.getString("Contact_Name");
                LocalDateTime createdDate = rs.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int contactId = rs.getInt("Contact_ID");
                int userID = rs.getInt("User_ID");

                filteredAppointmentList.add(new Appointments(appointmentID, title, description,
                        location, type, start, end, createdDate, createdBy, lastUpdate,
                        lastUpdatedBy, customerID, userID, contactId, contactName));
            }
            userTable.setItems(filteredAppointmentList);
        }
        catch(SQLException e) { System.out.println("SQL error!"); }
    }

    /**
     * Populates the user appointments reports table.
     * <p>
     * Data is pulled from database to populate the table using a prepared statement
     * Data is put inside an observable list to display in the table view.
     *</p>
     *
     *@throws SQLException throws when sql operation is failed or error interpreted
     */
    private void populateUserTable() throws SQLException
    {
        try
        {
            filteredAppointmentList.clear();

            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM appointments, customers, users, contacts "
                            + "WHERE appointments.User_ID = users.User_ID "
                            + "AND appointments.Contact_ID = contacts.Contact_ID "
                            + "AND appointments.Customer_ID = customers.Customer_ID "
                            + "ORDER BY Start");

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {

                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
                int customerID = rs.getInt("Customer_ID");
                String contactName = rs.getString("Contact_Name");

                // Other data not needed for table
                LocalDateTime createdDate = rs.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int contactID = rs.getInt("Contact_ID");
                String email = rs.getString("Email");

                int userID = 1;

                filteredAppointmentList.add(new Appointments(appointmentID, title, description,
                        location, type, start, end, createdDate, createdBy, lastUpdate,
                        lastUpdatedBy, customerID, userID, contactID, contactName));
            }
            userTable.setItems(filteredAppointmentList);
        }
        catch(SQLException e) { System.out.println("SQL error!"); }
    }
}
