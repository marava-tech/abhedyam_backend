package com.abhedyam.util;

import java.util.regex.Pattern;

public class UpiUtil {
    
    private static final Pattern VPA_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+$");
    private static final int MIN_VPA_LENGTH = 3;
    private static final int MAX_VPA_LENGTH = 100;
    
    public static boolean isValidVpaFormat(String vpa) {
        if (vpa == null || vpa.isEmpty()) {
            return false;
        }
        
        vpa = vpa.trim();
        
        if (vpa.length() < MIN_VPA_LENGTH || vpa.length() > MAX_VPA_LENGTH) {
            return false;
        }
        
        if (!vpa.contains("@")) {
            return false;
        }
        
        String[] parts = vpa.split("@");
        if (parts.length != 2) {
            return false;
        }
        
        String username = parts[0];
        String provider = parts[1];
        
        if (username.isEmpty() || provider.isEmpty()) {
            return false;
        }
        
        if (username.startsWith(".") || username.endsWith(".") || 
            username.startsWith("-") || username.endsWith("-") ||
            username.startsWith("_") || username.endsWith("_")) {
            return false;
        }
        
        if (provider.startsWith(".") || provider.endsWith(".") ||
            provider.startsWith("-") || provider.endsWith("-")) {
            return false;
        }
        
        return VPA_PATTERN.matcher(vpa).matches();
    }
}

