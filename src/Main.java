import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class Main {
    public static void main(String args[]) throws Exception
    {
        int sum=0,cnt=0;
        float avg;
        ResultSet rs;
        try
        {
            Database db=new Database("test","test");
            //db.undoin();
            //db.undoconsumptiom();
            //db.undoout();



            //implementation of AddtoInventory

            db.AddtoInventory(200,80,250,200,80,50);

            //implementation of remove from inventory
            db.removeFromInventory(100,100,100);

            //implementation of consumption calculated wastage and save into table

            db.consumption(100);

            //report generation layout

            //report generated for cost only

            rs=db.procuringCost("01-JAN-18","30-DEC-18");
            System.out.println("DATE\t\t\tRICE\tPRICE\tCHICKEN\tPRICE\t" +
                    "BEEF\tPRICE\tTOTAL");
            while(rs.next())
            {
                System.out.print(rs.getDate(1)+"\t\t");
                System.out.print(rs.getInt(2)+"\t\t");
                System.out.print(rs.getInt(3)+"\t\t");
                System.out.print(rs.getInt(4)+"\t\t");
                System.out.print(rs.getInt(5)+"\t\t");
                System.out.print(rs.getInt(6)+"\t\t");
                System.out.print(rs.getInt(7)+"\t\t");
                System.out.println(rs.getInt(8)+"\t\t");
                sum=sum+rs.getInt(8);
                cnt++;

            }
            System.out.println("\n");
            avg=(float)(sum/cnt);
            System.out.println("TOTAL COST="+sum+"\n"+"AVERAGE COST="+avg);
            System.out.println("\n");

            //cost only report end

            //consumtion report only start

            rs=db.consumptionReport("01-JAN-18","30-DEC-18");
            System.out.println("TOTACONSUMPTION");
            while(rs.next())
            {
                System.out.print(rs.getDate(1)+"\t\t");
                System.out.println(rs.getInt(2));

            }
            //consumtion report only end

            //wastage only report start
            System.out.println("\n");
            rs=db.wastage("01-JAN-18","30-DEC-18");
            System.out.println("WASTAGE");
            while(rs.next())
            {
                System.out.print(rs.getDate(1)+"\t\t");
                System.out.println(rs.getInt(2));

            }

            //wastage report end
            System.out.println("\n");
            System.out.println("ALL REPORT");
            rs=db.ReportAll("01-JAN-18","30-DEC-18");
            while(rs.next())
            {
                System.out.print(rs.getDate(1)+"\t\t");
                System.out.print(rs.getInt(2)+"\t\t");
                System.out.print(rs.getInt(3)+"\t\t");
                System.out.println(rs.getInt(4));


            }


            rs=db.choices(true,false);
            boolean sumb=true;
            ResultSetMetaData rsmd=rs.getMetaData();
            int colnum=rsmd.getColumnCount();
            //System.out.println(col);
            if(colnum==3&&sumb==true)
            {
                System.out.println("TOTAL:");
                while(rs.next())
                {
                    System.out.print(rs.getInt(1)+"\t\t");
                    System.out.print(rs.getInt(2)+"\t");
                    System.out.print(rs.getInt(3)+"\t\t");
                }
            }
            else if(colnum==3&&sumb==false)
            {
                System.out.println("AVERAGE:");
                while(rs.next())
                {
                    System.out.print(rs.getInt(1)+"\t\t");
                    System.out.print(rs.getInt(2)+"\t");
                    System.out.print(rs.getInt(3)+"\t\t");
                }
            }
            else if(colnum==6)
            {
                System.out.println("ALL:");
                while(rs.next())
                {
                    System.out.println(rs.getInt(1)+"\t\t"+rs.getInt(3)+"\t\t"+rs.getInt(5));
                    System.out.println(rs.getInt(2)+"\t\t"+rs.getInt(4)+"\t\t"+rs.getInt(6));

                }
            }


            db.undoin();
            db.undoout();
            //db.consumption(82);
            db.undoconsumptiom();


        }
        catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

}
