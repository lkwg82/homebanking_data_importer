package de.lgohlke.homebanking.institutes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BankingURL {
    DKB("https://banking.dkb.de"),
    QUIRION("https://banking.quirion.de"),
    TRADEREPUBLIC("https://app.traderepublic.com/login");

    private final String url;
}
