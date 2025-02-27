package com.coyotesong.database.containers;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;

import java.time.Duration;

public class DurationWaitStrategy implements WaitStrategy {
    private Duration duration = Duration.ZERO;

    @Override
    public void waitUntilReady(WaitStrategyTarget waitStrategyTarget) {
        if (!duration.isZero()) {
            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public WaitStrategy withStartupTimeout(@NotNull final Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration is negative!");
        }

        this.duration = duration;
        return this;
    }
}
