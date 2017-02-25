    //Νικόλαος Φουρτούνης icsd13195
    //Παύλος Σκούπρας icsd13171
    //Γεώργιος Ζέρβας icsd13055

package secproject;

import java.io.Serializable;
import javax.crypto.SealedObject;


public class User implements Serializable {
    
    private String name, surname, loginName, salt;
    private SealedObject encryptedPass;
    
    public User(){}
    
    public User( String name, String surname, String loginName ){
        
        this.name = name; 
        this.surname = surname;
        this.loginName = loginName;       
    }//Constructor

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public SealedObject getEncryptedPass() {
        return encryptedPass;
    }

    public void setEncryptedPass(SealedObject encryptedPass) {
        this.encryptedPass = encryptedPass;
    }

}//User
