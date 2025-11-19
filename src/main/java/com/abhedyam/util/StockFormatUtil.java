package com.abhedyam.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StockFormatUtil {
    
    public static Number formatStock(BigDecimal stock) {
        if (stock == null) {
            return 0;
        }
        
        BigDecimal rounded = stock.setScale(1, RoundingMode.HALF_UP);
        BigDecimal stripped = rounded.stripTrailingZeros();
        
        if (stripped.scale() == 0) {
            return stripped.intValue();
        }
        
        return stripped;
    }
    
    public static BigDecimal formatStockAsBigDecimal(BigDecimal stock) {
        if (stock == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal rounded = stock.setScale(1, RoundingMode.HALF_UP);
        BigDecimal stripped = rounded.stripTrailingZeros();
        
        if (stripped.scale() == 0) {
            return BigDecimal.valueOf(stripped.intValue());
        }
        
        return stripped;
    }
}

