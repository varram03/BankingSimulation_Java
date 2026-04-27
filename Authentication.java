import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import javax.swing.*;

import javax.swing.JOptionPane;

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
    private String hashPassword(String password) throws Exception{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hb = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for(byte b:hb){
            sb.append(String.format("%02x",b));
        }
        return sb.toString();
    }

    public boolean registerUser(String userId, String password) throws Exception {
        if (!isValidPwd(password)) {
            JOptionPane.showMessageDialog(null,
                "\nUse a Strong Password\nPassword cannot be blank and must contain:\n"+
                "- At least 8 characters\n" +
                "- Uppercase, lowercase, and digit\n"
            );
            return false;
        }
        try (Connection conn = Database.connect()) {
            String query = "INSERT INTO users (userId, password) VALUES (?, ?)";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, userId);
            st.setString(2, hashPassword(password));
            st.executeUpdate();
            return true;
        }catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null,"User already registered. Please login.");
            return false;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
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
            if (actualPwd.equals(hashPassword(password))) {
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
