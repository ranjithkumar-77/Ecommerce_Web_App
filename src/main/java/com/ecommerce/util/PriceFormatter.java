package com.ecommerce.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PriceFormatter {

    private PriceFormatter() {
    }

    public static String amount(Object value) {
        if (value == null) {
            return "";
        }

        BigDecimal amount = value instanceof BigDecimal decimal
                ? decimal
                : new BigDecimal(value.toString().trim());
        return amount.setScale(0, RoundingMode.HALF_UP).toPlainString();
    }
}
