    //Νικόλαος Φουρτούνης icsd13195
    //Παύλος Σκούπρας icsd13171
    //Γεώργιος Ζέρβας icsd13055

package secproject;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable{
    
    private String description, amount;
    private Date date;
    private int type, id;
  
   public Transaction(){} 
    
    public Transaction(Date date, String description, String amount, int type){
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = type;
    }//Costructor

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id+1;
    }
         
}//Transaction
