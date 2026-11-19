
package com.subscription.subscription_management;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

public class PasswordHashGenerator {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.print("Enter a password to hash: ");
        String password = scanner.nextLine();

        if (password.isBlank()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        String hash = encoder.encode(password);

        System.out.println("\nBCrypt password hash:");
        System.out.println(hash);

        scanner.close();
    }
}
