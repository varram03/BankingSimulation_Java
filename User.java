import java.sql.*;
import java.util.*;
class User{
    String userId;
    String password;
    private ArrayList<Account> accounts = new ArrayList<>();

    public User(String userId, String password){
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public List<Account>getAccounts(){
        return accounts;
    }

    public void loadFromDB() {
        accounts.clear();
        try (Connection con = Database.connect()){
            String sql = "SELECT * FROM accounts WHERE userId = ?";
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, userId);

            ResultSet rs = st.executeQuery();
            accounts.clear();
            while (rs.next()) {
                accounts.add(new Account(
                    rs.getString("accNo"),
                    rs.getString("accType"),
                    rs.getDouble("balance")
                ));
            }

            System.out.println("Accounts loaded from database\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void create(Account acc){
        accounts.add(acc);
    }

    public void showAccounts(){
        System.out.printf("%-15s %-15s %-10s\n", "Account No", "Type", "Balance");
        System.out.println("-------------------------------------------");
        for(Account acc : accounts){
            System.out.printf("%-15s %-15s %-10.2f\n",
                acc.getAccNo(),
                acc.getAccType(),
                acc.getBalance());
        }
    }
}
