import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;

/**
       *@author Rizvi Ahmed
        * @version 0.0.1
        * @since 2018-10-06
*/

public class Database {
    public Statement statement; //Statement type object to create sql statement
    public String sql_query; //String of create sql statement
    public boolean error; //bool to get error status has getter method
    public boolean connected  ; // Connection type object to get connection using driver.
    public ResultSet rs;  // resultset type object to retrive database record
    public String username; // username to access to database
    public String password; // password for database
    private Connection connection; //connection type object to connect to object
    private String db_url; // database destination url
    private int rt;
    private final float avgCon = .2f; // average consumption per person use to calculate wastage


    /**
     * Constructor for Database class objects. Creates connection with database.
     * @param _username String Username for the database account
     * @param _password String Password that is allocated to the user
     */

    public Database(String _username, String _password) {
        connection = null;
        error = false;
        connected = false;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            db_url = "jdbc:oracle:thin:@localhost:1521:xe";
            username = _username;
            password = _password;
            connection = DriverManager.getConnection(db_url, username, password);

            if (connection != null) {
                error = false;
                connected = true;
                statement = connection.createStatement();
            } else connected = false;

        } catch (ClassNotFoundException | SQLException ex) {
            error = true;
            System.err.println("Dbase Connection Error");
            ex.printStackTrace();
        }
    }

    /**
     * Closes connection with database. Should always be called at the end of each session to close the database.
     */
    public void close(){
        try{
            if(connection != null && !connection.isClosed()){
                rs.close();
                connection.close();
                statement.close();
            }
        }
        catch (SQLException ex){
            error = true;
            ex.printStackTrace();
        }
    }

    /**
     *
     * @return boolean coneection status can be called anytime
     */
    public boolean getConnectionStatus()
    {
        return connected;
    }

    /**
     *
     * @return boolean error status can be called anytime
     */
    public boolean geterrorstatus()
    {
        return error;
    }

    /**
     *
     * @param _rice amount of rice
     * @param r_price price of rice
     * @param _beef amount of beef
     * @param b_price price of beed
     * @param _chicken amount of chicken
     * @param c_price price of chicken
     */
    public void AddtoInventory(int _rice, int r_price, int _beef, int b_price, int _chicken, int c_price) {
        //TODO cnt variable
        int cnt = 0;
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        String todayDate = df.format(cal.getTime());
        //System.out.println(todayDate);
        int total = (_rice * r_price) + (_beef * b_price) + (_chicken * c_price);

        try {
            sql_query = "UPDATE INVENTORY SET RICE=RICE+" + Integer.toString(_rice) + "," +
                    "BEEF=BEEF+" + Integer.toString(_beef) + "," + "CHICKEN=CHICKEN+" + Integer.toString(_chicken) + " WHERE ID=1";
            //System.out.println(sql_query);
            //rs=statement.executeQuery("SELECT * FROM INVENTORY");
            statement.executeQuery(sql_query);
            //System.out.println("hello");
            sql_query = "INSERT INTO ITEM_ADDED VALUES(TO_DATE('" + todayDate + "','mm/dd/yyyy')," + Integer.toString(_rice) + "," +
                    Integer.toString(r_price) + "," + Integer.toString(_beef) + "," + Integer.toString(b_price) + "," +
                    Integer.toString(_chicken) + "," + Integer.toString(c_price) + "," + Integer.toString(total) + ")";
            // System.out.println(sql_query);
            statement.executeQuery(sql_query);
            sql_query = "COMMIT";
            statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("ITEM ADDITION ERROR");
            ex.printStackTrace();
        }

    }


    /**
     * Method for item removal from inventory according to menu can be used for extension not implemented
     */


    public void removeFromInventory() {
        int total;
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        String todayDate = df.format(cal.getTime());
        LocalDate currentDate = LocalDate.now(); // 2016-06-17
        DayOfWeek dow = currentDate.getDayOfWeek(); // FRIDAY

        try {
            sql_query = "SELECT * FROM MENU WHERE DAY='" + dow + "'";
            rs = statement.executeQuery(sql_query);
            rs.next();
            total = rs.getInt(2) + rs.getInt(3) + rs.getInt(4);
            sql_query = "UPDATE INVENTORY SET RICE=RICE-" + Integer.toString(rs.getInt(2)) + "," +
                    "BEEF=BEEF-" + Integer.toString(rs.getInt(3)) + "," +
                    "CHICKEN=CHICKEN-" + Integer.toString(rs.getInt(4)) + " WHERE ID=1";
            //System.out.println(sql_query);
            statement.executeQuery(sql_query);
            sql_query = "SELECT * FROM MENU WHERE DAY='" + dow + "'";
            rs=statement.executeQuery(sql_query);
            rs.next();
            sql_query = "INSERT INTO ITEM_REMOVED VALUES(TO_DATE('" + todayDate + "','mm/dd/yyyy')," + Integer.toString(rs.getInt(2)) + "," +
                    Integer.toString(rs.getInt(3)) + "," + Integer.toString(rs.getInt(4))+"," + Integer.toString(total) + ")";
            //System.out.println(sql_query);
            statement.executeQuery(sql_query);
            //sql_query = "COMMIT";
           // statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("ITEM REMOVAL FROM MENU ERROR");
            ex.printStackTrace();
        }

    }

    /**
     *
     * @param _rice amount of rice to be removed
     * @param _beef amount of beef to be removed
     * @param _chicken amount of chicken to be removed
     */
    public void removeFromInventory(int _rice, int _beef, int _chicken) {
        int total = _rice + _beef + _chicken;
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        String todayDate = df.format(cal.getTime());

        sql_query = "UPDATE INVENTORY SET RICE=RICE-" + Integer.toString(_rice) + "," +
                "BEEF=BEEF-" + Integer.toString(_beef) + "," + "CHICKEN=CHICKEN-" + Integer.toString(_chicken) + " WHERE ID=1";
        try {
            statement.executeQuery(sql_query);
            sql_query = "INSERT INTO ITEM_REMOVED VALUES(TO_DATE('" + todayDate + "','mm/dd/yyyy')," + Integer.toString(_rice) + "," + Integer.toString(_beef) + "," +
                    Integer.toString(_chicken) + "," + Integer.toString(total) + ")";
            statement.executeQuery(sql_query);
            statement.executeQuery("COMMIT");
        } catch (SQLException ex) {
            System.err.println("CUSTOM ITEM REMOVAL ERROR");
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @param f_taken
     * @param b_taken
     * @param c_taken
     */
    public void consumption(int f_taken,int b_taken,int c_taken) {
        int Tconsumption = (int) ((f_taken * avgCon)+(b_taken*avgCon)+(c_taken*avgCon)), wastage;
        DateFormat df = new SimpleDateFormat("dd-MMM-yy");
        DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        String todayDate = df.format(cal.getTime());
        sql_query = "SELECT * FROM ITEM_REMOVED WHERE  R_DATE='" + todayDate + "'";
        todayDate = df2.format(cal.getTime());
        //System.out.println(sql_query);
        try {
            rs = statement.executeQuery(sql_query);
            rs.next();
            wastage = rs.getInt(5) - Tconsumption;
            sql_query = "INSERT INTO CONSUMPTION VALUES(TO_DATE('" + todayDate + "','mm/dd/yyyy')," + Integer.toString(f_taken+b_taken+c_taken) + "," +
                    Integer.toString(Tconsumption) + "," + Integer.toString(wastage) + ")";
            //System.out.println(sql_query);
            statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("CONSUMTION INPUT ERROR");
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param start start date of type String format dd-MMM-yy ex 18-0ct-18
     * @param end   end date of type String format dd-MMM-yy ex 18-0ct-18
     *              report generated inclusive of both date
     * @return      resultset type object contaning desired tuples
     */

    public ResultSet procuringCost(String start, String end) {

        sql_query = "SELECT * FROM P_COST WHERE P_DATE>='" + start + "'" + "AND P_DATE<='" + end + "'";
        //System.out.println(sql_query);
        try {
            rs = statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("COST PRINTING ERROR");
            ex.printStackTrace();
        }
        return rs;
    }

    public ResultSet p_costSum(String start, String end) {

        sql_query = "SELECT SUM(RICE),AVG(RICE),SUM(BEEF),AVG(BEEF),SUM(CHICKEN),AVG(CHICKEN),SUM(TOTAL_COST),AVG(TOTAL_COST) FROM P_COST WHERE P_DATE>='"
                + start + "'" + " AND P_DATE<='" + end + "'";
        //System.out.println(sql_query);
        try {
            rs = statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("SUM/AVG PRINTING ERROR");
            ex.printStackTrace();
        }
        return rs;
    }

    /**
     *
     * @param start start date of type String format dd-MMM-yy ex 18-0ct-18
     *
     * @param end end date of type String format dd-MMM-yy ex 18-0ct-18
     * @return resultset type object containing expenditure cost
     */
    public ResultSet expCost(String start, String end) {


        sql_query = "SELECT * FROM EXP_COST WHERE R_DATE>='" + start + "'" + "AND R_DATE<='" + end + "'";
        try {
            rs = statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("EXP COST PRINTING ERROR");
            ex.printStackTrace();
        }
        return rs;
    }

     public ResultSet exp_costSum(String start, String end) {
         sql_query = "SELECT SUM(RICE),AVG(RICE),SUM(BEEF),AVG(BEEF),SUM(CHICKEN),AVG(CHICKEN),SUM(TOTAL),AVG(TOTAL) FROM EXP_COST WHERE R_DATE>='"
                 + start + "'" + " AND R_DATE<='" + end + "'";
         try {
             rs = statement.executeQuery(sql_query);
         } catch (SQLException ex) {
             System.err.println("EXP SUM/AVG PRINTING ERROR");
             ex.printStackTrace();
         }
         return rs;
     }

    /**
     *
     * @param start start date of type String format dd-MMM-yy ex 18-0ct-18
     * @param end   end date of type String format dd-MMM-yy ex 18-0ct-18
     * @return resultset type object containing consumption report only
     */
    public ResultSet consumptionReport(String start, String end) {
        sql_query = "SELECT C_DATE,TOTALCONSUMPTION FROM CONSUMPTION WHERE C_DATE>='" + start + "'" + "AND C_DATE<='" + end + "'";
        try {
            rs = statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("CONSUMPTION REPORT ERROR");
            ex.printStackTrace();
        }
        return rs;
    }

    public ResultSet consumptionsummary(String start, String end)
    {
        sql_query="SELECT SUM(TOTALCONSUMPTION),AVG(TOTALCONSUMPTION) FROM CONSUMPTION WHERE C_DATE>='" + start + "'" + "AND C_DATE<='" + end + "'";
        try {
            rs = statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("CONSUMPTION REPORT ERROR");
            ex.printStackTrace();
        }
        return  rs;
    }


    /**
     *
     * @param start start date of type String format dd-MMM-yy ex 18-0ct-18
     * @param end   end date of type String format dd-MMM-yy ex 18-0ct-18
     * @return resultset type object containing wastage report only
     */
    public ResultSet wastage(String start, String end) {
        sql_query = "SELECT C_DATE,WASTAGE FROM CONSUMPTION WHERE C_DATE>='" + start + "'" + "AND C_DATE<='" + end + "'";
        try {
            rs = statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("CONSUMPTION REPORT ERROR");
            ex.printStackTrace();
        }
        return rs;
    }

    public ResultSet wastagesummary(String start, String end)
    {
        sql_query="SELECT SUM(WASTAGE),AVG(WASTAGE) FROM CONSUMPTION WHERE C_DATE>='" + start + "'" + "AND C_DATE<='" + end + "'";
        try {
            rs = statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("CONSUMPTION REPORT ERROR");
            ex.printStackTrace();
        }
        return  rs;
    }

    /**
     *
     * @param start start date of type String format dd-MMM-yy ex 18-0ct-18
     * @param end   end date of type String format dd-MMM-yy ex 18-0ct-18
     * @return resultset type object containing overall report
     */
    public ResultSet ReportAll(String start, String end) {
        sql_query = "CREATE OR REPLACE VIEW REPORTALL AS SELECT * FROM CONSUMPTION WHERE C_DATE>='" + start + "'" + "AND C_DATE<='" + end + "'";
        //System.out.println(sql_query);
        try {
            statement.executeQuery(sql_query);
            sql_query = "COMMIT";
            statement.executeQuery(sql_query);
            sql_query = "SELECT * FROM REPORTALL";
            rs = statement.executeQuery(sql_query);

        } catch (SQLException ex) {
            System.err.println("FULL REPORT GENERATION ERROR");
            ex.printStackTrace();
        }
        return rs;
    }

    /**
     *
     * @param sum bool to check true if sum needs to be shown false otherwise
     * @param avg bool to check true if avg needs to be shown false otherwise
     * @return resultset type object returning sum and avg
     */
    public ResultSet choices(boolean sum, boolean avg) {
        //String sub_query;
        if (sum == true && avg == false) {
            sql_query = "SELECT SUM(STUDENTS),SUM(TOTALCONSUMPTION),SUM(WASTAGE) FROM REPORTALL";
        } else if (sum == false && avg == true) {
            sql_query = "SELECT AVG(STUDENTS),AVG(TOTALCONSUMPTION),AVG(WASTAGE) FROM REPORTALL";
        } else {
            sql_query = "SELECT SUM(STUDENTS),AVG(STUDENTS),SUM(TOTALCONSUMPTION),AVG(TOTALCONSUMPTION),TOTAL(WASTAGE),AVG(WASTAGE) FROM REPORTALL";
        }
        try {
            rs = statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.out.println("ALLREPEORT GENERATION ERROR");
            ex.printStackTrace();
        }
        return rs;
    }

    /**
     * method to undo the chages made by addtoinventory method
     */

    public void undoin() {
        DateFormat df = new SimpleDateFormat("dd-MMM-yy");
        Calendar cal = Calendar.getInstance();
        String todayDate = df.format(cal.getTime());
        sql_query = "SELECT * FROM P_COST WHERE P_DATE='" + todayDate + "'";
        //System.out.println(sql_query);
        try {
            //int n;
            rs = statement.executeQuery(sql_query);
            rs.next();
            //System.out.println(rs.getInt(1));
            sql_query = "UPDATE INVENTORY SET RICE=RICE-" + Integer.toString(rs.getInt(2)) + "," +
                    "BEEF=BEEF-" + Integer.toString(rs.getInt(4)) + "," + "CHICKEN=CHICKEN-" +
                    Integer.toString(rs.getInt(6)) + " WHERE ID=1";
            //System.out.println(sql_query);
            statement.executeQuery(sql_query);
            //System.out.println(sql_query);
            sql_query = "DELETE FROM ITEM_ADDED WHERE P_DATE='" + todayDate + "'";
            statement.executeQuery(sql_query);
            sql_query = "COMMIT";
            statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("INVENTORY IN UNDOING ERROR");
            ex.printStackTrace();
        }
    }

    /**
     * method to undo changes made by removefrominventory method
     */

    public void undoout() {
        DateFormat df = new SimpleDateFormat("dd-MMM-yy");
        Calendar cal = Calendar.getInstance();
        String todayDate = df.format(cal.getTime());
        sql_query = "SELECT * FROM EXP_COST WHERE R_DATE='" + todayDate + "'";
        try {
            rs = statement.executeQuery(sql_query);
            rs.next();
            sql_query = "UPDATE INVENTORY SET RICE=RICE+" + Integer.toString(rs.getInt(2)) + "," +
                    "BEEF=BEEF+" + Integer.toString(rs.getInt(3)) + "," + "CHICKEN=CHICKEN+" +
                    Integer.toString(rs.getInt(4)) + " WHERE ID=1";
            statement.executeQuery(sql_query);
            sql_query = "DELETE FROM ITEM_REMOVED WHERE R_DATE='" + todayDate + "'";
            statement.executeQuery(sql_query);
            sql_query = "COMMIT";
            statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("INVENTORY IN UNDOING ERROR");
            ex.printStackTrace();
        }
    }

    /**
     * method to undo changes made by consumption method
     */
    public void undoconsumptiom() {
        DateFormat df = new SimpleDateFormat("dd-MMM-yy");
        Calendar cal = Calendar.getInstance();
        String todayDate = df.format(cal.getTime());
        sql_query = "DELETE FROM CONSUMPTION WHERE C_DATE='" + todayDate + "'";
        try {
            statement.executeQuery(sql_query);
            sql_query = "COMMIT";
            statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("CONSUMTION ERROR");
            ex.printStackTrace();
        }

    }
}
