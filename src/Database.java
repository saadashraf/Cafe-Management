import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;

public class Database {
    public Statement statement;
    public String sql_query;
    public boolean error;
    public boolean connected;
    public ResultSet rs;
    public String username;
    public String password;
    private Connection connection;
    private String db_url;
    private int rt;
    private float avgCon = .2f;

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
            total = rs.getInt(1) + rs.getInt(2) + rs.getInt(3);
            sql_query = "UPDATE INVENTORY SET RICE=RICE-" + Integer.toString(rs.getInt(1)) + "," +
                    "BEEF=BEEF-" + Integer.toString(rs.getInt(2)) + "," + "CHICKEN=CHICKEN-" + Integer.toString(rs.getInt(3)) + "WHERE ID=1" + ")";
            //statement.executeQuery(sql_query);
            sql_query = "INSERT INTO ITEM_REMOVED VALUES(TO_DATE('" + todayDate + "','dd/mm/yyyy'," + Integer.toString(rs.getInt(1)) + "," +
                    Integer.toString(rs.getInt(2)) + "," + Integer.toString(rs.getInt(3)) + Integer.toString(total) + ")";
            //statement.executeQuery(sql_query);
            sql_query = "COMMIT";
            statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("ITEM REMOVAL ERROR");
            ex.printStackTrace();
        }

    }


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
            sql_query = "COMMIT";
            statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("CUSTOM ITEM REMOVAL ERROR");
            ex.printStackTrace();
        }
    }


    public void consumption(int noOfStudent) {
        int Tconsumption = (int) (noOfStudent * avgCon), wastage;
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
            sql_query = "INSERT INTO CONSUMPTION VALUES(TO_DATE('" + todayDate + "','mm/dd/yyyy')," + Integer.toString(noOfStudent) + "," +
                    Integer.toString(Tconsumption) + "," + Integer.toString(wastage) + ")";
            //System.out.println(sql_query);
            statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("CONSUMTION INPUT ERROR");
            ex.printStackTrace();
        }
    }

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

    /*public ResultSet p_costSum(String start, String end,boolean sum) {
        sql_query="CREATE OR REPLACE VIEW SUMAVG AS "

        sql_query = "SELECT SUM(TOTAL),AVG(TOTAL) FROM P_COST WHERE DATE>='" + start + "'" + "AND DATE<='" + end + "'";
        try {
            rs = statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("SUM/AVG PRINTING ERROR");
            ex.printStackTrace();
        }
        return rs;
    }*/

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

    /* public ResultSet exp_costSum(String start, String end) {
         sql_query = "SELECT SUM(TOTAL),AVG(TOTAL) FROM EXP_COST WHERE DATE>='" + start + "'" + "AND DATE<='" + end + "'";
         try {
             rs = statement.executeQuery(sql_query);
         } catch (SQLException ex) {
             System.err.println("EXP SUM/AVG PRINTING ERROR");
             ex.printStackTrace();
         }
         return rs;
     }*/


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


    public ResultSet ReportAll(String start, String end) {
        sql_query = "CREATE OR REPLACE VIEW REPORTALL AS SELECT * FROM CONSUMPTION WHERE C_DATE>='" + start + "'" + "AND C_DATE<='" + end + "'";
        //System.out.println(sql_query);
        try {
            statement.executeQuery(sql_query);
            sql_query = "SELECT * FROM REPORTALL";
            rs = statement.executeQuery(sql_query);
            sql_query = "COMMIT";
            statement.executeQuery(sql_query);
        } catch (SQLException ex) {
            System.err.println("FULL REPORT GENERATION ERROR");
            ex.printStackTrace();
        }
        return rs;
    }

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
