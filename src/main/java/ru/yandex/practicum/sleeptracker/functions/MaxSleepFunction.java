package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.reports.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class MaxSleepFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final String DESCRIPTION = "Максимальная продолжительность сна в минутах";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return Optional.ofNullable(sessions)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    long max = s.stream()
                            .filter(Objects::nonNull)
                            .mapToLong(SleepingSession::getDurationInMinutes)
                            .filter(duration -> duration >= 0)
                            .max()
                            .orElse(0L); // Если в списке были только null или отрицательные

                    return new SleepAnalysisResult(DESCRIPTION, max);
                })
                // Вместо -1 возвращаем красивый статус
                .orElse(new SleepAnalysisResult(DESCRIPTION, "нет данных"));
    }
}
