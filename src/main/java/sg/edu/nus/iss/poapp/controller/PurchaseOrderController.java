package sg.edu.nus.iss.poapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.poapp.model.Item;
import sg.edu.nus.iss.poapp.model.Order;
import sg.edu.nus.iss.poapp.model.Quotation;
import sg.edu.nus.iss.poapp.model.ShippingAddress;
import sg.edu.nus.iss.poapp.service.PurchaseOrderService;

@Controller
public class PurchaseOrderController {
    
    @Autowired
    PurchaseOrderService purchaseOrderService;

    @GetMapping(path="/")
    public String renderLandingPage(Model model, HttpSession session) {
    
        // persist order even when re-accessing landing page as long as session is valid
        Order order = (Order) session.getAttribute("order");
        if (order == null) {
            order = new Order();
            session.setAttribute("order", order);
        }
        model.addAttribute("order", order);
        model.addAttribute("item", new Item());
        
        // start from empty cart again whenever accessing landing page
        // model.addAttribute("item", new Item());
        // Order order = new Order();
        // session.setAttribute("order", order);
        // model.addAttribute("order", order);

        return "view1";
    }

    @PostMapping(path="/item")
    public String updateLandingPage(@ModelAttribute @Valid Item item, BindingResult bindingResult, Model model, HttpSession session) {
        
        if (!purchaseOrderService.isValidItem(item)) {
            FieldError err = new FieldError("item", "item", "We do not stock %s".formatted(item.getItem()));
            bindingResult.addError(err);
        }

        if (bindingResult.hasErrors()) {
            Order order = (Order) session.getAttribute("order");
            model.addAttribute("order", order);
            return "view1";
        }

        Order order = (Order) session.getAttribute("order");
        purchaseOrderService.addItemToCart(item, order);
        model.addAttribute("item", new Item());
        model.addAttribute("order", order);
        return "view1";
    }

    @GetMapping(path="/shippingaddress")
    public String renderViewTwo(Model model, HttpSession session) {

        Order order = (Order) session.getAttribute("order");

        if (order == null || order.getContents().isEmpty()) {
            return "redirect:/";
        }

        ShippingAddress shippingAddress = new ShippingAddress();
        session.setAttribute("shippingAddress", shippingAddress);
        model.addAttribute("shippingAddress", shippingAddress);
        return "view2";
    }

    @PostMapping(path="/checkout")
    public String checkOut(@ModelAttribute @Valid ShippingAddress shippingAddress, BindingResult bindingResult, Model model, HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            return "view2";
        }

        Order order = (Order) session.getAttribute("order");
        boolean success = true;

        try {
            Quotation quotation = purchaseOrderService.getQuotations(order.getExistingItems());
            float totalCost = 0f;

            for (Item i : order.getContents()) {
                totalCost += i.getQuantity()*quotation.getQuotations().get(i.getItem());
            }

            model.addAttribute("id", quotation.getQuoteId());
            model.addAttribute("totalCost", totalCost);

        } catch (Exception e) {
            success = false;
            model.addAttribute("errorMessage", e.getMessage());
        }

        model.addAttribute("name", shippingAddress.getName());
        model.addAttribute("shippingAddress", shippingAddress.getAddress());
        model.addAttribute("success", success);
        session.invalidate();
        return "view3";
    }
}