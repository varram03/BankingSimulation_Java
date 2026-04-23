import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    private static String generateAccountNumber(String userId) {
        String prefix = userId.length() >= 2 ? userId.substring(0, 2).toUpperCase() : userId.toUpperCase();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);
        return prefix + timestamp;
    }

    public static void main(String args[]) {
        Authentication a = new Authentication();
        Transaction t = new Transaction();
        User u = null;
        String curUser = null;

        System.out.println("\n------- Welcome to the Banking Simulation!---------");

        while (u == null) {
            System.out.println("\n1.Register");
            System.out.println("2.Login");
            System.out.print("Choose an option: ");
            int op = sc.nextInt();
            sc.nextLine();
            try {
                if (op == 1) {
                    handleRegister(a);
                } else if (op == 2) {
                    u = handlelogin(a);
                    if (u != null) {
                        curUser = u.getUserId();
                    }
                } else {
                    System.out.println("Invalid option! Try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred:");
                e.printStackTrace();
            }
        }

        boolean session = true;
        while (session) {
            System.out.println("\n------Menu------");
            System.out.println("1. Show Accounts");
            System.out.println("2. Create Account");
            System.out.println("3. Select Account");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int op = sc.nextInt();
            sc.nextLine();
            try {
                switch (op) {
                    case 1:
                        u.showAccounts();
                        break;
                    case 2:
                        createAccount(u, curUser);
                        break;
                    case 3:
                        selectAccount(u, t);
                        break;
                    case 4:
                        session = false;
                        System.out.println("Logged out successfully!");
                        break;
                    default:
                        System.out.println("Invalid option! Try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred:");
                e.printStackTrace();
            }
        }
        sc.close();
    }

    private static void handleRegister(Authentication a) throws Exception {
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        a.registerUser(userId, password);
    }

    private static User handlelogin(Authentication a) throws Exception {
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        int res = a.loginUser(userId, password);
        if (res == 1) {
            System.out.println("Login successful!");
            System.out.println(" ");
            User u = new User(userId, password);
            u.loadFromDB();
            if (u.getAccounts().size() == 0) {
                System.out.println("No accounts found! Please create an account.");
            } else {
                System.out.println("Your accounts: ");
                u.showAccounts();
            }
            return u;
        } else if (res == 0) {
            System.out.println("Incorrect password! Try again.");
        } else if (res == -1) {
            System.out.println("User not found! Try again.");
        } else {
            System.out.println("Database connection failed!");
        }
        return null;
    }

    private static void createAccount(User u, String userId) {
        System.out.println("Enter the account type (savings/current): ");
        String atype = sc.nextLine();
        System.out.println("Enter the initial balance amount: ");
        double bal = sc.nextDouble();
        sc.nextLine();
        String accNo = generateAccountNumber(userId);
        Account acc = new Account(accNo, atype, bal);
        u.create(acc);
        acc.saveToDB(userId);
        System.out.println("Account created to database.");
        System.out.println("Your generated account number is: " + accNo);
        u.loadFromDB();
    }

    private static void selectAccount(User u, Transaction t) {
        if (u.getAccounts().isEmpty()) {
            System.out.println("No accounts found! Please create an account first.");
            return;
        }
        u.showAccounts();
        System.out.print("Enter the account number: ");
        String accNo = sc.next();
        sc.nextLine();
        Account selected = null;
        for (Account acc : u.getAccounts()) {
            if (acc.getAccNo().equals(accNo)) {
                selected = acc;
                break;
            }
        }
        if (selected == null) {
            System.out.println("Account not found! Try again.");
            return;
        }
        accountmenu(selected, t);
    }

    private static void accountmenu(Account acc, Transaction t) {
        boolean Loggedin = true;
        while (Loggedin) {
            System.out.println("\n------Account Menu------");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. View Transaction History");
            System.out.println("5. Switch Account");
            System.out.println("6.Logout");

            System.out.print("Choose an option: ");
            int op = sc.nextInt();
            sc.nextLine();

            try {
                switch (op) {
                    case 1:
                        System.out.println("Enter amount to deposit: ");
                        double dAmt = sc.nextDouble();
                        sc.nextLine();
                        t.deposit(acc, dAmt);
                        break;
                    case 2:
                        System.out.println("Enter amount to withdraw: ");
                        double wAmt = sc.nextDouble();
                        sc.nextLine();
                        t.withdraw(acc, wAmt);
                        break;
                    case 3:
                        System.out.println("Enter receiver account number: ");
                        String rAccNo = sc.nextLine();
                        System.out.println("Enter amount to transfer: ");
                        double tAmt = sc.nextDouble();
                        sc.nextLine();
                        Account receiver = fetch(rAccNo);
                        if (receiver != null) {
                            t.transfer(acc, receiver, tAmt);
                        } else {
                            System.out.println("Receiver account not found! Try again.");
                        }
                        break;
                    case 4:
                        System.out.println("Enter number of transactions: ");
                        int limit = sc.nextInt();
                        sc.nextLine();
                        t.showHistory(acc.getAccNo(), limit);
                        break;
                    case 5:
                        Loggedin = false;
                        break;
                    case 6:
                        System.out.println("Logged out successfully!");
                        return;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (InvalidTransactionException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("An error occurred:");
                e.printStackTrace();
            }
        }
    }

    private static Account fetch(String accNo) {
        try (Connection con = Database.connect()) {
            String sql = "SELECT * FROM accounts WHERE accNo = ?";
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, accNo);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getString("accNo"),
                        rs.getString("accType"),
                        rs.getDouble("balance"));
            }
        } catch (Exception e) {
            System.out.println("Error, try again!");
            e.printStackTrace();
        }
        return null;
    }
}
