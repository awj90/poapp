package sg.edu.nus.iss.poapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ShippingAddress {
    
    @NotBlank(message="Mandatory field")
    @Size(min=2, message="Name must have at least 2 characters")
    private String name;

    @NotBlank(message="Mandatory field")
    private String address;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    
}
