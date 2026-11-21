package biz.datainmotion.training.userapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import biz.datainmotion.training.userapi.MailGateway.MailException;

class WelcomeMailerTest {

    private static final UserView USER = new UserView(7, "Ada", "ada@example.org");
    private static final RetryPolicy THREE_TRIES = new RetryPolicy(3, Duration.ZERO);

    @Test
    void deliversOnFirstAttempt() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        MailGateway gateway = (recipient, text) -> calls.incrementAndGet();

        assertTrue(new WelcomeMailer(gateway, new GreetingTemplate(), THREE_TRIES).welcome(USER));
        assertEquals(1, calls.get());
    }

    @Test
    void retriesAfterTransientFailure() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        MailGateway gateway = (recipient, text) -> {
            if (calls.incrementAndGet() < 3) {
                throw new MailException("vorübergehend nicht erreichbar");
            }
        };

        assertTrue(new WelcomeMailer(gateway, new GreetingTemplate(), THREE_TRIES).welcome(USER));
        assertEquals(3, calls.get());
    }

    @Test
    void givesUpAfterMaxAttempts() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        MailGateway gateway = (recipient, text) -> {
            calls.incrementAndGet();
            throw new MailException("nicht erreichbar");
        };

        assertFalse(new WelcomeMailer(gateway, new GreetingTemplate(), THREE_TRIES).welcome(USER));
        assertEquals(3, calls.get());
    }
}
