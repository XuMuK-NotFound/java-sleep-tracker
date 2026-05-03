package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.reports.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TotalSessionsFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final String DESCRIPTION = "Общее количество сессий сна";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return Optional.ofNullable(sessions)
                .map(s -> new SleepAnalysisResult(DESCRIPTION, (long) s.size()))
                .orElse(new SleepAnalysisResult(DESCRIPTION, 0L));
    }
}
