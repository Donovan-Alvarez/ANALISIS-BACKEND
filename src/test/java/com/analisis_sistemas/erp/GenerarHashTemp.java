package com.analisis_sistemas.erp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHashTemp {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("erpwijoeli -> " + encoder.encode("erpwijoeli"));
        System.out.println("ITAdmin -> " + encoder.encode("ITAdmin"));
    }
}
