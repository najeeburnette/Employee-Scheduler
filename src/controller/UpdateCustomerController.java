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
import main.Countries;
import main.Customers;
import main.FirstLevelDivisions;
import main.Users;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class UpdateCustomerController implements Initializable
{
    Users currentUser;
    Customers selectedCustomer;
    ObservableList<Customers> customerList = FXCollections.observableArrayList();
    ObservableList<Countries> countryList = FXCollections.observableArrayList();
    ObservableList<FirstLevelDivisions> firstLevelList = FXCollections.observableArrayList();

    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Label titleLabel;
    @FXML private Label errorLabel;

    @FXML private TextField addressField;
    @FXML private Label addressLabel;

    @FXML private ComboBox<String> countryComboBox;
    @FXML private Label countryLabel;

    @FXML private ComboBox<String> firstDivisionComboBox;
    @FXML private Label firstDivisionLabel;

    @FXML private TextField idField;
    @FXML private Label idLabel;

    @FXML private TextField nameField;
    @FXML private Label nameLabel;

    @FXML private TextField phoneField;
    @FXML private Label phoneLabel;

    @FXML private TextField postalField;
    @FXML private Label postalLabel;

/*
Combo boxes should have set values, dont know if needed, MID PRIORITY
 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        selectedCustomer = MainMenuController.getSelectedCustomer();

        populateFirstLevelCombo();
        populateCountryCombo();

        idField.setText(String.valueOf(selectedCustomer.getCustomerID()));
        nameField.setText(selectedCustomer.getCustomerName());
        addressField.setText(selectedCustomer.getAddress());
        //countryComboBox.setVisibleRowCount(selectedCustomer);
        //firstDivisionComboBox.setButtonCell();
        postalField.setText(selectedCustomer.getPostalCode());
        phoneField.setText(selectedCustomer.getPhone());

    }

    /**
     * Populates the State/Region combo box with all divisions.
     * <p>
     * Data is pulled from the database using a prepared statement and a SELECT
     * query.The list changes depending on country is selected from the country combo box.
     */
    @FXML
    private void populateFirstLevelCombo()
    {
        ObservableList<String> firstLevelCombo = FXCollections.observableArrayList();
        try
        {
            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM first_level_divisions");

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                int divisionID = rs.getInt("Division_ID");
                String division = rs.getString("Division");
                LocalDateTime createDate = rs.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int countryID = rs.getInt("COUNTRY_ID");

                firstLevelCombo.add(division);

                firstLevelList.add(new FirstLevelDivisions(divisionID, division,
                        createDate, createdBy, lastUpdate, lastUpdatedBy, countryID));
            }
            firstDivisionComboBox.setItems(firstLevelCombo);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("SQL Error!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error!");
        }
    }

    /**
     * Populates the Country combo box.
     * <p>
     * Data is pulled from the database using a prepared statement and a SELECT
     * query.
     */
    @FXML
    private void populateCountryCombo()
    {
        ObservableList<String> countryCombo = FXCollections.observableArrayList();
        try
        {
            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM countries");

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                int countryID = rs.getInt("Country_ID");
                String country = rs.getString("Country");
                LocalDateTime createDate = rs.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");

                countryCombo.add(country);

                countryList.add(new Countries(countryID, country, createDate,
                        createdBy, lastUpdate, lastUpdatedBy));
            }
            countryComboBox.setItems(countryCombo);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("SQL Error!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error!");
        }
    }

    /**
     * Filters the State/Region combo box based on the selection of the country combo box.
     * <p>
     * Data is pulled using a prepared statement and a SELECT query.
     * The ID of the selected country is compared with the first level divisions table
     * and the divisions that match the same ID are used to
     * populate the State/Region combo box.
     *
     * @param event when the user selects a country from the combo box
     */
    @FXML
    void onActionCountryBox(ActionEvent event)
    {
        try
        {
            ObservableList<String> stateCombo = FXCollections.observableArrayList();
            PreparedStatement ps = null;

            if(countryComboBox.getValue() == null)
            {
                populateFirstLevelCombo();
            }
            if(countryComboBox.getValue().contentEquals("U.S"))
            {
                ps = JDBC.connection.prepareStatement(
                        "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = 1 "
                                + "ORDER BY Division");
            }
            if(countryComboBox.getValue().contentEquals("UK"))
            {
                ps = JDBC.connection.prepareStatement(
                        "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = 2 "
                                + "ORDER BY Division");
            }
            if(countryComboBox.getValue().contentEquals("Canada"))
            {
                ps = JDBC.connection.prepareStatement(
                        "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = 3 "
                                + "ORDER BY Division");
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                int divisionID = rs.getInt("Division_ID");
                String division = rs.getString("Division");
                LocalDateTime createDate = rs.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int countryID = rs.getInt("COUNTRY_ID");

                stateCombo.add(division);

                firstLevelList.add(new FirstLevelDivisions(divisionID, division,
                        createDate, createdBy, lastUpdate, lastUpdatedBy, countryID));
            }
            firstDivisionComboBox.setItems(stateCombo);


            firstDivisionComboBox.setButtonCell(new ListCell<String>()
            {
                @Override
                protected void updateItem(String item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if(empty || item == null)
                    {
                        setText("Select State/Region");
                    }
                    else
                    {
                        setText(item);
                    }
                }
            });
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("SQL error!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Non-SQL error!");
        }
    }

    /**
     * Finds the division ID matching the selected State/Region.
     *
     * @param i division selected from combo box
     * @return division ID if found or -1 no match found
     */
    private int getMatchingDivisionID(String i)
    {
        for(FirstLevelDivisions look : firstLevelList)
        {
            if(look.getDivision().trim().toLowerCase().contains(
                    i.trim().toLowerCase()))
            {
                return look.getDivisionID();
            }
        }
        return -1;
    }

    /**
     * This method returns the user back to the main controller without adding a Customer
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
     * This method returns the user back to the Main Menu after a Customer is added.
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

    @FXML
    void onActionSaveButton(ActionEvent event) throws SQLException
    {
        try {
            int customerId = Integer.parseInt(idField.getText());
            String name = "";
            String address = "";
            String postal = "";
            String phone = "";
            int divisionID = -1;
            String lastUpdatedBy = currentUser.getuserName();
            String lastUpdate = "";

            //error message for invalid inputs
            StringBuilder error = new StringBuilder("Error: ");

            //checking customer name input
            if (nameField.getText().isBlank()) {
                error.append("\n - Name field is empty.");
            } else {
                name = nameField.getText();
            }

            //checking address input
            if (addressField.getText().isBlank()) {
                error.append("\n - Address field is empty.");
            } else {
                address = addressField.getText();
            }

            //checking postal code input
            if (postalField.getText().isBlank()) {
                error.append("\n - Postal code field is empty.");
            } else {
                postal = postalField.getText();
            }

            //checking phone input
            if (phoneField.getText().isBlank()) {
                error.append("\n - Phone field is empty.");
            } else {
                phone = phoneField.getText();
            }
            if (countryComboBox.getValue() == null) {
                error.append("\n - Country not selected.");
            } else {
            }

            if (firstDivisionComboBox.getValue() == null) {
                error.append("\n - State/region not selected.");
            } else {
                divisionID = getMatchingDivisionID(firstDivisionComboBox.getValue());
            }

            //Show error messages if there are
            if (error.toString().compareTo("Error: ") != 0) {
                errorLabel.setText(error.toString());
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);

                PreparedStatement ps =  JDBC.connection.prepareStatement("UPDATE CUSTOMERS "
                        + "Set Customer_Name = ?, Address = ?, Postal_Code = ?, "
                        + "Phone = ?, Last_Update = NOW(), Last_Updated_By = ?, Division_ID = ? "
                        + "WHERE Customer_ID = ?");

                ps.setString(1, name);
                ps.setString(2, address);
                ps.setString(3, postal);
                ps.setString(4, phone);
                ps.setString(5, lastUpdatedBy);
                ps.setInt(6, divisionID);
                ps.setInt(7, customerId);

                int result = ps.executeUpdate();
                returnToMain();
            }
        }
        catch (SQLException e)
        {
            System.out.println("SQL Error!");
        }
        catch(Exception e)
        {
            System.out.println("Error!");
        }

    }

}