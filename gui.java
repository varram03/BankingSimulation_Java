import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class gui extends JFrame{
    Authentication auth = new Authentication();
    Transaction t = new Transaction();
    User currUser;
    Account selected;
    private String pending = "";
    CardLayout card = new CardLayout();
    JPanel container = new JPanel(card);

    public gui(){
        setTitle("Banking Simulation");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        container.add(loginPanel(), "login");
        container.add(usermenuPanel(), "userMenu");
        container.add(accMenu(), "accountMenu");
        container.add(passwordPanel(), "verify");
        container.add(createAccountPanel(), "createAccount");
        add(container);
        card.show(container,"login");
        setVisible(true);
    }
    private JPanel loginPanel(){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        JTextField user = new JTextField(15);
        JPasswordField pass = new JPasswordField(15);
        JButton loginButton = new JButton("LOGIN");
        JButton regButton = new JButton("REGISTER");
        g.insets = new Insets(10,10,10,10);
        g.gridx = 0;
        g.gridy = 0;
        p.add(new JLabel("User ID"),g);
        g.gridx = 1;
        p.add(user,g);
        g.gridx = 0;
        g.gridy = 1;
        p.add( new JLabel("Password"),g);
        g.gridx = 1;
        p.add(pass,g);
        g.gridx = 0;
        g.gridy = 2;
        p.add(loginButton,g);
        g.gridx = 1;
        p.add(regButton,g);
        loginButton.addActionListener(e->{
            try{
                String username = user.getText();
                String password = new String(pass.getPassword());
                int res = auth.loginUser(username,password);
                if(res==1){
                    currUser = new User(username, password);
                    currUser.loadFromDB();
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    card.show(container, "userMenu");
                }
                else if(res==0){
                    JOptionPane.showMessageDialog(this, "Incorrect password.");
                }
                else if(res==-1){
                    JOptionPane.showMessageDialog(this,"User does not exist. Please register first.");
                }
                else{
                    JOptionPane.showMessageDialog(this,"Database connection failed.");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
        regButton.addActionListener(e->{
            try{
                boolean valid = auth.registerUser(user.getText(), new String(pass.getPassword()));
                if (valid){
                    JOptionPane.showMessageDialog(this,"Registration Successful!");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
        return p;
    }

    private JPanel usermenuPanel(){
        JPanel p = new JPanel(new GridLayout(4,1,15,15));
        p.setBorder(BorderFactory.createEmptyBorder(40,80,40,80));
        JButton showButton = new JButton("Show Accounts");
        JButton createButton = new JButton("Create Account");
        JButton selectButton = new JButton("Select Account");
        JButton logoutButton = new JButton("Logout");
        p.add(showButton);
        p.add(createButton);
        p.add(selectButton);
        p.add(logoutButton);
        showButton.addActionListener(e->showAccounts());
        createButton.addActionListener(e->card.show(container, "createAccount"));
        selectButton.addActionListener(e->{
            selected = chooseAccount();
            if(selected!=null)
                card.show(container, "accountMenu");
        });
        logoutButton.addActionListener(e->{
            currUser = null;
            selected = null;
            card.show(container, "login");
    });
        return p;
    }

    private JPanel accMenu(){
        JPanel p = new JPanel(new GridLayout(6,1,12,12));
        p.setBorder(BorderFactory.createEmptyBorder(30,80,30,80));
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton historyButton = new JButton("Transaction History");
        JButton switchButton = new JButton("Switch Account");
        JButton logoutButton = new JButton("Logout");
        p.add(depositButton);
        p.add(withdrawButton);
        p.add(transferButton);
        p.add(historyButton);
        p.add(switchButton);
        p.add(logoutButton);
        depositButton.addActionListener(e->{
            pending = "deposit";
            card.show(container, "verify");
        });
        withdrawButton.addActionListener(e->{
            pending = "withdraw";
            card.show(container, "verify");
        });
        transferButton.addActionListener(e->{
            pending = "transfer";
            card.show(container, "verify");
        });
        historyButton.addActionListener(e->{
            try{
                String n = JOptionPane.showInputDialog("Number of Transactions");
                if(n!=null){
                    t.showHistory(selected.getAccNo(), Integer.parseInt(n));
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
        switchButton.addActionListener(e->{
            card.show(container,"userMenu");
        });
        logoutButton.addActionListener(e->{
            currUser = null;
            selected = null;
            card.show(container,"login");
        });
        return p;
    }

    private JPanel passwordPanel(){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        JLabel jl = new JLabel("Re-enter your password to continue");
        JPasswordField verify = new JPasswordField(15);
        JButton verifyButton = new JButton("Verify");
        JButton cancelButton = new JButton("Back");
        g.insets = new Insets(10,10,10,10);
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth= 2;
        p.add(jl,g);
        g.gridy = 1;
        p.add(verify,g);
        g.gridwidth = 1;
        g.gridx = 0;
        g.gridy = 2;
        p.add(verifyButton,g);
        g.gridx = 1;
        p.add(cancelButton,g);
        verifyButton.addActionListener(e->{
            try{
                String pwd = new String(verify.getPassword());
                int res = auth.loginUser(currUser.getUserId(), pwd);
                if(res!=1){
                    JOptionPane.showMessageDialog(this, "Wrong Password. Exiting...");
                    return;
                }
                performOperation();
                verify.setText("");
            }catch(Exception ex){
                ex.printStackTrace();;
            }
        });
        cancelButton.addActionListener(e->{
            verify.setText("");
            card.show(container,"accountMenu");
        });
        return p;
    }

    private void performOperation(){
        JFrame tFrame = new JFrame("Transaction");
        tFrame.setSize(350,200);
        tFrame.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        String labelText = "Enter Amount";
        if(pending.equals("deposit")){
            labelText = "Enter the deposit amount";
        }
        else if(pending.equals("withdraw")){
            labelText = "Enter the withdrawal amount";
        }
        else if(pending.equals("transfer")){
            labelText = "Enter the amount to be transferred";
        }
        JLabel label = new JLabel(labelText);
        JTextField amtField = new JTextField(15);
        JButton confirmButton = new JButton("Confirm");
        g.insets = new Insets(10,10,10,10);    
        g.gridx = 0;
        g.gridy = 0;
        p.add(label,g);
        g.gridy=1;
        p.add(amtField,g);
        g.gridy = 2;
        p.add(confirmButton,g);
        tFrame.add(p);
        tFrame.setVisible(true);
        confirmButton.addActionListener(e->{
            try{
                double amt = Double.parseDouble(amtField.getText());
                if(pending.equals("deposit")){
                    t.deposit(selected, amt);
                    JOptionPane.showMessageDialog(tFrame, "Deposit Successful!\nCurrent Balance: "+ selected.getBalance()); 
                    card.show(container,"accountMenu");
                }
                else if(pending.equals("withdraw")){
                    t.withdraw(selected, amt);
                    JOptionPane.showMessageDialog(tFrame, "Withdrawal Successful!\nCurrent Balance: "+ selected.getBalance());
                    card.show(container,"accountMenu");
                }
                else if(pending.equals("transfer")){
                    String r = JOptionPane.showInputDialog(tFrame, "Receiver Account Number:");
                    Account receiver = Main.fetch(r);
                    if(receiver == null){
                        JOptionPane.showMessageDialog(tFrame, "Receiver Account Not Found");
                        return;
                    }
                    t.transfer(selected,receiver,amt);
                    JOptionPane.showMessageDialog(tFrame, "Transfer Successful!\nCurrent Balance: "+ selected.getBalance());
                    card.show(container,"accountMenu");
                }
                tFrame.dispose();
            } catch(InvalidTransactionException ex){
                JOptionPane.showMessageDialog(tFrame,ex.getMessage());
            }
            catch(Exception ex){
                JOptionPane.showMessageDialog(tFrame, "Transaction failed.");
            }
        });
    }

    private Account chooseAccount(){
        List<Account> accts = currUser.getAccounts();
        String[] accNos = new String[accts.size()];
        for(int i = 0;i<accts.size();i++)
            accNos[i] = accts.get(i).getAccNo();
        String selected = (String)JOptionPane.showInputDialog(
            this, 
            "Choose Account",
            "Accounts",
            JOptionPane.PLAIN_MESSAGE,
            null,
            accNos,
            accNos[0]
        );
        for(Account a:accts){
            if(a.getAccNo().equals(selected))
                return a;
        }
        return null;
    }

    private JPanel createAccountPanel(){
        JPanel p =new JPanel(new GridBagLayout());
        GridBagConstraints g =new GridBagConstraints();
        JRadioButton savings = new JRadioButton("Savings",true);
        JRadioButton current = new JRadioButton("Current");
        ButtonGroup grp = new ButtonGroup();
        grp.add(savings);
        grp.add(current);
        JLabel accLabel = new JLabel("Account number will be auto generated");
        JTextField balField = new JTextField(15);
        JButton createButton = new JButton("Create Account");
        JButton backButton = new JButton("Back");
        g.insets = new Insets(10, 10, 10, 10);
        g.gridx = 0;
        g.gridy = 0;
        p.add(new JLabel("Choose Account Type"),g);
        g.gridy = 1;
        p.add(savings,g);
        g.gridy = 2;
        p.add(current,g);
        g.gridy = 3;
        p.add(accLabel,g);
        g.gridy = 4;
        p.add(new JLabel("Initial Balance"),g);
        g.gridy = 5;
        p.add(balField,g);
        g.gridy = 6;
        p.add(createButton,g);
        g.gridy = 7;
        p.add(backButton,g);
        createButton.addActionListener(e->{
            try{
                double bal = Double.parseDouble(balField.getText());
                String type = savings.isSelected() ? "Savings" : "Current";
                String accNo = Main.generateAccountNumber(currUser.getUserId(), type);
                Account acc = new Account(accNo, type, bal);
                currUser.create(acc);
                acc.saveToDB(currUser.getUserId());
                JOptionPane.showMessageDialog(this, "Account created successfully");
                balField.setText("");
                card.show(container, "userMenu");
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
        backButton.addActionListener(e->card.show(container,"userMenu"));
        return p;
    }

    private void showAccounts(){
        String[] cols = {"Account Number", "Type", "Balance"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        for(Account a:currUser.getAccounts()){
            model.addRow(new Object[]{a.getAccNo(), a.getAccType(), a.getBalance()});
        }
        JTable table = new JTable(model);
        JOptionPane.showMessageDialog(this, new JScrollPane(table));    
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(gui::new);
    }
}

