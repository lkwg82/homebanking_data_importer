package de.lgohlke.homebanking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BankingURL {
    DKB("https://banking.dkb.de");

    private final String url;
}
