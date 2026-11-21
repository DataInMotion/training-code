package biz.datainmotion.training.authservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import biz.datainmotion.training.authservice.TokenVerifier.Claims;

class TokenVerifierTest {

    private static final byte[] SECRET = "test-only-secret-0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
    private static final Instant NOW = Instant.parse("2026-11-01T10:00:00Z");

    private final TokenVerifier verifier = new TokenVerifier(SECRET, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void acceptsItsOwnToken() throws Exception {
        String token = verifier.issue(42, "user", Duration.ofMinutes(15));

        Claims claims = verifier.verify(token).orElseThrow();

        assertEquals(42, claims.accountId());
        assertEquals("user", claims.role());
    }

    @Test
    void rejectsChangedPayload() throws Exception {
        String[] parts = verifier.issue(42, "user", Duration.ofMinutes(15)).split("\\.");
        String forged = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":42,\"role\":\"admin\",\"exp\":9999999999}".getBytes(StandardCharsets.UTF_8));

        assertTrue(verifier.verify(parts[0] + "." + forged + "." + parts[2]).isEmpty());
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        String token = verifier.issue(42, "user", Duration.ofMinutes(15));
        TokenVerifier later = new TokenVerifier(SECRET, Clock.fixed(NOW.plus(Duration.ofHours(1)), ZoneOffset.UTC));

        assertTrue(later.verify(token).isEmpty());
    }

    @Test
    void rejectsOtherAlgorithm() {
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":42,\"role\":\"admin\",\"exp\":9999999999}".getBytes(StandardCharsets.UTF_8));

        assertTrue(verifier.verify(header + "." + payload + ".").isEmpty());
    }

    @Test
    void rejectsGarbage() {
        assertTrue(verifier.verify("kein.gültiges.token").isEmpty());
        assertTrue(verifier.verify(null).isEmpty());
    }

    @Test
    void refusesShortSecret() {
        assertThrows(IllegalArgumentException.class,
                () -> new TokenVerifier(new byte[8], Clock.systemUTC()));
    }
}
