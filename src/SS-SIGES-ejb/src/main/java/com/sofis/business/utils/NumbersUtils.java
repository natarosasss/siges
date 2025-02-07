package com.sofis.business.utils;

import com.sofis.entities.constantes.ConstantesEstandares;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author Usuario
 */
public class NumbersUtils {

    public static Double redondearDecimales(Double d) {
        if (d != null && !d.isNaN()) {
            double finalValue = Math.round(d * 100.0) / 100.0;
            return finalValue;
        }
        return d;
    }

    public static boolean isNumeric(String cadena) {
        try {
            Double.parseDouble(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static String formatDouble(Double d) {
        if (d != null && !d.isNaN()) {
            DecimalFormat df = new DecimalFormat(ConstantesEstandares.DECIMAL_FORMAT_PATTERN);
            return df.format(d);
        }
        return "";
    }
    
    public static String formatImporte(Double d) {
        if (d != null && !d.isNaN()) {
            DecimalFormat df = new DecimalFormat(ConstantesEstandares.IMPORTE_FORMAT_PATTERN);
            return df.format(d);
        }
        return "";
    }

    public static String formatNumber(Number d) {
        if (d != null) {
            DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
            simbolo.setDecimalSeparator(',');
            simbolo.setGroupingSeparator('.');
            DecimalFormat formatter = new DecimalFormat(ConstantesEstandares.NUMBER_FORMAT_PATTERN, simbolo);
            return formatter.format(d);
        }
        return "";
    }
}