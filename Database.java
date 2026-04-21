import java.sql.*;

public class Database {
    private static final String url = "jdbc:mysql://localhost:3306/banking?autoReconnect=true&useSSL=false";
    private static final String user = "root";
    private static final String password = "Varu@8624"; 
    private static Connection conn = null;
    public static Connection connect(){
        try{
            if(conn == null || conn.isClosed()){
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, password);
            }
        }catch(Exception e){
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
        return conn;
    }
}
