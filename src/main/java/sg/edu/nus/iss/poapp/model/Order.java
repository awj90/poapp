package sg.edu.nus.iss.poapp.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Order implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<Item> contents = new LinkedList<>();
    private List<String> existingItems = new LinkedList<>();

    public List<Item> getContents() {
        return contents;
    }

    public List<String> getExistingItems() {
        return existingItems;
    }

    public void setContents(List<Item> contents) {
        this.contents = contents;
    }

    public void setExistingItems(List<String> existingItems) {
        this.existingItems = existingItems;
    }

    public void addNewItem(Item item) {
        contents.add(item);
        existingItems.add(item.getItem());
    }

    public void addMoreOfExistingItem(Item item) {
        for (Item i : contents) {
            if (i.getItem().equals(item.getItem())) {
                int existingQuantity = i.getQuantity();
                i.setQuantity(existingQuantity + item.getQuantity());
            }
        }
    }
    
}
