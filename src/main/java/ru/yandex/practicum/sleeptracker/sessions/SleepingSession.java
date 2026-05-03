package ru.yandex.practicum.sleeptracker.sessions;

import ru.yandex.practicum.sleeptracker.enums.QualitySleepTypes;

import java.time.LocalDateTime;
import java.time.Duration;

public class SleepingSession {
    private final LocalDateTime sleepStart;
    private final LocalDateTime sleepEnd;
    private final QualitySleepTypes quality;

    public SleepingSession(LocalDateTime sleepStart, LocalDateTime sleepEnd, QualitySleepTypes quality) {
        if (sleepStart == null || sleepEnd == null || quality == null) {
            throw new IllegalArgumentException("Ошибка: поля не могут быть пустыми (null)");
        }

        if (sleepEnd.isBefore(sleepStart)) {
            throw new IllegalArgumentException("Ошибка: начало сна не может быть позже его конца");
        }

        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
        this.quality = quality;
    }

    public LocalDateTime getSleepStart() {
        return sleepStart;
    }

    public LocalDateTime getSleepEnd() {
        return sleepEnd;
    }

    public QualitySleepTypes getQuality() {
        return quality;
    }

    public long getDurationInMinutes() {
        return Duration.between(sleepStart, sleepEnd).toMinutes();
    }

    @Override
    public String toString() {
        return String.format("Sleep: %s -> %s (%s)", sleepStart, sleepEnd, quality);
    }
}