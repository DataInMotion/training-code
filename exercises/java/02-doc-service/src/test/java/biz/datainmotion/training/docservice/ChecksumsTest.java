package biz.datainmotion.training.docservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ChecksumsTest {

    @Test
    void sha256OfKnownInput() throws Exception {
        byte[] abc = "abc".getBytes(StandardCharsets.US_ASCII);

        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                Checksums.sha256(abc));
    }

    @Test
    void cacheKeyIsStable() {
        byte[] data = "Vorschau".getBytes(StandardCharsets.UTF_8);

        assertEquals(Checksums.cacheKey(data), Checksums.cacheKey(data.clone()));
    }
}
