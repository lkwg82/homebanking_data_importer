package de.lgohlke.homebanking;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

public class AccountStatusCSVConverter {
    public List<String> convert(Collection<AccountStatus> statuses) {
        return statuses.stream()
                       .map(AccountStatusCSVConverter::getString)
                       .toList();
    }

    public List<String> convert(AccountStatus status) {
        String statusAsCSV = getString(status);
        return List.of(
                "Date|IBAN|Balance|Name",
                statusAsCSV
        );
    }

    public AccountStatus convert(List<String> lines) {
        if (lines.size() != 2) {
            throw new IllegalArgumentException("expects two lines");
        }
        String dataline = lines.get(1);
        String[] parts = dataline.split("\\|");

        if (parts.length != 4) {
            throw new IllegalArgumentException("data line is invalid:'" + dataline + "'");
        }
        Date date = Date.valueOf(parts[0]);
        return AccountStatus.parse(date, parts[1], parts[2], parts[3]);
    }

    private static String getString(AccountStatus status) {
        return String.format("%s|%s|%s|%s",
                             status.date(),
                             status.iban(),
                             status.balanceAsStr(),
                             status.name());
    }
}
