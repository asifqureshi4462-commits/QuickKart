package com.quickkart.app.utils;

import java.text.DecimalFormat;

public class CurrencyUtils {
    private static final DecimalFormat FORMAT = new DecimalFormat("#,##,##0");
    private static final DecimalFormat FORMAT_DECIMAL = new DecimalFormat("#,##,##0.00");

    public static String format(double amount) {
        return "\u20B9" + FORMAT.format(amount);
    }

    public static String formatDecimal(double amount) {
        return "\u20B9" + FORMAT_DECIMAL.format(amount);
    }
}
