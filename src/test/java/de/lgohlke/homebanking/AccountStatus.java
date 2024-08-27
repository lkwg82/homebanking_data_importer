package de.lgohlke.homebanking;

import nl.garvelink.iban.IBAN;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

record AccountStatus(Date date, IBAN iban, BigDecimal balance, String name) {
    public static AccountStatus parse(String iban, String balance, String name) throws ParseException {

        Date now = Date.valueOf(LocalDate.now());
        return parse(now, iban, balance, name);
    }

    public static AccountStatus parse(Date date, String iban, String balance, String name) throws ParseException {
        return new AccountStatus(date, IBAN.valueOf(iban), parseBalance(balance), name);
    }

    private static BigDecimal parseBalance(String balanceString) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        Number number = format.parse(balanceString.replace(" €", ""));
        return new BigDecimal(number.toString());
    }
}
