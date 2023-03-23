package sg.edu.nus.iss.poapp.model;

import java.io.Serializable;

import jakarta.validation.constraints.Min;

public class Item implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String item;
    
    @Min(value=1, message="You must add at least 1 item")
    private int quantity;

    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
