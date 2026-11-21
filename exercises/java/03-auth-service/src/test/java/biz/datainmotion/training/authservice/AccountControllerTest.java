package biz.datainmotion.training.authservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class AccountControllerTest {

    private static final byte[] SECRET = "test-only-secret-0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

    private final TokenVerifier tokens = new TokenVerifier(SECRET, Clock.systemUTC());
    private final AccountController controller = new AccountController(new InMemoryAccounts(), tokens);

    @Test
    void returnsTheAccountOfTheTokenHolder() throws Exception {
        String token = tokens.issue(1, "user", Duration.ofMinutes(5));

        Account account = controller.ownAccount("Bearer " + token);

        assertEquals("Ada Beispiel", account.displayName());
    }

    @Test
    void rejectsRequestWithoutValidToken() {
        assertThrows(ResponseStatusException.class, () -> controller.ownAccount("Bearer abc"));
        assertThrows(ResponseStatusException.class, () -> controller.ownAccount(null));
    }

    @Test
    void publicProfileHidesPrivateFields() {
        assertEquals(new Account.PublicProfile(2, "Ben Muster"), controller.profile(2));
    }

    private static final class InMemoryAccounts implements AccountRepository {

        private final Map<Long, Account> accounts = new HashMap<>(Map.of(
                1L, new Account(1, "Ada Beispiel", "ada@example.org", "DE00 0000 0000 0000 0000 01"),
                2L, new Account(2, "Ben Muster", "ben@example.org", "DE00 0000 0000 0000 0000 02")));

        @Override
        public Optional<Account> findById(long id) {
            return Optional.ofNullable(accounts.get(id));
        }

        @Override
        public void close(long id) {
            accounts.remove(id);
        }
    }
}
