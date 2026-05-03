package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.reports.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class MinSleepFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final String DESCRIPTION = "Минимальная продолжительность сна в минутах";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return Optional.ofNullable(sessions)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    long min = s.stream()
                            .filter(Objects::nonNull)
                            .mapToLong(SleepingSession::getDurationInMinutes)
                            .filter(duration -> duration >= 0)
                            .min()
                            .orElse(0L);

                    return new SleepAnalysisResult(DESCRIPTION, min);
                })
                .orElse(new SleepAnalysisResult(DESCRIPTION, "нет данных"));
    }
}
