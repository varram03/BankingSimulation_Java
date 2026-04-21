import java.sql.*;

public class Authentication {
    public boolean isValidPwd(String password) {

        if (password.length() < 8) {
            return false;
        }
        boolean u = false;
        boolean l = false;
        boolean d = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                u = true;
            } else if (Character.isLowerCase(c)) {
                l = true;
            } else if (Character.isDigit(c)) {
                d = true;
            }
        }
        return u && l && d;
    }

    public void registerUser(String userId, String password) throws Exception {
        if (!isValidPwd(password)) {
            System.out.println("ERROR: Weak Password");
            System.out.println("Must contain:");
            System.out.println("- At least 8 characters");
            System.out.println("- Uppercase, lowercase, and digit\n");
            return;
        }
        try (Connection conn = Database.connect()) {
            String query = "INSERT INTO users (userId, password) VALUES (?, ?)";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, userId);
            st.setString(2, password);
            st.executeUpdate();
            System.out.println("User registered successfully!");
        } catch (Exception e) {
            System.out.println("Error!");
            e.printStackTrace();
        }
    }

    public int loginUser(String userId, String password) throws Exception {
        try (Connection con = Database.connect()) {
            if (con == null) {
                System.out.println("Database connection failed!");
                return -2;
            }
            String query = "SELECT * FROM users WHERE userId = ?";
            PreparedStatement st = con.prepareStatement(query);
            st.setString(1, userId);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            String actualPwd = rs.getString("password");
            if (actualPwd.equals(password)) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            System.out.println("Error Occured:");
            e.printStackTrace();
            return -2;
        }
    }
}
