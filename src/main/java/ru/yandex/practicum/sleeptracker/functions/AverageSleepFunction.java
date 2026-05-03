package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;
import ru.yandex.practicum.sleeptracker.reports.SleepAnalysisResult;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AverageSleepFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final DecimalFormat DF = new DecimalFormat("0.00");

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return Optional.ofNullable(sessions)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    double average = s.stream()
                            .mapToLong(SleepingSession::getDurationInMinutes)
                            .average()
                            .orElse(0.0);

                    return new SleepAnalysisResult(
                            "Средняя продолжительность сна в минутах",
                            DF.format(average)
                    );
                })

                .orElseGet(() -> new SleepAnalysisResult(
                        "Средняя продолжительность сна в минутах",
                        "Данные отсутствуют."
                ));
    }
}
