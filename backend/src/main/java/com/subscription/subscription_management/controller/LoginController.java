
package com.subscription.subscription_management;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            String role,
            String email,
            String password,
            Model model) {

        model.addAttribute("errorMessage",
                "Login authentication is not connected yet. We will connect it to MySQL next.");

        return "login";
    }
}
