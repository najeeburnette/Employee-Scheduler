package main;

/**
 * The Users Class
 *
 * @author Najee Burnette
 */

public class Users
{
    private static int userID;
    private static String userName;
    private static String password;

    /**
     * Users Constructor
     *
     * @param userID the user ID
     * @param userName the user name
     * @param password the password
     *
     */

    public Users(int userID, String userName, String password)
    {

        this.userID = userID;
        this.userName = userName;
        this.password = password;
    }



    /**
     * Setter for userID
     *
     * @param userID the user ID to set
     */
    public static void setuserID(int userID) { Users.userID = userID; }

    /**
     * Getter for userID
     *
     * @return the user Id to set
     */
    public static int getuserID() {return userID; }

    /**
     * Setter for userName
     *
     * @param userName the username to set
     */
    public static void setuserName(String userName) { Users.userName = userName; }

    /**
     * Getter for userName
     *
     * @return the username
     */
    public static String getuserName() {return userName;}

    /**
     * Setter for password
     *
     * @param password the password to set
     */
    public static void setPassword(String password) { Users.password = password; }

    /**
     * Getter for password
     *
     * @return the password
     */
    public static String getPassword() { return password; }




}
