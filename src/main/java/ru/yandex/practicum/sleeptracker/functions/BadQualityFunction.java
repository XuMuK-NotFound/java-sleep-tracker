package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.enums.QualitySleepTypes;
import ru.yandex.practicum.sleeptracker.reports.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class BadQualityFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final String DESCRIPTION = "Количество дней плохого сна";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return Optional.ofNullable(sessions)
                .map(s -> {
                    long badCount = s.stream()
                            .filter(Objects::nonNull)
                            .filter(session -> session.getQuality() == QualitySleepTypes.BAD)
                            .count();

                    return new SleepAnalysisResult(DESCRIPTION, badCount);
                })
                .orElse(new SleepAnalysisResult(DESCRIPTION, 0L));
    }
}
