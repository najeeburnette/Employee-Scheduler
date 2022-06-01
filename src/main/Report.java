package main;

/**
 *The Reports Class
 *
 * @author Najee Burnette
 */
public class Report {

    private String month;
    private String type;
    private int total;

    /**
     Report Constructor
     @param month for month tableview
     @param type for type tableview
     @param total for total tableview
     */
    public Report(String month, String type, int total)
    {
        this.month = month;
        this.type = type;
        this.total = total;
    }

    /**
     Setter for month
     @param month for the month view
     */
    public void setMonth(String month) { this.month = month; }

    /**
     Getter for month
     @return month
     */
    public String getMonth() { return month; }

    /**
     Setter for type
     @param type for the Type of appointment
     */
    public void setType(String type) { this.type = type; }

    /**
     Getter for type
     @return type
     */
    public String getType() { return type; }

    /**
     Setter for total
     @param total appointments
     */
    public void setTotal(int total) { this.total = total; }

    /**
     Getter for total
     @return total
     */
    public int getTotal() { return total; }
}
