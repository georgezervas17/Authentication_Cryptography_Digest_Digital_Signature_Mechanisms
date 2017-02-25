
    //Νικόλαος Φουρτούνης icsd13195
    //Παύλος Σκούπρας icsd13171
    //Γεώργιος Ζέρβας icsd13055

package secproject;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Graphics extends JFrame implements ActionListener {

    //Home Panel
    private GridLayout glayout;
    private JPanel[] panels = new JPanel[3];
    private JPanel homePanel = new JPanel();
    private JButton login_button, signup_button;

    //Signup Panel
    private JPanel signupPanel = new JPanel();
    private JLabel[] signupInfo = new JLabel[4];
    private JTextField[] infoFields = new JTextField[4];
    private JButton submit_signup, cancel_signup;

    //Login Panel
    private JPanel loginPanel = new JPanel();
    private JTextField username;
    private JPasswordField password;
    private JButton submit_login, cancel_login;

    //Functions Panel
    private JPanel functionsPanel = new JPanel();
    private JButton insert_button, edit_button, show_button;

    //Insert record Panel
    private JPanel insertPanel = new JPanel();
    private JButton income_button, outcome_button, back_button, exit_button;

    //Add transaction Panel (πληροφορίες για τις συναλλαγές απο τον χρήστη)
    private JPanel addPanel = new JPanel();
    private JTextField description, amount;
    private JFormattedTextField dateField;
    private JButton submit_add, cancel_add;
    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    final static int INCOME = 1;
    final static int OUTCOME = 0;
    int typeOfTransaction;

    //Edit transaction Panel 
    private JPanel editPanel = new JPanel();
    private JComboBox dateList, transactionsList;
    private JButton editTrans_button, back_edit;
    private ArrayList<String> dateToChoose = new ArrayList();
    private ArrayList<String> transactionToChoose = new ArrayList();
    private ArrayList<Transaction> transactions = new ArrayList();
    private Transaction transToEdit;
    private int choosenTrans;

    //Show transaction Panel 
    private JPanel showPanel = new JPanel();
    private JComboBox monthList;
    private JButton showTrans_button, back_show;
    private ArrayList<String> monthToChoose = new ArrayList();

    //Choosen transaction for editing
    private JButton save_button, back_edit2;
    private String operation = "";

    //Panel for choosen month transactions
    private JPanel showMonthPanel = new JPanel();
    private JTextArea textArea = new JTextArea();
    private JButton ok_button;

    Container pane = this.getContentPane();

    All_Functions all_functions;
    User logged_user;
    List<JTextField> tempList = new ArrayList();

    public Graphics() throws NoSuchAlgorithmException {

        //Ρύθμιση στοιχείων του πλαισίου
        super("Main Menu");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(500, 500);
        this.setVisible(true);

        //Κάθε φορά που ο χρήστης θα κλείνει την εφαρμογή θα καλείται η computeFileSignatures 
        //για τον μηχανισμό ακεραιότητας  
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (logged_user != null) {
                    all_functions.Compute_File_Signatures(logged_user, "signatures.dat");
                    System.out.println("phga gia upografh.");
                }//if

                e.getWindow().dispose();
            }
        });

        //Ρυθμίσεις για το αρχικό Panel που θα εμφανίσει το πρόγραμμα
        homePanel.setBackground(Color.LIGHT_GRAY);
        homePanel.setForeground(Color.DARK_GRAY);
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBorder(new EmptyBorder(100, 100, 100, 100));

        //Πρώτη γραμμή
        panels[0] = new JPanel();
        panels[0].setBackground(Color.LIGHT_GRAY);

        JLabel transmanagerLabel = new JLabel("Choose one option");
        transmanagerLabel.setForeground(Color.DARK_GRAY);
        transmanagerLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));

        panels[0].add(transmanagerLabel);

        //Δεύτερη γραμμή
        //Εισαγωγή μιας εικόνας 
        panels[1] = new JPanel();
        panels[1].setBackground(Color.LIGHT_GRAY);

        //Τρίτη γραμμή
        //Κουμπί για είσοδο στην εφαρμογή
        login_button = new JButton("Login");
        login_button.setBackground(Color.DARK_GRAY);
        login_button.setForeground(Color.WHITE);
        login_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        login_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Change_Panel(loginPanel);
            }
        });

        //Κουμπί για εγγραφή χρήστη
        signup_button = new JButton("Sign up");
        signup_button.setBackground(Color.DARK_GRAY);
        signup_button.setForeground(Color.WHITE);
        signup_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signup_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Change_Panel(signupPanel);
            }
        });

        //Ρύθμιση του Panel της τρίτης γραμμής και η εισαγωγή των κουμπιών σε αυτό
        panels[2] = new JPanel();
        panels[2].setBackground(Color.WHITE);

        panels[2].add(login_button);
        panels[2].add(signup_button);

        //Δημιουργία διαχειριστή διάταξης για την τρίτη γραμμή που έχει τα δύο κουμπιά
        glayout = new GridLayout(2, 1);
        panels[2].setLayout(glayout);

        //Εισαγωγή των panels στο γενικό Panel
        for (int i = 0; i < 3; i++) {
            homePanel.add(panels[i]);
        }//for

        //Εισαγωγή του panel στο frame
        pane.add(homePanel);
        pack();

        //Δημιουργία αντικειμένου του SecFunctions ώστε 
        //να χρησιμοποιήσουμε τις μεθόδους που χρειαζόμαστε
        all_functions = new All_Functions();

        Create_Panels();

    }//Constructor

    public void Create_Panels() {
        Functions_Panel();
        Login_Panel();
        Insert_Panel();
        Signup_Panel();
        Add_Transaction_Panel();
    }//createPanels

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        //**************SIGNUP PANEL****************//
        if (source == submit_signup) {

            String name = infoFields[0].getText();
            String surname = infoFields[1].getText();
            String userName = infoFields[2].getText();
            String password = infoFields[3].getText();

            //Έλεγχος για συμπλήρωση όλων των πεδίων, για σωστη δομή κωδικού
            //και για την ύπαρξη χρήστη με το logiName (σε όλες τις περιπτώσεις 
            //εμφανίζεται ανάλογο μύνημα λάθους)
            if (!Check_Fields(Arrays.asList(infoFields)) || !Check_Password(password)
                    || !If_User_Exist(userName)) {
                return;
            }//if

            User newUser = new User(name, surname, userName);

            //Αν ο χρηστης δημιουργήθηκε με επιτυχία, μπαίνει στις επιλογές της εφαρμογής
            if (all_functions.Create_User_Data(newUser, password)) {
                Change_Panel(functionsPanel);
                logged_user = newUser;
                transactions = all_functions.Get_All_Transactions(logged_user);
                JOptionPane.showMessageDialog(null, "Welcome to the system user "+username.getText()+" !", "Submit message", JOptionPane.PLAIN_MESSAGE);
            }//if
        }//sign up submit

        //**************LOGIN PANEL****************//
        if (source == submit_login) {

            tempList.clear();
            tempList.add(username);
            tempList.add(password);

            //Έλεγχος για συμπλήρωση όλων των πεδίων
            if (!Check_Fields(tempList)) {
                return;
            }//if

            //Αν η αυθεντικοποιήση πραγματοποιηθεί με επιτυχία τότε
            //εμφανίζεται το πανελ με τις επιλογές, αλλιώς εμφανίζεται σχετικό μύνημα
            if (all_functions.Authenticate_User(username.getText(), password.getText())) {

                Change_Panel(functionsPanel);
                logged_user = all_functions.Get_User(username.getText());

                if (all_functions.validateSignatures(logged_user) == 0) {
                    System.out.println("0");
                    JOptionPane.showMessageDialog(null, "Your are not authorized user "+username.getText()+" !", "Alert", JOptionPane.INFORMATION_MESSAGE);
                } else if (all_functions.validateSignatures(logged_user) == 1) {
                    System.out.println("1");
                    JOptionPane.showMessageDialog(null, "Welcome back authorized user "+username.getText()+" !", "File integrity", JOptionPane.INFORMATION_MESSAGE);
                }//else if

                transactions = all_functions.Get_All_Transactions(logged_user);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid data,try again", "Invalid information", JOptionPane.ERROR_MESSAGE);
                username.setText("");
                password.setText("");
            }//is else
        }//if submit login

        //**************ADD TRANSACTION PANEL****************//
        if (source == submit_add) {

            tempList.clear();
            tempList.add(dateField);
            tempList.add(amount);
            tempList.add(description);

            //Έλεγχος για συμπλήρωση όλων των πεδίων
            if (!Check_Fields(tempList)) {
                return;
            }//if

            Date date = new Date();

            try {
                date = df.parse(dateField.getText());
            } catch (ParseException ex) {
                Logger.getLogger(Graphics.class.getName()).log(Level.SEVERE, null, ex);
            }//try-catch//try-catch

            if (operation == "add") {

                Transaction newTrans = new Transaction();

                newTrans.setDate(date);
                newTrans.setAmount(amount.getText());
                newTrans.setDescription(description.getText());
                newTrans.setType(typeOfTransaction);

                all_functions.Save_New_Transaction(newTrans, logged_user, typeOfTransaction);
                Change_Panel(insertPanel);

                transactions = all_functions.Get_All_Transactions(logged_user);

                //Ενημέρωση της drop-down λίστας
                dateToChoose = Get_Dates_toString(1);

                if (dateList != null) {
                    for (int i = 0; i < dateToChoose.size(); i++) {
                        dateList.removeItemAt(1);
                        dateList.addItem(dateToChoose.get(i));
                    }//for
                }//if

            } else if (operation == "edit") {

                transToEdit.setDate(date);
                transToEdit.setAmount(amount.getText());
                transToEdit.setDescription(description.getText());

                all_functions.Save_Changed_Transaction(transToEdit, logged_user);
                transactions = all_functions.Get_All_Transactions(logged_user);
                dateToChoose = Get_Dates_toString(1);

                for (int i = 0; i < dateToChoose.size(); i++) {
                    dateList.removeItemAt(1);
                    dateList.addItem(dateToChoose.get(i));
                }//for

                Change_Panel(editPanel);
            }//else if  

            dateField.setText("");
            description.setText("");
            amount.setText("");
        }//if submit add

        //**************EDIT TRANSACTION PANEL****************//
        if (source == dateList && !dateList.getSelectedItem().toString().equals("-")) {
            transactionsList.removeAllItems();
            transactionToChoose = Transactions_Date(dateList.getSelectedItem().toString());

            for (int i = 0; i < transactionToChoose.size(); i++) {
                transactionsList.addItem(transactionToChoose.get(i));
            }//for
        }//if dateToChoose

        if (source == editTrans_button) {
            if (transactionsList.getSelectedItem().toString().equals("-") || dateList.getSelectedItem().toString().equals("-")) {
                JOptionPane.showMessageDialog(null, "All fields are required.", "Empty information", JOptionPane.ERROR_MESSAGE);
                return;
            }//if

            choosenTrans = Integer.parseInt(transactionsList.getSelectedItem().toString().replaceAll("\\D", ""));
            transToEdit = Transaction_By_ID(choosenTrans);
            Show_Transaction_Edit();
        }//if editTrans button

        if (source == back_edit) {
            transactionsList.removeAllItems();
            dateList.setSelectedIndex(0);
            Change_Panel(functionsPanel);
        }//if cancel edit

        //**************SHOW TRANSACTION PANEL****************//
        if (source == showTrans_button) {
            if (showMonthPanel.getComponentCount() == 0) {
                Show_Month_Transactions();
            }//if

            Show_Transaction_Details(monthList.getSelectedItem().toString());
            Change_Panel(showMonthPanel);
        }//if showTrans_button

        if (source == back_show) {
            Change_Panel(functionsPanel);
        }//if cancel_show

        if (source == ok_button) {
            Change_Panel(showPanel);
        }//if ok_button

    }//actionPerformed

    public void Login_Panel() {

        //Δημιουργία διαχειριστή διάταξης
        GridLayout gl = new GridLayout(3, 2);

        //Ρυθμίσεις για το πάνελ
        loginPanel.setLayout(gl);
        loginPanel.setBorder(new EmptyBorder(100, 100, 100, 100));
        loginPanel.setBackground(Color.LIGHT_GRAY);
        loginPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        //JLabels
        JLabel lnameLabel = new JLabel("Login name");
        JLabel passLabel = new JLabel("Password");

        //Buttons
        submit_login = new JButton("Submit");
        submit_login.setBackground(Color.DARK_GRAY);
        submit_login.setForeground(Color.WHITE);
        submit_login.setFont(new Font("Segoe UI", Font.BOLD, 15));

        cancel_login = new JButton("Cancel");
        cancel_login.setBackground(Color.DARK_GRAY);
        cancel_login.setForeground(Color.WHITE);
        cancel_login.setFont(new Font("Segoe UI", Font.BOLD, 15));

        submit_login.addActionListener(this);

        cancel_login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Change_Panel(homePanel);
            }
        });

        //Εισαγωσή στοιχείων στο πανελ        
        loginPanel.add(lnameLabel);
        username = new JTextField(15);
        username.setForeground(Color.DARK_GRAY);
        username.setBackground(Color.LIGHT_GRAY);
        username.setFont(new Font("Segoe UI", Font.BOLD, 15));

        loginPanel.add(username);
        loginPanel.add(passLabel);

        password = new JPasswordField(15);
        password.setForeground(Color.DARK_GRAY);
        password.setBackground(Color.LIGHT_GRAY);
        password.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginPanel.add(password);

        loginPanel.add(submit_login);
        loginPanel.add(cancel_login);

    }//create_LoginPanel

    public void Functions_Panel() {

        //Δημιουργία διαχειριστή διάταξης
        GridLayout gl = new GridLayout(2, 1);

        //Ρυθμίσεις για το πάνελ
        functionsPanel.setLayout(gl);
        functionsPanel.setBorder(new EmptyBorder(100, 100, 100, 100));
        functionsPanel.setBackground(Color.LIGHT_GRAY);
        functionsPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        //Δεύτερη γραμμή
        //JButtons
        insert_button = new JButton("Insert Transaction");
        insert_button.setBackground(Color.DARK_GRAY);
        insert_button.setForeground(Color.WHITE);
        insert_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        insert_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Change_Panel(insertPanel);
                operation = "add";
            }
        });

        edit_button = new JButton("Edit Transaction");
        edit_button.setBackground(Color.DARK_GRAY);
        edit_button.setForeground(Color.WHITE);
        edit_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        edit_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                operation = "edit";

                if (transactions.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "There are no transactions to edit.",
                            "Edit transaction", JOptionPane.PLAIN_MESSAGE);
                }//if
                else {
                    if (editPanel.getComponentCount() == 0) {
                        Edit_Transaction_Panel();
                    }//if

                    Change_Panel(editPanel);
                }//else                
            }//actionPerformed
        });

        show_button = new JButton("Show Transaction");
        show_button.setBackground(Color.DARK_GRAY);
        show_button.setForeground(Color.WHITE);
        show_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        show_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                if (transactions.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "There are no transactions to show.",
                            "Show transaction", JOptionPane.PLAIN_MESSAGE);
                }//if
                else {
                    if (showPanel.getComponentCount() == 0) {
                        Show_Transactions_Panel();
                    }//if

                    Change_Panel(showPanel);
                }//else
            }//actionPerformed
        });

        exit_button = new JButton("Exit");
        exit_button.setBackground(Color.DARK_GRAY);
        exit_button.setForeground(Color.WHITE);
        exit_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        exit_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        //Panel για τα κουμπια
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.LIGHT_GRAY);
        buttonsPanel.setLayout(new GridLayout(4, 1));

        buttonsPanel.add(insert_button);
        buttonsPanel.add(edit_button);
        buttonsPanel.add(show_button);
        buttonsPanel.add(exit_button);

        //Εισαγωγή των panels στο γενικό Panel
        functionsPanel.add(buttonsPanel);

    }//create_functionsPanel

    public void Insert_Panel() {

        //Δημιουργία διαχειριστή διάταξης
        GridLayout gl = new GridLayout(3, 1);

        //Ρυθμίσεις για το πάνελ
        insertPanel.setLayout(new BoxLayout(insertPanel, BoxLayout.Y_AXIS));
        insertPanel.setBorder(new EmptyBorder(100, 100, 100, 100));
        insertPanel.setBackground(Color.LIGHT_GRAY);
        insertPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        //Πρώτη γραμμή
        JPanel insertLabelPanel = new JPanel();
        insertLabelPanel.setBackground(Color.LIGHT_GRAY);

        JLabel insertLabel = new JLabel("Insert transraction");
        insertLabel.setForeground(Color.DARK_GRAY);
        insertLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));

        insertLabelPanel.add(insertLabel);

        //Τρίτη γραμμή    
        //JButtons
        income_button = new JButton("Income");
        income_button.setBackground(Color.DARK_GRAY);
        income_button.setForeground(Color.WHITE);
        income_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        income_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                typeOfTransaction = INCOME;
                Change_Panel(addPanel);
            }
        });

        outcome_button = new JButton("Outcome");
        outcome_button.setBackground(Color.DARK_GRAY);
        outcome_button.setForeground(Color.WHITE);
        outcome_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        outcome_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                typeOfTransaction = OUTCOME;
                Change_Panel(addPanel);
            }
        });

        back_button = new JButton("Back");
        back_button.setBackground(Color.DARK_GRAY);
        back_button.setForeground(Color.WHITE);
        back_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        back_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Change_Panel(functionsPanel);
            }
        });

        exit_button = new JButton("Exit");
        exit_button.setBackground(Color.DARK_GRAY);
        exit_button.setForeground(Color.WHITE);
        exit_button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        exit_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        //Panel για τα κουμπια
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.LIGHT_GRAY);
        buttonsPanel.setForeground(Color.DARK_GRAY);
        buttonsPanel.setLayout(new GridLayout(4, 1));

        buttonsPanel.add(income_button);
        buttonsPanel.add(outcome_button);
        buttonsPanel.add(back_button);
        buttonsPanel.add(exit_button);

        //Εισαγωγή των panels στο γενικό Panel
        insertPanel.add(insertLabelPanel);
        insertPanel.add(buttonsPanel);

        pack();

    }//create_InsertPanel

    public void Signup_Panel() {

        //Δημιουργία διαχειριστή διάταξης
        GridLayout gl = new GridLayout(5, 2);

        //Ρυθμίσεις για το πάνελ
        signupPanel.setLayout(gl);
        signupPanel.setBorder(new EmptyBorder(100, 100, 100, 100));
        signupPanel.setBackground(Color.LIGHT_GRAY);
        signupPanel.setForeground(Color.DARK_GRAY);
        signupPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        //JLabels
        signupInfo[0] = new JLabel("Name");
        signupInfo[1] = new JLabel("Surname");
        signupInfo[2] = new JLabel("Login name");
        signupInfo[3] = new JLabel("Password");

        //Buttons
        submit_signup = new JButton("Submit");
        submit_signup.setBackground(Color.DARK_GRAY);
        submit_signup.setForeground(Color.WHITE);
        submit_signup.setFont(new Font("Segoe UI", Font.BOLD, 15));

        cancel_signup = new JButton("Back");
        cancel_signup.setBackground(Color.DARK_GRAY);
        cancel_signup.setForeground(Color.WHITE);
        cancel_signup.setFont(new Font("Segoe UI", Font.BOLD, 15));

        submit_signup.addActionListener(this);

        cancel_signup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Change_Panel(homePanel);
            }
        });

        //Εισαγωσή στοιχείων στο πανελ        
        for (int i = 0; i < 4; i++) {

            signupPanel.add(signupInfo[i]);
            infoFields[i] = new JTextField(15);
            infoFields[i].setForeground(Color.DARK_GRAY);
            infoFields[i].setBackground(Color.LIGHT_GRAY);
            signupPanel.add(infoFields[i]);

        }//for

        signupPanel.add(submit_signup);
        signupPanel.add(cancel_signup);

    }//create_SignupPanel 

    public void Edit_Transaction_Panel() {

        //Δημιουργία διαχειριστή διάταξης
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        //Ρυθμίσεις για το πάνελ
        editPanel.setLayout(gbl);
        editPanel.setBorder(new EmptyBorder(100, 100, 100, 100));
        editPanel.setBackground(Color.LIGHT_GRAY);
        editPanel.setForeground(Color.DARK_GRAY);
        editPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel editLabelPanel = new JPanel();
        editLabelPanel.setBackground(Color.LIGHT_GRAY);
        editLabelPanel.setForeground(Color.DARK_GRAY);
        editLabelPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel editLabel = new JLabel("Edit transaction");
        editLabel.setForeground(Color.DARK_GRAY);
        editLabel.setBackground(Color.LIGHT_GRAY);
        editLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));

        editLabelPanel.add(editLabel);

        //JLabels
        JLabel dateLabel = new JLabel("Date of Transaction:");
        JLabel transLabel = new JLabel("Transactions:");

        //Καλούμε την συνάστηση της SecFunctions που επιστρέφει μια λίστα με 
        //τις συναλλαγές 
        transactions = all_functions.Get_All_Transactions(logged_user);
        dateToChoose = Get_Dates_toString(1);

        //Προσθέτουμε στις λίστες τα παρακάτω σαν προεπιλογή
        dateToChoose.add(0, "Choose");
        transactionToChoose.add(0, "Choose");

        //Dropdown list για τα κριτήρια αναζήτησης με τις λίστες που έχουμε
        dateList = new JComboBox(dateToChoose.toArray());
        dateList.setBackground(Color.LIGHT_GRAY);
        dateList.setForeground(Color.DARK_GRAY);
        dateList.setFont(new Font("Segoe UI", Font.BOLD, 15));

        transactionsList = new JComboBox(transactionToChoose.toArray());
        transactionsList.setBackground(Color.LIGHT_GRAY);
        transactionsList.setForeground(Color.DARK_GRAY);
        transactionsList.setFont(new Font("Segoe UI", Font.BOLD, 15));

        dateList.addActionListener(this);
        transactionsList.addActionListener(this);

        //Buttons
        editTrans_button = new JButton("Edit");
        editTrans_button.setForeground(Color.white);
        editTrans_button.setBackground(Color.DARK_GRAY);
        editTrans_button.setFont(new Font("Segoe UI", Font.BOLD, 15));

        back_edit = new JButton("Back");
        back_edit.setForeground(Color.white);
        back_edit.setBackground(Color.DARK_GRAY);
        back_edit.setFont(new Font("Segoe UI", Font.BOLD, 15));

        editTrans_button.addActionListener(this);
        back_edit.addActionListener(this);

        //Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.LIGHT_GRAY);
        infoPanel.setForeground(Color.DARK_GRAY);
        infoPanel.setLayout(new GridLayout(3, 2));

        infoPanel.add(dateLabel);
        infoPanel.add(dateList);
        infoPanel.add(transLabel);
        infoPanel.add(transactionsList);
        infoPanel.add(editTrans_button);
        infoPanel.add(back_edit);

        //Εισαγωσή στοιχείων στο πανελ 
        c.gridx = 0;
        c.gridy = 0;
        editPanel.add(editLabelPanel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridx = 0;
        c.gridy = 2;
        editPanel.add(infoPanel, c);

        pack();
    }//create_editTransactionPanel

    public void Add_Transaction_Panel() {

        //Δημιουργία διαχειριστή διάταξης
        GridLayout gl = new GridLayout(4, 2);

        dateField = new JFormattedTextField(df);

        //Ρυθμίσεις για το πάνελ
        addPanel.setLayout(gl);
        addPanel.setBorder(new EmptyBorder(100, 100, 100, 100));
        addPanel.setBackground(Color.LIGHT_GRAY);
        addPanel.setForeground(Color.DARK_GRAY);
        addPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        //JLabels
        JLabel dateLabel = new JLabel("Date");
        JLabel reportLabel = new JLabel("Report");
        JLabel amountLabel = new JLabel("Amount");

        //Buttons
        submit_add = new JButton("Submit");
        submit_add.setBackground(Color.DARK_GRAY);
        submit_add.setForeground(Color.WHITE);
        submit_add.setFont(new Font("Segoe UI", Font.BOLD, 15));

        cancel_add = new JButton("Back");
        cancel_add.setBackground(Color.DARK_GRAY);
        cancel_add.setForeground(Color.WHITE);
        cancel_add.setFont(new Font("Segoe UI", Font.BOLD, 15));

        submit_add.addActionListener(this);

        cancel_add.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (operation == "add") {
                    Change_Panel(insertPanel);
                } else if (operation == "edit") {
                    Change_Panel(editPanel);
                }//else if  

                dateField.setText("");
                description.setText("");
                amount.setText("");
            }//actionPerformed
        });

        //Εισαγωσή στοιχείων στο πανελ        
        addPanel.add(dateLabel);
        dateField.setForeground(Color.DARK_GRAY);
        dateField.setBackground(Color.LIGHT_GRAY);
        dateField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addPanel.add(dateField);

        addPanel.add(reportLabel);
        description = new JTextField(15);
        description.setForeground(Color.DARK_GRAY);
        description.setBackground(Color.LIGHT_GRAY);
        description.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addPanel.add(description);

        addPanel.add(amountLabel);
        amount = new JTextField(15);
        amount.setForeground(Color.DARK_GRAY);
        amount.setBackground(Color.LIGHT_GRAY);
        amount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addPanel.add(amount);

        addPanel.add(submit_add);
        addPanel.add(cancel_add);

    }//create_addTransactionPanel

    public void Show_Transactions_Panel() {

        //Δημιουργία διαχειριστή διάταξης
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        //Ρυθμίσεις για το πάνελ
        showPanel.setLayout(gbl);
        showPanel.setBorder(new EmptyBorder(100, 100, 100, 100));
        showPanel.setBackground(Color.LIGHT_GRAY);
        showPanel.setForeground(Color.DARK_GRAY);
        showPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel showLabelPanel = new JPanel();
        showLabelPanel.setBackground(Color.LIGHT_GRAY);
        showLabelPanel.setForeground(Color.DARK_GRAY);
        showLabelPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel showLabel = new JLabel("Show transractions");
        showLabel.setBackground(Color.LIGHT_GRAY);
        showLabel.setForeground(Color.DARK_GRAY);
        showLabel.setFont(new Font("Segoe UI", Font.BOLD,25));

        showLabelPanel.add(showLabel);

        //Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.LIGHT_GRAY);
        infoPanel.setLayout(new GridLayout(4, 1));

        //JLabel
        JLabel monthLabel = new JLabel("Choose a month:");
        monthLabel.setBackground(Color.LIGHT_GRAY);
        monthLabel.setForeground(Color.DARK_GRAY);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        //Καλούμε την συνάστηση της SecFunctions που επιστρέφει μια λίστα με 
        //τις συναλλαγές 
        transactions = all_functions.Get_All_Transactions(logged_user);
        monthToChoose = Get_Dates_toString(2);

        //Προσθέτουμε στις λίστες τα παρακάτω σαν προεπιλογή
        monthToChoose.add(0, "Choose");

        //Dropdown list για τα κριτήρια αναζήτησης με τις λίστες που έχουμε
        monthList = new JComboBox(monthToChoose.toArray());
        monthList.setBackground(Color.LIGHT_GRAY);
        monthList.setForeground(Color.DARK_GRAY);
        monthList.setFont(new Font("Segoe UI", Font.BOLD, 15));

        monthList.addActionListener(this);

        //Buttons
        showTrans_button = new JButton("Show");
        showTrans_button.setForeground(Color.white);
        showTrans_button.setBackground(Color.DARK_GRAY);
        showTrans_button.setFont(new Font("Segoe UI", Font.BOLD, 15));

        back_show = new JButton("Back");
        back_show.setForeground(Color.white);
        back_show.setBackground(Color.DARK_GRAY);
        back_show.setFont(new Font("Segoe UI", Font.BOLD, 15));

        showTrans_button.addActionListener(this);
        back_show.addActionListener(this);

        infoPanel.add(monthLabel);
        infoPanel.add(monthList);
        infoPanel.add(showTrans_button);
        infoPanel.add(back_show);

        //Εισαγωσή στοιχείων στο πανελ  
        c.gridx = 0;
        c.gridy = 0;
        showPanel.add(showLabelPanel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        showPanel.add(infoPanel, c);

    }//create_showTransactionsPanel

    public void Show_Transaction_Details(String monthSelected) {

        tempList.clear();
        //Χρησιμοποιείται για το υποερώτημα "Έκδοση αναφοράς για τα έσοδα και τα έξοδα ανά μήνα"

        //Τοποθετεί τις κατάλληλες πληροφορίες για τις συναλλαγες κάποιου μήνα που 
        //επέλεξε ο χρήστης στο textArea για εμφάνιση 
        textArea.setText("");

        ArrayList<Transaction> tempList = new ArrayList();
        String newline = "\n";

        //Αρχικά παίρνουμε όλες τις συναλλαγές ΕΣΟΔΩΝ σε μια λίστα
        //και τοποθετούμε τις πληροφορίες στο textArea      
        tempList.clear();
        tempList = Month_Transactions(monthSelected, INCOME);

        for (int i = 0; i < tempList.size(); i++) {
            textArea.append("Income - "+ new SimpleDateFormat("dd MMMM yyyy").format(tempList.get(i).getDate()) + newline);
            textArea.append(tempList.get(i).getAmount() + newline);
            textArea.append(tempList.get(i).getDescription() + newline);
            textArea.append(newline);
        }//for

        //Στην συνέχεια παίρνουμε όλες τις συναλλαγές ΕΞΟΔΩΝ σε μια λίστα
        //και τοποθετούμε τις πληροφορίες στο textArea
        tempList.clear();
        tempList = Month_Transactions(monthSelected, OUTCOME);

        for (int i = 0; i < tempList.size(); i++) {
            textArea.append("Outcome - "+new SimpleDateFormat("dd MMMM yyyy").format(tempList.get(i).getDate())+ newline);
            textArea.append(tempList.get(i).getAmount() + newline);
            textArea.append(tempList.get(i).getDescription() + newline);
            textArea.append(newline);
        }//for

        //Τέλος τοποθετούμε το σύνολο των εσόδων και εξόδων 
        textArea.append("Income Sum: ");
        textArea.append(Integer.toString(Get_Sum(INCOME)));
        textArea.append(newline);
        textArea.append("Ooucome Sum: ");
        textArea.append(Integer.toString(Get_Sum(OUTCOME)));
    }//printTransactionInfo

    public void Show_Month_Transactions() {

        //Δημιουργία διαχειριστή διάταξης
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        //Ρυθμίσεις για το πάνελ
        showMonthPanel.setLayout(gbl);
        showMonthPanel.setBorder(new EmptyBorder(100, 100, 100, 100));
        showMonthPanel.setBackground(Color.LIGHT_GRAY);
        showMonthPanel.setForeground(Color.DARK_GRAY);
        showMonthPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel showLabelPanel = new JPanel();
        showLabelPanel.setBackground(Color.LIGHT_GRAY);
        showLabelPanel.setForeground(Color.DARK_GRAY);
        showLabelPanel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel showLabel = new JLabel("Month transractions");
        showLabel.setBackground(Color.LIGHT_GRAY);
        showLabel.setForeground(Color.DARK_GRAY);
        showLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));

        showLabelPanel.add(showLabel);

        textArea = new JTextArea(6, 20);
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setForeground(Color.DARK_GRAY);
        textArea.setFont(new Font("Segoe UI", Font.BOLD, 15));
        textArea.setBounds(100, 100, 300, 100);
        //JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);

        ok_button = new JButton("OK");
        ok_button.setForeground(Color.white);
        ok_button.setBackground(Color.DARK_GRAY);
        ok_button.setFont(new Font("Segoe UI", Font.BOLD, 15));

        ok_button.addActionListener(this);

        c.gridx = 0;
        c.gridy = 0;
        showMonthPanel.add(showLabelPanel, c);
        c.gridx = 0;
        c.gridy = 1;
        showMonthPanel.add(textArea, c);
        c.gridx = 0;
        c.gridy = 2;
        showMonthPanel.add(ok_button, c);
    }//showMonthTransactions

    //ΒΟΗΘΗΤΙΚΕΣ ΣΥΝΑΡΤΗΣΕΙΣ
    public void Show_Transaction_Edit() {

        //Όταν ο χρήστης επιλέξει κάποια συγκεκριμένη συναλλαγή για τροποποίηση 
        //θα εμφανιστούν τα παλιά δεδομένα στα πεδία για να τα αλλάξει ο χρήστης όπως επιθυμεί
        Change_Panel(addPanel);

        dateField.setText(df.format(transToEdit.getDate()));
        description.setText(transToEdit.getDescription());
        amount.setText(transToEdit.getAmount());

    }//showTransactionToEdit

    public void Change_Panel(JPanel newPanel) {

        pane.removeAll();
        pane.add(newPanel);
        pane.revalidate();
        pane.repaint();
        pack();
    }//changePanel

    public ArrayList<String> Get_Dates_toString(int option) {

        /*Επιστρέφει τις ταξινομημένες (και χωρίς διπλοεγρραφές) ημερομηνίες 
          σε μορφή String για να τις βάλουμε στα JComboBox της διεπαφής.
          Για option=1 επιστρέφουμε τις ημερομηνίες ολόκληρες για το υποερώτημα 
          "Τροποποίηση στοιχείων εγγραφής εσόδων ή εξόδων" ενώ αν είναι option != 1
          επιστρέφει μόνο τον μήνα και το έτος για το υποερώτημα "Έκδοση αναφοράς για τα έσοδα και τα έξοδα ανά μήνα"
          όπου χρειαζόμαστε μόνο τον μήνα */
        ArrayList<String> datesList = new ArrayList();
        ArrayList<Date> tempList = Sort_Dates();

        DateFormat dateFormat = new SimpleDateFormat((option == 1) ? "dd MMMM yyyy" : "MMMM yyyy");

        //Αποθηκεύουμε σε μορφή String τις ημερομηνίες στην λίστα που θα γυρίσει
        //σαν αποτέλεσμα αυτή η συνάρτηση
        for (int i = 0; i < tempList.size(); i++) {
            if (datesList.contains(dateFormat.format(tempList.get(i)))) {
                continue;
            }
            datesList.add(dateFormat.format(tempList.get(i)));
        }//for

        return datesList;
    }//getDatesToString

    public ArrayList<String> Transactions_Date(String date) {

        //Επιστρέφει λίστα με τα ID συναλλαγών και τον τύπο τους 
        //που έχουν ώς ημερομηνία αυτή που δίνεται ώς παράμετρο.
        //Η λίστα αυτή είναι για να εμφανίσουμε τις συναλλαγές σε JComboBox (drop-down list)
        //ώστε να επιλέξει ο χρήστης μια για τροποποίηση
        ArrayList<String> transList = new ArrayList();
        Sort_Dates();
        String typeOfTrans;

        try {
            Date choosenDate = new SimpleDateFormat("dd MMMM yyyy").parse(date);

            transList.add("Choose");

            for (int i = 0; i < transactions.size(); i++) {

                if (transactions.get(i).getDate().compareTo(choosenDate) == 0) {

                    typeOfTrans = (INCOME == transactions.get(i).getType()) ? " - Income" : " - Outcome";
                    transList.add(Integer.toString(transactions.get(i).getId()) + typeOfTrans);
                }//if
            }//for 

        } catch (ParseException ex) {
            Logger.getLogger(Graphics.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        return transList;
    }//getTransactionsWithDate

    public ArrayList<Transaction> Month_Transactions(String month, int typeOfTrans) {

        //Επιστρέφει λίστα με τις συναλλαγές που έγιναν τον μήνα που δίνεται ως παράμετρο
        // και έχουν τύπο = typeOfTrans. Η παράμετρος typeOfTrans είναι για να μπορούμε 
        //να παίρνουμε μόνο τις συναλλαγές εσόδων ή μόνο εξόδων, ώστε να μπορούμε κατα την 
        //"Έκδοση αναφοράς για τα έσοδα και τα έξοδα ανά μήνα" να εμφανίζουμε με σειρά 
        //(πχ πρώτα τα έσοδα και μετά τα έξοδα
        ArrayList<Transaction> transList = new ArrayList();
        ArrayList<Date> uniqueDates = Sort_Dates();

        DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

        for (int i = 0; i < uniqueDates.size(); i++) {
            for (int j = 0; j < transactions.size(); j++) {

                if (dateFormat.format(transactions.get(j).getDate()).equals(month)
                        && typeOfTrans == transactions.get(j).getType()
                        && dateFormat.format(transactions.get(j).getDate()).equals(dateFormat.format(uniqueDates.get(i)))) {

                    if (transList.contains(transactions.get(j))) {
                        continue;
                    }
                    transList.add(transactions.get(j));
                }//if
            }//for j
        }//for i

        return transList;
    }//getTransactionsWithMonth

    public int Get_Sum(int typeOfTrans) {

        //Υπολογισμός συνολικού αθροίσματος εσόδων ή εξόδων 
        int sum = 0;

        for (int i = 0; i < transactions.size(); i++) {

            if (typeOfTrans == transactions.get(i).getType()) {
                sum += Integer.parseInt(transactions.get(i).getAmount());
            }//if
        }//for 

        return sum;
    }//getSumOf

    public Transaction Transaction_By_ID(int ID) {

        //Επιστρέφει την συναλλαγή με συγκεκριμένο ID
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId() == ID) {
                return transactions.get(i);
            }
        }//for

        return null;
    }//getTransactionByID

    public ArrayList<Date> Sort_Dates() {

        ArrayList<Date> tempList = new ArrayList();

        //Σε προσωρινή λίστα αποθηκεύει τις ημερομηνίες των συναλλαγών των εξόδων και εσόδων
        for (int i = 0; i < transactions.size(); i++) {
            if (tempList.contains(transactions.get(i).getDate())) {
                continue;
            }
            tempList.add(transactions.get(i).getDate());
        }//for       

        //Ταξινομεί τις ημερομηνίες για να τις εμφανίσουμε στον χρήστη για επιλογή
        Collections.sort(tempList, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        return tempList;
    }//sortDates

    public boolean Check_Fields(List<JTextField> fieldsList) {

        for (int i = 0; i < fieldsList.size(); i++) {
            if (fieldsList.get(i).getText().equals("")) {
                JOptionPane.showMessageDialog(null, "All fields are required.", "Empty information", JOptionPane.ERROR_MESSAGE);
                return false;
            }//if
        }//for

        return true;
    }//checkAllFields

    public boolean Check_Password(String password) {

       
        String PASSWORD_PATTERN
                = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            JOptionPane.showMessageDialog(null, "Password must contains one digit from 0-9 ,to \n"
                    + "have length at least 6 and maximum of 20 characters ,also \n"
                    + "one uppercase letter, one lowercase\n"
                    + "character and one special symbol (“!@#$%&*”).", "Invalid password\n", JOptionPane.ERROR_MESSAGE);
        }//if

        return matcher.matches();

    }//checkPassword

    public boolean If_User_Exist(String username) {

        if (all_functions.Get_User(username) != null) {
            JOptionPane.showMessageDialog(null, "User with login name = \"" + username
                    + "\"is already exist. Choose Another one", "Erorr message", JOptionPane.ERROR_MESSAGE);
            return false;
        }//if

        return true;
    }//checkIfUserExist

}//SecProjectFrame
