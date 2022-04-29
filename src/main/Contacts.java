package main;

/**
 * The Contacts Class
 *
 * @author Najee Burnette
 */
public class Contacts
{
    private int contactID;
    private String contactName;
    private String email;

    /**
     * Contacts constructor
     *
     * @param contactID the contact ID
     * @param contactName the contact name
     * @param email the email
     */
    public Contacts(int contactID, String contactName, String email)
    {
        setContactID(contactID);
        setContactName(contactName);
        setEmail(email);
    }

    /**
     * Setter for contactID
     *
     * @param contactID the contact ID to set
     */
    public void setContactID(int contactID) { this.contactID = contactID; }

    /**
     * Getter for contactID
     *
     * @return the contact ID
     */
    public int getContactID() { return contactID; }

    /**
     * Setter for contactName
     *
     * @param contactName the contact name to set
     */
    public void setContactName(String contactName) { this.contactName = contactName; }

    /**
     * Getter for contactName
     *
     * @return the contact name
     */
    public String getContactName() { return contactName; }

    /**
     * Setter for email
     *
     * @param email the email to set
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Getter for email
     *
     * @return the email
     */
    public String getEmail() { return email; }
}
