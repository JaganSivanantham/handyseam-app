package com.handyseam.app.customer;

import com.handyseam.app.order.ShopOrder;
import com.handyseam.app.order.ShopOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShopOrderRepository orderRepository; 

    
    @GetMapping("/customers")
    public String viewCustomersPage(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Customer> list;
        if (keyword != null) {
            list = customerService.searchCustomers(keyword);
        } else {
            list = customerService.getAllCustomers();
        }
        model.addAttribute("listCustomers", list);
        model.addAttribute("newCustomer", new Customer());
        model.addAttribute("keyword", keyword);
        return "customers";
    }

   
    @PostMapping("/saveCustomer")
    public String saveCustomer(@ModelAttribute("newCustomer") Customer customer,
                               @RequestParam("photoFile") MultipartFile photoFile) throws IOException {

       
        if (!photoFile.isEmpty()) {
            customer.setPhoto(photoFile.getBytes());
        }

        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    
    @GetMapping("/customer/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getCustomerPhoto(@PathVariable Long id) {
        Customer c = customerRepository.findById(id).orElse(null);
        if (c != null && c.hasPhoto()) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(c.getPhoto());
        }
        return ResponseEntity.notFound().build();
    }

   
    @GetMapping("/customers/{id}")
    public String viewCustomerProfile(@PathVariable Long id, Model model) {
        Customer c = customerRepository.findById(id).orElseThrow();

        
        List<ShopOrder> orders = orderRepository.findByCustomer(c);

       
        double totalPending = 0.0;
        for(ShopOrder o : orders) {
            totalPending += o.getBalanceAmount();
        }

        model.addAttribute("customer", c);
        model.addAttribute("orders", orders);
        model.addAttribute("totalPending", totalPending);

        return "customer_profile";
    }
}
