package de.lgohlke.homebanking.sops;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SopsDecryptorTest {
    @Test
    @Disabled("needs missing file")
    void testSopsDecryptor() {
        var sopsDecryptor = new SopsDecryptor("keepass.enc.yaml");
        var content = sopsDecryptor.getContent();
        System.out.println(content);
    }

    @Test
    void testFileNotFound() {
        String nonExistentFilePath = "nonexistent_file.yaml";
        SopsDecryptor sopsDecryptor = new SopsDecryptor(nonExistentFilePath);

        assertThatThrownBy(sopsDecryptor::getContent).isInstanceOf(FileNotFoundException.class)
                                                     .hasMessageContaining("missing file:");
    }

    @Test
    @DisplayName("fail: no sops file")
    void testFileNoSopsFile() throws IOException {
        File tempFile = File.createTempFile("tempFile", ".yaml");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("sample: data");
        }

        SopsDecryptor sopsDecryptor = new SopsDecryptor(tempFile.getAbsolutePath());

        assertThatThrownBy(sopsDecryptor::getContent).isInstanceOf(IllegalStateException.class)
                                                     .hasMessageContaining("sops Kommando ist fehlgeschlagen.");
    }


    @Test
    @DisplayName("fail: wrong key")
    void shouldFailWrongKey() throws IOException {
        var content = """
                id: ENC[AES256_GCM,data:BidX80oJTyUG9deF4pzAaCn9FbrkXB3RG1ZhLlw=,iv:KlLDJwq+a7LaV5MPX00nJ+wxyy4wJgRywq6kx+zhcKY=,tag:wBs8rJ7Z0lokq3kcuuTrcA==,type:str]
                key: ENC[AES256_GCM,data:iZXohM1Sp4QIhExehhtgFK3it7d+wjJ8OjkWxZKRuwISWHnAf1a3ixkKIJY=,iv:2ycooet6AqUilMRsFtYy+cJCYYX0S2QGCuZiQhmPolk=,tag:b+E44dCiHrcmHWB0GM7nJg==,type:str]
                sops:
                    kms: []
                    gcp_kms: []
                    azure_kv: []
                    hc_vault: []
                    age: []
                    lastmodified: "2024-08-10T10:13:00Z"
                    mac: ENC[AES256_GCM,data:NgicLFPPj96WkXtMl5Gv3snecPuuuu+xMOqAvZNc3/6MJjgu9J5hOXgT5I9SBwz9is368y1v3goVJlv6lomvci2dlkzmHypYOnH9pT3m6gBo/D8rFyMCOG2OtUTyNuqTht3y81MsB+wrxSSaz5CX2Js1bnEnM3TKbrXEPI17BA=,iv:SebGopfImFN4zkftrhfbD086VcayJ8E38IhtjMjHZDk=,tag:t7MBUkZR9Ga/0LVSmZEGmg==,type:str]
                    pgp:
                        - created_at: "2022-04-06T04:51:08Z"
                          enc: |
                            -----BEGIN PGP MESSAGE-----
                
                            hf4dwcgkwuhbqcisaqdaydqqmuivozy7dxjbe3usz5fhaiqksevis+wz4fcfuykw
                            uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu
                            1gybcqiqw0jvdc8vjyekphde/f+qbe1ej7fu8pcr1dqohmeuobd9qlsybfysfh7i
                            yleqkra10je03lrjr18vmi4nocydjpnxxvsasilmc5x6frln/nuczroufdzh2ng4
                            k3hxfuabt+i=
                            =9RWR
                            -----END PGP MESSAGE-----
                          fp: 3B433B4D6495A9C2930B7BF3B4B17C5344A614E1
                    unencrypted_suffix: _unencrypted
                    version: 3.9.0
                """;
        File tempFile = File.createTempFile("tempFile2", ".yaml");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        SopsDecryptor sopsDecryptor = new SopsDecryptor(tempFile.getAbsolutePath());

        assertThatThrownBy(sopsDecryptor::getContent).isInstanceOf(IllegalStateException.class)
                                                     .hasMessageContaining("sops Kommando ist fehlgeschlagen.");
        assertThat(sopsDecryptor.getError()).contains("could not decrypt data key with PGP key");
    }
}

