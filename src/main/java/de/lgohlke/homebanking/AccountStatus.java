package de.lgohlke.homebanking;

import lombok.NonNull;
import lombok.SneakyThrows;
import nl.garvelink.iban.IBAN;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public record AccountStatus(@NonNull Date date, IBAN iban, BigDecimal balance, String name) {
    public static AccountStatus parse(@NonNull String iban, @NonNull String balance, @NonNull String name) {
        Date now = Date.valueOf(LocalDate.now());
        return parse(now, iban, balance, name);
    }

    public static AccountStatus parse(@NonNull String date, @NonNull String iban, @NonNull String balance, @NonNull String name) {
        return parse(Date.valueOf(date), iban, balance, name);
    }

    public static AccountStatus parse(@NonNull Date date, @NonNull String iban, @NonNull String balance, String name) {
        return new AccountStatus(date, IBAN.valueOf(iban), parseBalance(balance), name);
    }

    @SneakyThrows
    private static BigDecimal parseBalance(String balanceString) {
        if (balanceString.isBlank()) {
            throw new IllegalArgumentException("just blank balance str: '" + balanceString + "'");
        }

        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        Number number = format.parse(balanceString.replace(" €", ""));
        return new BigDecimal(number.toString()).setScale(2, RoundingMode.HALF_UP);
    }

    public String balanceAsStr() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
        symbols.setDecimalSeparator(',');

        DecimalFormat df = new DecimalFormat("0.00", symbols);

        return df.format(balance);
    }
}
