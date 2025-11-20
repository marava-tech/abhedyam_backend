package com.abhedyam.util;

import java.util.List;

public final class PackageConstants {
    
    public static final String BUSINESS_APP_PACKAGE = "tech.marava.abhedyam";
    public static final String CUSTOMER_APP_PACKAGE = "tech.marava.abhedyamc";
    
    public static final List<String> ALL_PACKAGES = List.of(BUSINESS_APP_PACKAGE, CUSTOMER_APP_PACKAGE);
    
    private PackageConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}

