package com.team.wearly.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123!";
        String hash = encoder.encode(password);
        System.out.println("========================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("========================================");
        System.out.println("SQL INSERT 문:");
        System.out.println("'" + hash + "'");
        System.out.println("========================================");
    }
}
