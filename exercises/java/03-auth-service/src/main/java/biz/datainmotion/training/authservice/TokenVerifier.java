package biz.datainmotion.training.authservice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** Stellt Zugriffstoken aus und prüft sie. */
public final class TokenVerifier {

    private static final String ALGORITHM = "HS256";
    private static final int MIN_SECRET_BYTES = 32;

    private final byte[] secret;
    private final Clock clock;
    private final ObjectMapper json = new ObjectMapper();

    public TokenVerifier(byte[] secret, Clock clock) {
        if (secret == null || secret.length < MIN_SECRET_BYTES) {
            throw new IllegalArgumentException("Schlüssel zu kurz");
        }
        this.secret = secret.clone();
        this.clock = clock;
    }

    /** Was ein gültiges Token über seinen Inhaber aussagt. */
    public record Claims(long accountId, String role) {
    }

    public String issue(long accountId, String role, Duration lifetime) throws GeneralSecurityException {
        ObjectNode header = json.createObjectNode().put("alg", ALGORITHM).put("typ", "JWT");
        ObjectNode payload = json.createObjectNode()
                .put("sub", accountId)
                .put("role", role)
                .put("exp", clock.instant().plus(lifetime).getEpochSecond());
        String signed = encode(header.toString()) + "." + encode(payload.toString());
        return signed + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(sign(signed));
    }

    public Optional<Claims> verify(String token) {
        String[] parts = token == null ? new String[0] : token.split("\\.");
        if (parts.length != 3) {
            return Optional.empty();
        }
        try {
            JsonNode header = json.readTree(decode(parts[0]));
            if (!ALGORITHM.equals(header.path("alg").asText())) {
                return Optional.empty();
            }
            byte[] expected = sign(parts[0] + "." + parts[1]);
            if (!MessageDigest.isEqual(expected, decode(parts[2]))) {
                return Optional.empty();
            }
            JsonNode payload = json.readTree(decode(parts[1]));
            if (payload.path("exp").asLong(0) <= clock.instant().getEpochSecond()) {
                return Optional.empty();
            }
            return Optional.of(new Claims(payload.path("sub").asLong(), payload.path("role").asText("user")));
        } catch (IllegalArgumentException | IOException | GeneralSecurityException e) {
            // Ein nicht lesbares Token ist ein ungültiges Token; der Grund geht den Aufrufer nichts an.
            return Optional.empty();
        }
    }

    private byte[] sign(String data) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.US_ASCII));
    }

    private static String encode(String text) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] decode(String part) {
        return Base64.getUrlDecoder().decode(part);
    }
}
