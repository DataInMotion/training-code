package biz.datainmotion.training.userapi;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/** Wartezeiten zwischen Wiederholungen: exponentiell, mit zufälligem Anteil. */
public final class RetryPolicy {

    private static final int MAX_SHIFT = 10;

    private final int maxAttempts;
    private final Duration baseDelay;

    public RetryPolicy(int maxAttempts, Duration baseDelay) {
        if (maxAttempts < 1 || baseDelay.isNegative()) {
            throw new IllegalArgumentException("maxAttempts >= 1, baseDelay >= 0");
        }
        this.maxAttempts = maxAttempts;
        this.baseDelay = baseDelay;
    }

    public boolean shouldRetry(int attempt) {
        return attempt < maxAttempts;
    }

    /** Liefert eine Wartezeit zwischen der halben und der vollen exponentiellen Stufe. */
    public Duration delayBefore(int attempt) {
        long step = baseDelay.toMillis() << Math.min(Math.max(attempt, 0), MAX_SHIFT);
        long half = step / 2;
        long jitter = ThreadLocalRandom.current().nextLong(half + 1);
        return Duration.ofMillis(half + jitter);
    }
}
