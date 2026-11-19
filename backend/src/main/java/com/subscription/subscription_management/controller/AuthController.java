
package com.subscription.subscription_management.controller;

import com.subscription.subscription_management.AuthRequest;
import com.subscription.subscription_management.AuthResponse;
import com.subscription.subscription_management.RegisterRequest;
import com.subscription.subscription_management.service.AuthService;
import com.subscription.subscription_management.service.RegisterService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RegisterService registerService;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            AuthService authService,
            RegisterService registerService,
            SecurityContextRepository securityContextRepository) {

        this.authService = authService;
        this.registerService = registerService;
        this.securityContextRepository = securityContextRepository;
    }

    // CUSTOMER REGISTRATION
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {
            String message = registerService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", message));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ADMIN AND CUSTOMER LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        try {
            AuthResponse response = authService.login(request);

            // Remove any previous session.
            HttpSession oldSession = httpRequest.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create a new session.
            HttpSession session = httpRequest.getSession(true);

            session.setAttribute("userId", response.userId());
            session.setAttribute("userRole", response.role());
            session.setAttribute("fullName", response.fullName());

            // Assign the user's authority.
            var authority = new SimpleGrantedAuthority(
                    "ROLE_" + response.role()
            );

            var authentication =
                    new UsernamePasswordAuthenticationToken(
                            response.fullName(),
                            null,
                            List.of(authority)
                    );

            // Create Spring Security context.
            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);

            // Save authentication in the HTTP session.
            securityContextRepository.saveContext(
                    context,
                    httpRequest,
                    httpResponse
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            SecurityContextHolder.clearContext();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request) {

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully")
        );
    }
}
