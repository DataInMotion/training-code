package biz.datainmotion.training.authservice;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class PartnerAuthenticatorTest {

    private final PartnerAuthenticator authenticator =
            new PartnerAuthenticator("key".getBytes(StandardCharsets.US_ASCII));

    @Test
    void acceptsCorrectWebhookSignature() throws Exception {
        // HMAC-SHA256("key", "The quick brown fox jumps over the lazy dog")
        assertTrue(authenticator.verifyWebhook("The quick brown fox jumps over the lazy dog",
                "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"));
    }

    @Test
    void rejectsWrongOrMalformedSignature() throws Exception {
        assertFalse(authenticator.verifyWebhook("anderer Inhalt",
                "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"));
        assertFalse(authenticator.verifyWebhook("anderer Inhalt", "keine-hex-zahl"));
    }
}
