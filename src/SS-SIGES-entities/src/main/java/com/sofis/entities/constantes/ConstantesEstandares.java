package com.sofis.entities.constantes;

import java.util.TimeZone;

/**
 *
 * @author Usuario
 */
public class ConstantesEstandares {
    
   public static final String SEPARADOR="<BR/>";
    
    public static final String CODIGO_RUTA="RUTA";
    public static final String CODIGO_UY2="UY";
    public static final String CODIGO_CI="DO";
    
    public static final String CODIGO_PENDIENTE="PEN";
    public static final String CODIGO_PENDIENTE_CON_ERRORES="PDC";
    public static final String CODIGO_APROBADO ="APR";
    
//    public static final String LOGGER= "com.sofis";
    
    public static final String CON_CORREO="CON_CORREO";
    public static final String MAIL_FROM="MAIL_FROM";
    public static final String MAIL_ENCODING="MAIL_FROM";
    
    public static final String CALENDAR_PATTERN = "dd/MM/yyyy";
    
    /*
    *   15-06-2018  ---> Se cambia el valor de CALENDAR_TIME_ZONE para que tome el "TimeZone" desde el sistema.
    */
    
    public static final String CALENDAR_TIME_ZONE = TimeZone.getDefault().getID();

    public static final String CURRENT_LOCALE = "es_ES";
    public static final String CURRENT_LOCALE_LANGUAGE = "es";
    public static final String CURRENT_LOCALE_COUNTRY = "ES";
    
    public static final String NUMBER_FORMAT_PATTERN = "###,###.##";
    public static final String IMPORTE_FORMAT_PATTERN = "#,###,###,##0";
    public static final String DECIMAL_FORMAT_PATTERN = "#,###,###,##0.00";
    public static final int NUMBER_DECIMAL_PLACES = 2;
    
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$;";
    
    public static final String SEMAFORO_VERDE = "#80c41c";
    public static final String SEMAFORO_AMARILLO = "#fdc600";
    public static final String SEMAFORO_AMARILLO_MAS = "#fdc601";
    public static final String SEMAFORO_AMARILLO_MENOS = "#fdc602";
    public static final String SEMAFORO_ROJO = "#ff0000";
    public static final String SEMAFORO_NARANJA = "#FE9A2E";
    public static final String SEMAFORO_AZUL = "#035e9f";
    public static final String SEMAFORO_GRIS = "#bababa";
    public static final String COLOR_TRANSPARENT = "transparent";
    public static final String COLOR_NEGRO = "#000000";
}
