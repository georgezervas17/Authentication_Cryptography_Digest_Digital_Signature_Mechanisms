//Γεώργιος Ζέρβας icsd13055
//Νικόλαος Φουρτούνης icsd13195
//Παύλος Σκούπρας icsd13171

package secproject;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.FileUtils;

public class All_Functions {

    public All_Functions() throws NoSuchAlgorithmException {

        //Στην πρώτη εκτέλεση θα δημιουργηθεί το δημόσιο και ιδιωτικό κλειδί και 
        //θα αποθηκευτουν σε αντιστοιχο αρχείο 
        if (!(new File("publicKey")).exists() && !(new File("privateKey")).exists()) {

            //Δημιουργία δημόσιου και ιδιωτικού κλειδιού αν δεν υπάρχει
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair key = keyGen.generateKeyPair();

            Write_to_File("privateKey", key.getPrivate());
            Write_to_File("publicKey", key.getPublic());
        }//if
    }//Constructor

    //***************************************************************
    //****************ΔΙΑΧΕΙΡΗΣΗ USERNAME-PASSWORD*******************
    //***************************************************************
    
    
    //ΚΡΥΠΤΟΓΡΑΦΗΣΗ ΧΡΗΣΤΗ ΚΑΙ ΚΩΔΙΚΟY ΧΡΗΣΤΗ , ΓΙΑ SIGN UP
    // hash = salt+password
    //encryp_key = hash + public_key -> ΚΡΥΠΤΟΓΡΑΦΗΣΗ 
    public void Encrypt_User_Password(User username, String password) throws NoSuchAlgorithmException {

        //Δημιουργία τυχαίου αλφαριθμητικού salt και αποθηκευσή του
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] tempSalt = new byte[16];
        sr.nextBytes(tempSalt);
        String salt = tempSalt.toString();

        //Δημιουργία σύνοψης κλειδιού
        byte[] hashedPass = Hash_Password(password, salt);

        //Διαβάζει το δημόσιο κλειδί από το αρχείο
        PublicKey publicKey = (PublicKey) readFromFile("publicKey");

        //Κρυπτογράφηση σύνοψης με το δημόσιο κλειδί
        SealedObject encryptedPass = Encrypt_Key(publicKey, hashedPass, "RSA");

        //Θέτουμε τις τιμές salt και encryptedPass στον χρηστη
        username.setSalt(salt);
        username.setEncryptedPass(encryptedPass);

    }//encryptUserPassword

    //ΑΥΘΕΝΤΙΚΟΠΟΙΗΣΗ ΧΡΗΣΤΗ ΓΙΑ ΤΟ LOGIN
    public boolean Authenticate_User(String username, String password) {

        //Αυθεντικοποιεί τον χρήστη.
        try {
            //Αν ο χρήστης με login name = givenName υπάρχει, αποθηκεύεται σε μεταβλητή 
            User user = Get_User(username);

            //Υπολογίζεται η σύνοψη του κωδικού που δώθηκε κατα την είσοδο (givenPass)
            //μαζί με το salt του χρήστη με login name = givenName
            byte[] givenHashedPass = Hash_Password(password, user.getSalt());

            //Αποκρυπτογραφούμε την κρυπτογραφημένη σύνοψη του χρήστη με όνομα givenName με το ιδιωτικό κλειδι
            PrivateKey privateKey = (PrivateKey) readFromFile("privateKey");
            byte[] descryptedHash = Decrypt_Key(privateKey, user.getEncryptedPass(), "RSA");

            //Μετατροπή των byte[] σε String για να μπορέσουμε να τα συγκρίνουμε
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < givenHashedPass.length; i++) {
                sb.append(Integer.toString((givenHashedPass[i] & 0xff) + 0x100, 16).substring(1));
            }//for

            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < descryptedHash.length; i++) {
                sb2.append(Integer.toString((descryptedHash[i] & 0xff) + 0x100, 16).substring(1));
            }//for

            //Αν οι δύο συνόψεις είναι ίδιες επιστρέφει true και γίνεται επιτυχής σύνδεση
            if (sb.toString().equals(sb2.toString())) {
                return true;
            }//if

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            return false;
        }//try-catch

        return false;
    }//authenticateUser

    // ΓΙΑ ΤΗΝ ΑΥΘΕNΤΙΚΟΠΟΙΗΣΗ.
    //ΑΠΟΚΡΥΠΤΟΓΡΑΦΗΣΗ ΚΛΕΙΔΙΟΥ
    public byte[] Decrypt_Key(Key decryption_key, SealedObject pass_decrypt, String algorithm) {

        //Συνάρτηση για την αποκρυπτογράφηση κλειδιού
        Cipher cipher;
        byte[] decryptedKey = null;

        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, decryption_key);
            decryptedKey = (byte[]) pass_decrypt.getObject(cipher);

        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        return decryptedKey;
    }//decryptKey

    //ΓΙΑ ΤΟ SIGN UP KAI TO LOGIN
    //ΔΗΜΙΟΥΡΓΙΑ ΚΡΥΠΤΟΓΡΑΦΗΜΕΝΟΥ ΚΩΔΙΚΟΥ ΧΡΗΣΤΗ ΜΕ ΤΗΝ ΠΡΟΣΘΗΚΗ ΤΗΣ ΤΥΧΑΙΑΣ ΜΕΤΑΒΛΗΤΗΣ Salt. 
    public byte[] Hash_Password(String password, String salt) throws NoSuchAlgorithmException {

        //Δημιουργία σύνοψης κωδικού του χρήστη
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        digest.update(salt.getBytes());
        byte[] bytes = digest.digest(password.getBytes());

        return bytes;
    }//hashPassword
    
    public byte[] Hash_File(String file_name) {

        //Δημιουργία σύνοψης του αρχείου fileName
        //Χρησιμοποιείται στον μηχανισμό ακεραιότητας κατα τον υπολογισμό
        //των ψηφιακών υπογραφών των αρχείων
        if (!new File(file_name).exists()) {
            return null;
        }//if

        FileInputStream is;
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            is = new FileInputStream(file_name);

            byte[] bytesBuffer = new byte[1024];
            int bytesRead = -1;

            while ((bytesRead = is.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }//while

        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        byte[] bytes = digest.digest();

        return bytes;
    }//hashFile 
    
    public User Get_User(String loginName) {

        //Επιστρέφει το αντικείμενο της κλάσης User όπου το 
        //όνομα είναι ίδιο με loginName. Χρησιμοποιείται από την authenticateUser
        //για την αυθεντικοποιήση του χρήστη
        ArrayList<User> userList = Get_Users_List_From_File();
        for (int i = 0; i < userList.size(); i++) {

            if ((userList.get(i).getLoginName()).equals(loginName)) {
                return userList.get(i);
            }//if
        }//for

        return null;
    }//getUser
    
    //ΣΥΝΑΡΤΗΣΗ ΠΟΥ ΦΑΙΡΝΕΙ ΤΑ ΣΤΟΙΧΕΙΑ ΤΟΥ ΧΡΗΣΗΤ ΑΠΟ ΤΟ ΑΡΧΕΙΟ ΧΡΗΣΤΩΝ.
    public ArrayList<User> Get_Users_List_From_File() {

        //Επιστρέφει μία λίστα με όλους τους χρήστες που έχουν κάνει εγγραφή
        //Χρησιμοποιείται απο την getUser για να βρει αν υπάρχει ο χρήστης με
        //συγκεκριμένο loginName
        ObjectInputStream in;
        User user;
        ArrayList<User> usersFromFile = new ArrayList();

        try {
            in = new ObjectInputStream(new FileInputStream("Users.dat"));

            while (true) {

                try {
                    user = (User) in.readObject();
                } catch (EOFException exep) {
                    break;
                }//try-catch

                usersFromFile.add(user);

            }//while
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        return usersFromFile;
    }//getUsersListFromFile
    
    
    //******************************************************************
    //*********** ΔΙΑΧΕΙΡΗΣΗ ΔΕΔΟΜΕΝΩΝ ΤΟΥ ΧΡΗΣΤΗ***********************
    //******************************************************************
    
    //ΔΗΜΙΟΥΡΓΙΑ ΟΛΩΝ ΤΩΝ ΠΛΗΡΟΦΟΡΙΩΝ ΠΟΥ ΑΠΙΤΟΥΝ ΓΙΑ ΤΗΝ ΑΠΟΘΗΚΕΥΣΗ ΕΝΟΣ ΧΡΗΣΤΗ.
    public boolean Create_User_Data(User new_user, String password) {

        //Δημιουργεί όλα τα απαραίτητα δεδομένα για κάθε χρήστη κάθε φορά
        //που γίνεται εγγραφή( κρυπτογράφηση κωδικού, δημιουργία directory,
        //δημιουργία συμμετρικού ) και αποθήκευση αυτών στο αρχείο Users
        try {
            Encrypt_User_Password(new_user, password);
            Create_User_Folder(new_user);
            Create_Symmetric_Key(new_user);

            Append_to_File("Users.dat", new_user);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        return true;
    }//createUserData

    //ΔΗΜΙΟΥΡΓΙΑ ΑΡΧΕΙΟΥ ΓΙΑ ΤΟΥΣ ΧΡΗΣΤΕΣ.
    public void Create_User_Folder(User user) {

        File userFolder = new File("UsersFolders/" + user.getLoginName());
        userFolder.mkdirs();
    }//createUserFolder

    //ΔΗΜΙΟΥΡΓΙΑ ΣΥΜΜΕΤΡΙΚΟΥ ΚΛΕΙΔΙΟΥ ΜΑΖΙ ΜΕ ΤΟ ΔΗΜΟΣΙΟ ΚΛΕΙΔΙ
    public void Create_Symmetric_Key(User user) throws NoSuchAlgorithmException {

        //Δημιουργία συμμετρικού κλειδιού
        KeyGenerator key_gen = KeyGenerator.getInstance("AES");
        key_gen.init(128);
        SecretKey secretKey = key_gen.generateKey();

        //Διαβάζει το δημόσιο κλειδί από το αρχείο
        PublicKey publicKey = (PublicKey) readFromFile("publicKey");
        //Κρυπτογράφηση συμμετρικού κλειδιού με το δημόσιο κλειδί
        SealedObject encSecretKey = Encrypt_Key(publicKey, secretKey.getEncoded(), "RSA");
        //Αποθήκευση του συμμετρικού κλειδιού στον φάκελο του χρήστη       
        Write_to_File("UsersFolders/" + user.getLoginName() + "/aesKey", encSecretKey);

    }//createSymmetricKey

    //ΚΡΥΠΤΟΓΡΑΦΗΣΗ ΚΛΕΙΔΙΟΥ
    public SealedObject Encrypt_Key(Key encryptionKey, byte[] keyToEncrypt, String algorithm) {

        //Συνάρτηση για την κρυπτογράφηση κλειδιού
        Cipher cipher;
        SealedObject encryptedKey = null;

        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            encryptedKey = new SealedObject(keyToEncrypt, cipher);

        } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        return encryptedKey;

    }//encryptKey
    
    
    //*********************************************************************
    //********************ΔΙΑΧΕΙΡΗΣΗ ΣΥΝΑΛΛΑΓΩΝ****************************
    //*********************************************************************
    
    
    //ΑΠΟΘΗΚΕΥΣΗ ΣΥΝΑΛΛΑΓΗΣ ΑΦΟΥ ΓΙΝΕΙ Η ΚΡΥΠΤΟΓΡΑΦΗΣΗ ΤΟΥΣ ΣΕ ΚΑΤΑΛΛΗΛΟ ΑΡΧΕΙΟ
    public void Save_New_Transaction(Transaction new_transaction, User logged_user, int transaction_type) {

        //Αποθηκεύεται η νέα συναλλαγή, αφού κρυπτογραφηθεί σε κατάλληλο αρχείο, 
        //ανάλογα με τον τύπο της συναλλαγής. 
        int INCOME = 1;
        int OUTCOME = 0;

        //Δίνεται μοναδικό ID σε κάθε νέα συναλλαγή 
        new_transaction.setId(Get_Last_ID(logged_user));

        SecretKey secretKey = Get_Secret_Key(logged_user);
        SealedObject encryptedData = Encrypt_Data(secretKey, new_transaction, "AES");

        if (transaction_type == INCOME) {
            Append_to_File("UsersFolders/" + logged_user.getLoginName() + "/income.dat", encryptedData);
        } else if (transaction_type == OUTCOME) {
            Append_to_File("UsersFolders/" + logged_user.getLoginName() + "/outcome.dat", encryptedData);
        }//else if

    }//saveNewTransaction

    //ΑΠΟΘΗΚΕΥΣΗ ΤΩΝ ΑΛΛΑΓΩΝ ΠΟΥ ΕΓΙΝΑΝ ΑΠΟ ΤΟΝ ΧΡΗΣΤΗ ΤΥΠΟΥ TRANSACTION.
    public void Save_Changed_Transaction(Transaction changedTrans, User logged_user) {

        //Η συνάρτηση αυτή αποθηκεύει τις αλλαγές που έκανε ο χρήστης σε μια συναλλαγή
        //Διαβάζουμε απο το αρχείο το συμμετρικό κλειδί για να αποκρυπτογραφήσουμε τα δεδομένα
        SecretKey secretKey = Get_Secret_Key(logged_user);
        SealedObject encryptedData;
        String fileName = null;

        //Ανάλογα με το τύπο της συναλλαγής, χρησιμοποιούμε το κατάλληλο αρχείο
        if (changedTrans.getType() == Graphics.INCOME) {
            fileName = "UsersFolders/" + logged_user.getLoginName() + "/income.dat";
        } else if (changedTrans.getType() == Graphics.OUTCOME) {
            fileName = "UsersFolders/" + logged_user.getLoginName() + "/outcome.dat";
        }//else if
        
        
        
        
        //Απο το αρχείο που ορίσαμε παραπάνω παίρνουμε την λίστα με όλες τις συναλλαγές
        //συγκεκριμένου τύπου (δηλαδή έσοδα ή έξοδα).
        ArrayList<Transaction> tr = Get_Transactions_From_File(fileName, secretKey);

        //Ψάχνουμε σε αυτήν την λίστα την συναλλαγή με το ID της τροποποιημένης 
        //συναλλαγής για να αποθηκεύσουμε τις αλλαγές
        for (int i = 0; i < tr.size(); i++) {
            if (tr.get(i).getId() == changedTrans.getId()) {
                tr.set(i, changedTrans);
            }//if

            //Κρυπτογραφούμε τα δεδομένα 
            encryptedData = Encrypt_Data(secretKey, tr.get(i), "AES");

            //Καλούμε την writeToFile την πρώτη φορά για να σβηστούν τα περιεχόμενα 
            //του αρχείου ώστε να βάλουμε τα νέα
            if (i == 0) {
                Write_to_File(fileName, encryptedData);
            } else {
                Append_to_File(fileName, encryptedData);
            }
        }//for   

    }//saveChangedTransaction
    
    
    //ΣΥΝΑΡΤΗΣΗ ΠΟΥ ΑΠΟΚΡΥΠΤΟΓΡΑΦΟΥΜΕ ΤΟ ΣΥΜΜΕΤΡΙΚΟ ΜΑΖΙ ΜΕ ΤΟ ΙΔΙΩΤΙΚΟ ΚΛΕΙΔΙ
    public SecretKey Get_Secret_Key(User loggedInUser) {

        //Παίρνουμε το ιδιωτικό κλειδί από το αρχείο των κλειδιών.
        PrivateKey privateKey = (PrivateKey) readFromFile("privateKey");

        //Παίρνουμε το συμμετρικό κλειδί.
        SealedObject encSecretKey = (SealedObject) readFromFile("UsersFolders/" + loggedInUser.getLoginName() + "/aesKey");

        //Αποκρυπτογραφούμε το συμμετρικό κλειδί με το ιδιωτικό κλειδί
        byte[] encodedKey = Decrypt_Key(privateKey, encSecretKey, "RSA");
        SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        return secretKey;
    }//getSecretKey
    
    //ΚΡΥΠΤΟΓΡΑΦΗΣΗ ΑΝΤΙΚΕΙΜΕΝΟΥ ΤRANSACTION
    public SealedObject Encrypt_Data(Key encryptionKey, Transaction dataToEncrypt, String algorithm) {

        //Συνάρτηση για την κρυπτογράφηση αντικειμένου Transaction
        Cipher cipher;
        SealedObject sealed = null;

        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            sealed = new SealedObject(dataToEncrypt, cipher);

        } catch (NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | NoSuchAlgorithmException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        return sealed;
    }//encryptData

    //ΑΠΟΚΡΥΠΤΟΓΡΑΦΗΣΗ ΑΝΤΙΚΕΙΜΕΝΩΝ ΤΥΠΟΥ TRANSACTION
    public Transaction Decrypt_Data(Key decryptionKey, SealedObject dataToDencrypt, String algorithm) {

        //Συνάρτηση για την αποκρυπτογράφηση αντικειμένου Transaction
        Cipher cipher;
        Transaction decryptedTrans = null;

        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
            decryptedTrans = (Transaction) dataToDencrypt.getObject(cipher);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | ClassNotFoundException | BadPaddingException | IllegalBlockSizeException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        return decryptedTrans;
    }//decryptData

    

    //ΣΥΝΑΡΤΗΣΗ ΠΟΥ ΕΠΙΣΤΡΕΦΕΙ ΜΙΑ ΛΙΣΤΑ ΜΕ ΟΛΕΣ ΤΙΣ ΣΥΝΑΛΛΑΓΕΣ ΑΠΟ ΕΝΑ ΑΡΧΕΙΟ.
    public ArrayList Get_Transactions_From_File(String file, SecretKey secretKey) {

        //Επιστρέφει μία λίστα με τις συναλλαγές που είναι αποθηκευμένες στο 
        //αρχείο με όνομα fileName, το οποίο δίνεται ώς παράμετρο
        ObjectInputStream in;
        Transaction transaction;
        SealedObject sealedObj;
        ArrayList transactionsFromFile = new ArrayList();

        try {
            in = new ObjectInputStream(new FileInputStream(file));

            while (true) {

                try {
                    //Διάβασμα αντικειμένου απο αρχείο
                    sealedObj = (SealedObject) in.readObject();
                    //Αποκρυπτογράφηση του αντικειμένου 
                    transaction = Decrypt_Data(secretKey, sealedObj, "AES");
                } catch (EOFException exep) {
                    break;
                }//try-catch

                //Προσθήκη του αποκρυπτογραφημένου αντικειμένου Transaction στην λίστα 
                transactionsFromFile.add(transaction);

            }//while
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            return transactionsFromFile;
        }//try-catch

        return transactionsFromFile;
    }//getTransactionsFromFile

    //ΣΥΝΑΡΤΗΣΗ ΠΟΥ ΕΠΙΣΤΡΕΦΕΙ ΜΙΑ ΛΙΣΤΑ ΜΕ ΟΛΕΣ ΤΙΣ ΣΥΝΑΛΛΑΓΕΣ ΕΝΟΣ ΧΡΗΣΤΗ
    public ArrayList<Transaction> Get_All_Transactions(User logged_user) {

        //Διαβάζει απο τα αρχεία και επιστρέφει μια λίστα με όλες τις συναλλαγές το χρήστη
        ArrayList<Transaction> transactionsList = new ArrayList();

        //Διαβάζουμε απο το αρχείο το συμμετρικό κλειδί για να αποκρυπτογραφήσουμε τα δεδομένα
        SecretKey secretKey = Get_Secret_Key(logged_user);

        //Αποθηκεύουμε σε λίστες τις συναλλαγές του χρήστη που ειναι συνδεδεμένος
        ArrayList<Transaction> trOutcomes = Get_Transactions_From_File("UsersFolders/" + logged_user.getLoginName() + "/outcome.dat", secretKey);
        ArrayList<Transaction> trIncomes = Get_Transactions_From_File("UsersFolders/" + logged_user.getLoginName() + "/income.dat", secretKey);

        //Αποθηκεύει τις συναλλαγές του αρχείου με τα έξοδα  
        for (int i = 0; i < trOutcomes.size(); i++) {
            transactionsList.add(trOutcomes.get(i));
        }//for

        //Αποθηκεύει τις συναλλαγές του αρχείου με τα έσοδα 
        for (int i = 0; i < trIncomes.size(); i++) {
            transactionsList.add(trIncomes.get(i));
        }//for

        return transactionsList;
    }//getAllTransactions

    //ΣΥΝΑΡΤΗΣΗ ΠΟΥ ΕΠΙΣΤΡΕΦΕΙ ΤΑ ΣΤΟΙΧΕΙΑ ΤΟΥ ΧΡΗΣΤΗ.
    

    

    //ΣΥΝΑΡΤΗΣΗ ΠΟΥ ΔΙΝΕΙ
    public int Get_Last_ID(User loggedInUser) {

        //Η συνάρτηση αυτή χρησιμοποιείται από την saveNewTransaction συνάρτηση 
        //για να δώσει μοναδικό ID στις συναλλαγές του κάθε χρήστη. Επιστρέφει 
        //το τελευταίο ID που δώθηκε και δίνεται στην επόμενη συναλλαγή η τιμή 
        //αυτού αυξημένο κατα ένα
        ArrayList<Transaction> transactions = Get_All_Transactions(loggedInUser);

        int maxID = 0;

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId() > maxID) {
                maxID = transactions.get(i).getId();
            }//if
        }//for

        return maxID;
    }//getLastID

    
    
    
    
    
    //*******************************************************************************
    //******************************ΨΗΦΙΑΚΗ ΥΠΟΓΡΑΦΗ*********************************
    //*******************************************************************************
    
    public int validateSignatures(User loggedInUser) {

        File oldFile;

        if (!(oldFile = new File("UsersFolders/" + loggedInUser.getLoginName() + "/signatures.dat")).exists()) {
            return -1;
        }//if

        //Υπολογίζει τις υπογραφές των αρχείων και τις αποθηκεύει σε νέο αρχείο 
        Compute_File_Signatures(loggedInUser, "signaturesNew.dat");
        File newFile = new File("UsersFolders/" + loggedInUser.getLoginName() + "/signaturesNew.dat");

        /*Συγκρίνουμε τα δύο αρχεία με τις υπογραφές (το παλιό, που αποθηκέυσαμε κατα την 
         *τελευταία αποσύνδεση του χρήστη και το νέο που δημιουργήσαμε προηγουμένως). Αν 
         *τα δύο αρχεία είναι ίδια τότε σημαίνει οτι δεν έχει γίνει τροποποίηση αρχείων 
         *απο μη εξουσιοδοτημένο χρήστη.*/
        boolean compareFiles = true;

        try {
            compareFiles = FileUtils.contentEquals(oldFile, newFile);
            
            System.out.println(compareFiles);
            newFile.delete();
        } catch (IOException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        return compareFiles ? 1 : 0;

    }//validateSignatures

    public void Compute_File_Signatures(User loggedInUser, String fileName) {

        HashMap<String, byte[]> hm = new HashMap<>();
        PrivateKey privateKey = (PrivateKey) readFromFile("privateKey");
        String fileIncome, fileOutcome;

        fileIncome = "UsersFolders/" + loggedInUser.getLoginName() + "/income.dat";
        hm.put(fileIncome, Hash_File(fileIncome));

        fileOutcome = "UsersFolders/" + loggedInUser.getLoginName() + "/outcome.dat";
        hm.put(fileOutcome, Hash_File(fileOutcome));

        byte[] realSig;

        try {
            //Δημιουργείται η υπογραφή και αρχικοποιείται με το ιδιωτικό κλειδί
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);

            //Υπογράφονται οι συνόψεις των αρχείων και αποθηκεύονται σε αρχείο 
            System.out.println("hm.get(fileIncome): "+ hm.get(fileIncome));
            if (hm.get(fileIncome) != null) {
                signature.update(hm.get(fileIncome));
                realSig = signature.sign();
                System.out.println("sign income: "+ realSig);
                Write_to_File("UsersFolders/" + loggedInUser.getLoginName() + "/" + fileName, realSig);
            }//if
            
            
            System.out.println("hm.get(fileOutcome: )"+ hm.get(fileOutcome));
            if (hm.get(fileOutcome) != null) {
                signature.update(hm.get(fileOutcome));
                realSig = signature.sign();
                System.out.println("sign outcome: "+ realSig);
                Append_to_File("UsersFolders/" + loggedInUser.getLoginName() + "/" + fileName, realSig);
            }//if

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

    }//computeFileSignatures

    
    //********EXOYME ΕΠΙΣΗΣ ΚΑΙ ΤΗΝ HASH_FILE***********************
    
    
    //**************************************************************
    //*************ΜΕΘΟΔΟΙ ΓΙΑ ΤΗΝ ΕΓΓΡΑΦΗ ΣΕ ΑΡΧΕΙΟ****************
    //**************************************************************
    
    public void Write_to_File(String file_name, Object data) {

        //Εγγραφή σε αρχείο
        ObjectOutputStream out;

        try {

            out = new ObjectOutputStream(new FileOutputStream(file_name));

            out.writeObject(data);
            out.flush();
            out.close();
        } //try //try
        catch (IOException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//catch//catch

    }//writeToFile

    public void Append_to_File(String file_name, Object data) {

        //Προσθήκη δεδομένων σε αρχείο
        if (!new File(file_name).exists()) {
            Write_to_File(file_name, data);
            return;
        }//if

        ObjectOutputStream out;

        try {

            //http://stackoverflow.com/questions/15607969/appending-objects-to-a-serialization-file
            out = new ObjectOutputStream(new FileOutputStream(file_name, true)) {
                protected void writeStreamHeader() throws IOException {
                    reset();
                }
            };

            out.writeObject(data);
            out.flush();
            out.close();
        } //try //try
        catch (IOException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//catch//catch

    }//appendToFile

    public Object readFromFile(String fileName) {

        //Διάβασμα αντικειμένου απο αρχείο
        ObjectInputStream ois;
        Object tempObj;

        try {
            ois = new ObjectInputStream(new FileInputStream(fileName));
            tempObj = ois.readObject();

            ois.close();
            return tempObj;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(All_Functions.class.getName()).log(Level.SEVERE, null, ex);
        }//try-catch//try-catch

        return null;
    }//readFromFile   
}//SecFunctions
