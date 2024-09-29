package de.lgohlke.homebanking.keepass;

import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.purejava.KeepassProxyAccess;

import java.util.List;
import java.util.Map;


public class KeepassProxyAccessTest {
    @Test
    @Disabled("learning test")
    public void helloWorld() {
        var kpa = new KeepassProxyAccess();
        if (kpa.connect()) {
            System.out.println("connected");
        } else {
            System.err.println("connection failed");
        }
//        if (kpa.associate()) {
//            System.out.println("associated");
//        } else {
//            System.err.println("Association failed");
//        }
        // add new ones for each dev cycle
        val assId = "test";
        val keyPair = "BLBfj0SxUJNGE7JVOHBoUrDXf9zSo3ebDMKtBVqQrH8="; // gitleaks:allow

        System.out.println(assId);
        System.out.println(keyPair);
        if (kpa.testAssociate(assId, keyPair)) {
            System.out.println("works");
        }
        System.out.println(kpa.getDatabaseGroups());
        var id_key_map = List.of(Map.of("id", assId, "key", keyPair));
        var logins = kpa.getLogins("https://banking.quirion.de/", "", true, id_key_map);
        System.out.println(logins);
//        kpa.lockDatabase();
    }
}
