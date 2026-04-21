import java.sql.*;

class Transaction{
    public void deposit(Account acc, double amount) throws InvalidTransactionException{
        if(amount <= 0){
            throw new InvalidTransactionException("Deposit amount must be greater than zero!");
        }
        Connection con = null;
        try{
            con = Database.connect();
            con.setAutoCommit(false);
            String query = "UPDATE accounts SET balance = balance + ? WHERE accNo = ?";
            PreparedStatement st = con.prepareStatement(query);
            st.setDouble(1, amount);
            st.setString(2, acc.getAccNo());
            st.executeUpdate();
            
            insertTransaction(con, acc.getAccNo(),"DEPOSIT", amount, "Deposit of " + amount);
            con.commit();

            acc.setBalance(acc.getBalance() + amount);
            System.out.println("The amount " + amount + " is deposited successfully!");
            System.out.println("The current balance is: " + acc.getBalance());

        }catch(Exception e){
            try{
                if(con!=null) con.rollback();
            } catch(Exception ex){
                ex.printStackTrace();
            }
            System.out.println("Transaction failed!");
            e.printStackTrace();
        }finally{
            try{
                if(con!=null) con.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void withdraw(Account acc, double amount) throws InvalidTransactionException{
        if(amount <= 0){
            throw new InvalidTransactionException("Amount must be greater than zero!");
        }

        if(acc.getBalance()<amount){
            throw new InvalidTransactionException("Insuffiencient Balance");
        }
        Connection con = null;
        try{
            con = Database.connect();
            con.setAutoCommit(false);
            String query = "UPDATE accounts SET balance = balance - ? WHERE accNo = ?";
            PreparedStatement st = con.prepareStatement(query);
            st.setDouble(1, amount);
            st.setString(2, acc.getAccNo());
            st.executeUpdate();
            
            insertTransaction(con, acc.getAccNo(),"WITHDRAW", amount, "Withdrawal of " + amount);
            con.commit();

            acc.setBalance(acc.getBalance() - amount);
            System.out.println("The amount " + amount + " is withdrawn successfully!");
            System.out.println("The current balance is: " + acc.getBalance());

        }catch(Exception e){
            try{
                if(con!=null) con.rollback();
            } catch(Exception ex){
                ex.printStackTrace();
            }
            System.out.println("Transaction failed!");
            e.printStackTrace();
        }finally{
            try{
                if(con!=null) con.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void transfer(Account sender, Account receiver, double amount) throws InvalidTransactionException {
        if (amount<=0) {
            throw new InvalidTransactionException("Invalid amount transfer!");
        }
        if (sender.getBalance()<amount) {
            throw new InvalidTransactionException("Insufficient funds!");
        }
        Connection con = null;
        try{
            con = Database.connect();
            con.setAutoCommit(false);

            String debit = "UPDATE accounts SET balance = balance - ? WHERE accNo = ?";
            PreparedStatement st1 = con.prepareStatement(debit);
            st1.setDouble(1, amount);
            st1.setString(2, sender.getAccNo());
            st1.executeUpdate();

            String credit = "UPDATE accounts SET balance = balance + ? WHERE accNo = ?";
            PreparedStatement st2 = con.prepareStatement(credit);
            st2.setDouble(1, amount);
            st2.setString(2, receiver.getAccNo());
            st2.executeUpdate();

            insertTransaction(con, sender.getAccNo(),"TRANSFER", amount, "Transferred " + amount + " to " + receiver.getAccNo());
            insertTransaction(con, receiver.getAccNo(),"TRANSFER", amount, "Received " + amount + " from " + sender.getAccNo());
            con.commit();

            sender.setBalance(sender.getBalance() - amount);
            receiver.setBalance(receiver.getBalance() + amount);

            System.out.println("Transaction successful!");
        }catch(Exception e){
            try{
                if(con!=null) con.rollback();
            } catch(Exception ex){
                ex.printStackTrace();
            }
            System.out.println("Transaction failed!");
            e.printStackTrace();
        }finally{
            try{
                if(con!=null) con.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void insertTransaction(Connection con, String accNo, String type, double amount, String desc) throws SQLException{
        String t = "INSERT INTO transactions(accNo, type, amount, description) VALUES (?, ?, ?, ?)";
            PreparedStatement st = con.prepareStatement(t);
            st.setString(1, accNo);
            st.setString(2, type);
            st.setDouble(3, amount);
            st.setString(4, desc);
            st.executeUpdate();
    }

    public void showHistory(String accNo, int limit){
        try{
            Connection con = Database.connect();
            String sql = "Select * from transactions where accNo = ? and (type IN ('DEPOSIT', 'WITHDRAW') or (type = 'TRANSFER' and description like 'Transferred%')) Order by date desc limit ?";
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, accNo);
            st.setInt(2, limit);
            ResultSet rs = st.executeQuery();
            System.out.println("\n-----Transaction History:----");
            System.out.printf("%-12s %-10s %-20s %-20s\n" , "Type", "Amount", "Description","Date");
            System.out.println("------------------------------");
            boolean hasData = false;
            while(rs.next()){
                hasData = true;
                System.out.printf("%-12s %-10.2f %-20s %-20s\n",  
                rs.getString("type"), 
                rs.getDouble("amount"), 
                rs.getString("description"),
                rs.getTimestamp("date"));
            }
            if(!hasData){
                System.out.println("No transactions found.");
            }
        }catch(Exception e){
                System.out.println("Error! Try again later.");
                e.printStackTrace();
        }
    }
}