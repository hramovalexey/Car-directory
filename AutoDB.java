import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class AutoDB {
    public static void main(String[] args) {
        String str
                = "{ \"number\" : \"AA789H96\", \"model\" : \"'Datsun'\", \"color\" : \"Белый\", \"year\" : 2016}";
        try {
            Object obj = new JSONParser().parse(str);
            JSONObject json = (JSONObject) obj;
            String number = (String) json.get("number");
            System.out.println(number);
            Long year = (Long) json.get("year");
            System.out.println(year);
        }
        catch (ParseException e) {
            System.out.println(e.toString());
        }

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/AutoDB", "postgres", "1");
            System.out.println("ну допустим, connected");
            // conn.createStatement().execute("insert into auto_list (number, model, color, year) values(\"AA789H96\", \"Datsun\", \"Белый\", 2016);");
            // conn.createStatement().execute("select * from auto_list;");
            boolean result;
            /*boolean result = conn.createStatement().execute(
                    "insert into auto_list "
                            + "values('AA4427H96', 'Datsun', 'Белый', 2016);");

            System.out.println(result);*/

            Statement statement = conn.createStatement();
            statement.execute("select * from auto_list;");
            ResultSet rs = statement.getResultSet();


            System.out.println(rs);
            conn.close();


        }
        catch (ClassNotFoundException | java.sql.SQLException e) {
            System.out.println(e.toString());
        }
    }
}
