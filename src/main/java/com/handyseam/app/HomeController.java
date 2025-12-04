package com.handyseam.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Only handle the root URL "/"
    @GetMapping("/")
    public String home() {
        // Redirect users to the new Dashboard controller automatically
        return "redirect:/dashboard";
    }

    // REMOVED: The @GetMapping("/dashboard") method
    // because DashboardController.java handles it now.
}