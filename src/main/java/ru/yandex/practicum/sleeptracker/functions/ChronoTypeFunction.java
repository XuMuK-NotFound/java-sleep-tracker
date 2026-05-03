package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.enums.ChronoTypeOfSleep;
import ru.yandex.practicum.sleeptracker.reports.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronoTypeFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final LocalTime OWL_SLEEP_TIME = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKE_TIME = LocalTime.of(9, 0);
    private static final LocalTime LARK_SLEEP_TIME = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_TIME = LocalTime.of(7, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final String DESCRIPTION = "Хронотип пользователя";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return Optional.ofNullable(sessions)
                .filter(s -> !s.isEmpty())
                .map(this::determineDominantType) // Выносим всю сложную логику в отдельный метод
                .map(type -> new SleepAnalysisResult(DESCRIPTION, type))
                .orElse(new SleepAnalysisResult(DESCRIPTION, ChronoTypeOfSleep.DOVE));
    }

    private ChronoTypeOfSleep determineDominantType(List<SleepingSession> sessions) {

        Map<ChronoTypeOfSleep, Long> counts = sessions.stream()
                .filter(this::isNightSleep)
                .map(this::classifySession)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        if (counts.isEmpty()) return ChronoTypeOfSleep.DOVE;

        long max = Collections.max(counts.values());

        long winnersCount = counts.values().stream().filter(c -> c == max).count();
        if (winnersCount > 1) return ChronoTypeOfSleep.DOVE;

        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() == max)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(ChronoTypeOfSleep.DOVE);
    }

    private ChronoTypeOfSleep classifySession(SleepingSession session) {
        LocalTime sleepTime = session.getSleepStart().toLocalTime();
        LocalTime wakeTime = session.getSleepEnd().toLocalTime();

        boolean isLark = wakeTime.isBefore(LARK_WAKE_TIME) ||
                (wakeTime.equals(LARK_WAKE_TIME) && sleepTime.isBefore(LARK_SLEEP_TIME));

        boolean isOwl = sleepTime.isAfter(OWL_SLEEP_TIME) ||
                (sleepTime.equals(OWL_SLEEP_TIME) && wakeTime.isAfter(OWL_WAKE_TIME));

        if (isOwl) return ChronoTypeOfSleep.OWL;
        if (isLark) return ChronoTypeOfSleep.LARK;
        return ChronoTypeOfSleep.DOVE;
    }

    private boolean isNightSleep(SleepingSession session) {
        LocalDate startDate = session.getSleepStart().toLocalDate();
        LocalDate endDate = session.getSleepEnd().toLocalDate();
        LocalTime startTime = session.getSleepStart().toLocalTime();
        LocalTime endTime = session.getSleepEnd().toLocalTime();

        return !startDate.equals(endDate)
                || startTime.isBefore(NIGHT_END)
                || !endTime.isAfter(NIGHT_END);
    }
}
