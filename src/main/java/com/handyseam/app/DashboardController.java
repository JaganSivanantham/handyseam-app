package com.handyseam.app;


import com.handyseam.app.customer.CustomerRepository;
// import com.handyseam.app.item.ItemRepository; <--- Removed this because we skipped Inventory
import com.handyseam.app.order.ShopOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired private ShopOrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;

    // @Autowired private ItemRepository itemRepository; <--- Removed this

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        // 1. Fetch the counts
        Long pendingCount = orderRepository.countOrdersPending();
        Double totalRevenue = orderRepository.sumTotalRevenue();
        long customerCount = customerRepository.count();

        // long itemCount = itemRepository.count(); <--- Removed this
        long itemCount = 0; // We hardcode 0 for now since Inventory is on hold

        // Handle case where revenue is null (no orders yet)
        if (totalRevenue == null) totalRevenue = 0.0;
        if (pendingCount == null) pendingCount = 0L;

        // 2. Add to model
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("customerCount", customerCount);
        model.addAttribute("itemCount", itemCount);

        // 3. Fetch Recent Orders Table
        model.addAttribute("recentOrders", orderRepository.findTop5ByOrderByOrderDateDesc());

        return "dashboard";
    }
}