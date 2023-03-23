package sg.edu.nus.iss.poapp.service;

import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.poapp.model.Item;
import sg.edu.nus.iss.poapp.model.Order;
import sg.edu.nus.iss.poapp.model.Quotation;

@Service
public class PurchaseOrderService {
    
    @Value("${qsys.api.url}")
    private String url; // https://quotation.chuklee.com/quotation //

    //apple 0.3, orange 0.7, bread 2.5, cheese 5, chicken 4.7, mineral_water 0.9, instant_noodles 9.8

    private static final List<String> VALID_ITEMS = List.of("apple", "orange", "bread", "cheese", "chicken", "mineral_water", "instant_noodles");
    
    public boolean isValidItem(Item item) {
        return VALID_ITEMS.contains(item.getItem());
    }

    public void addItemToCart(Item item, Order order) {

        if (!order.getExistingItems().contains(item.getItem())) {
            order.addNewItem(item);
        } else {
            order.addMoreOfExistingItem(item);
        }
    }

    public Quotation getQuotations(List<String> items) throws Exception {

        // Set up request entity and exchange for response entity
        RequestEntity<String> req = 
                        RequestEntity.post(url)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .body(Json.createArrayBuilder(items).build().toString());
        
        RestTemplate template = new RestTemplate();

        try {
            ResponseEntity<String> resp = template.exchange(req, String.class); 

            // Marshal the response body into Java object
            Quotation q = new Quotation();
            StringReader sr = new StringReader(resp.getBody());
            JsonReader jsonReader = Json.createReader(sr);
            JsonObject jsonObject = jsonReader.readObject();
            
            q.setQuoteId(jsonObject.getString("quoteId"));
            
            JsonObject quotations = jsonObject.getJsonObject("quotations");
            for (String s : items) {
                JsonNumber jsonNumber = quotations.getJsonNumber(s);
                double unitPrice = jsonNumber.doubleValue();
                q.addQuotation(s, (float) unitPrice);
            }
            return q;

        } catch (Exception e) {
            throw e;
        }

    }
}
