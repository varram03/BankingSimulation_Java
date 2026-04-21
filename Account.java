import java.sql.*;

class Account{
    private String accNo;
    private String accType;
    private double bal;

    public Account(String accNo, String accType,double bal){
        this.accNo = accNo;
        this.accType = accType;
        this.bal = bal;
    }

    public String getAccNo(){
        return accNo;
    }

    public double getBalance(){
        return bal;
    }
    
    public String getAccType(){
        return accType;
    }

    public void setBalance(double balance) {
        this.bal = balance;
    }

    @Override
    public String toString(){
        return "Account Number: " +accNo+ ", Type: " +accType+ ", Balance: " +bal;
    }

    public void saveToDB(String userId){
        try(Connection con = Database.connect()){
            String sql = "INSERT INTO accounts (accNo, userId, accType, balance) VALUES (?, ?, ?, ?)";
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, accNo);
            st.setString(2, userId);
            st.setString(3, accType);
            st.setDouble(4, bal);
            st.executeUpdate();
        }catch(Exception e){
            System.out.println("Error saving!");
            e.printStackTrace();
        }       
    }
}