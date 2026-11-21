package biz.datainmotion.training.userapi;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.datainmotion.training.userapi.MailGateway.MailException;

/** Schickt neuen Benutzern den Begrüßungstext, mit Wiederholung bei vorübergehenden Fehlern. */
public class WelcomeMailer {

    private static final Logger log = LoggerFactory.getLogger(WelcomeMailer.class);

    private final MailGateway gateway;
    private final GreetingTemplate greeting;
    private final RetryPolicy retry;

    public WelcomeMailer(MailGateway gateway, GreetingTemplate greeting, RetryPolicy retry) {
        this.gateway = gateway;
        this.greeting = greeting;
        this.retry = retry;
    }

    public boolean welcome(UserView user) throws InterruptedException {
        String text = greeting.render(user);
        for (int attempt = 1; ; attempt++) {
            try {
                gateway.send(user.email(), text);
                return true;
            } catch (MailException e) {
                log.warn("Begrüßung für Benutzer {} nicht zugestellt (Versuch {})", user.id(), attempt);
                if (!retry.shouldRetry(attempt)) {
                    return false;
                }
                Duration pause = retry.delayBefore(attempt);
                Thread.sleep(pause.toMillis());
            }
        }
    }
}
