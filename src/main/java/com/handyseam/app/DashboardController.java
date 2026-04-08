package com.handyseam.app;


import com.handyseam.app.customer.CustomerRepository;

import com.handyseam.app.order.ShopOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired private ShopOrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;

   

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

       
        Long pendingCount = orderRepository.countOrdersPending();
        Double totalRevenue = orderRepository.sumTotalRevenue();
        long customerCount = customerRepository.count();

       
        long itemCount = 0; 

        
        if (totalRevenue == null) totalRevenue = 0.0;
        if (pendingCount == null) pendingCount = 0L;

        
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("customerCount", customerCount);
        model.addAttribute("itemCount", itemCount);

       
        model.addAttribute("recentOrders", orderRepository.findTop5ByOrderByOrderDateDesc());

        return "dashboard";
    }
}
