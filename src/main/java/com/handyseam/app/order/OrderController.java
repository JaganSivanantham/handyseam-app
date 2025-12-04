package com.handyseam.app.order;

import com.handyseam.app.customer.Customer;
import com.handyseam.app.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShopOrderRepository shopOrderRepository; // Add this to fetch orders

    // --- 1. THIS FIXES THE WHITELABEL ERROR ON VIEW ORDERS ---
    @GetMapping("")
    public String viewAllOrders(Model model) {
        model.addAttribute("listOrders", shopOrderRepository.findAll());
        return "view_orders"; // We will create this HTML next
    }

    @GetMapping("/create")
    public String showCreateOrderPage() {
        return "order_type_select";
    }

    // --- 2. THIS FIXES SEARCH ON THE SELECT CUSTOMER PAGE ---
    @GetMapping("/create/tailoring/select-customer")
    public String selectCustomerForOrder(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Customer> list;
        if (keyword != null) {
            list = customerService.searchCustomers(keyword);
        } else {
            list = customerService.getAllCustomers();
        }
        model.addAttribute("listCustomers", list);
        model.addAttribute("keyword", keyword);
        return "order_select_customer";
    }
}