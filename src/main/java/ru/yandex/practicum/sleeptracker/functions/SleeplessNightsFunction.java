package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.reports.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class SleeplessNightsFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final String DESCRIPTION = "Количество бессонных ночей";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return Optional.ofNullable(sessions)
                .filter(s -> !s.isEmpty())
                .map(this::calculateSleeplessNights)
                .orElse(new SleepAnalysisResult(DESCRIPTION, 0L));
    }

    private SleepAnalysisResult calculateSleeplessNights(List<SleepingSession> sessions) {
        // Поиск дней когда спал
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(this::isNightSleep)
                .flatMap(session -> getNightDates(session).stream())
                .collect(Collectors.toSet());

        // Поиск периода (начало и конец)
        Optional<LocalDateTime> firstStart = sessions.stream()
                .map(SleepingSession::getSleepStart)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> lastEnd = sessions.stream()
                .map(SleepingSession::getSleepEnd)
                .max(LocalDateTime::compareTo);

        if (firstStart.isEmpty() || lastEnd.isEmpty()) {
            return new SleepAnalysisResult(DESCRIPTION, 0L);
        }

        LocalDate firstNight = getFirstNightInPeriod(firstStart.get());
        LocalDate lastNight = getLastNightInPeriod(lastEnd.get());

        if (firstNight.isAfter(lastNight)) {
            return new SleepAnalysisResult(DESCRIPTION, 0L);
        }

        // Счет времени
        long totalNights = ChronoUnit.DAYS.between(firstNight, lastNight) + 1;
        long sleeplessNights = Math.max(0, totalNights - nightsWithSleep.size());

        return new SleepAnalysisResult(DESCRIPTION, sleeplessNights);
    }

    private LocalDate getFirstNightInPeriod(LocalDateTime start) {
        return start.toLocalTime().isBefore(NIGHT_END)
                ? start.toLocalDate().minusDays(1)
                : start.toLocalDate();
    }

    private LocalDate getLastNightInPeriod(LocalDateTime end) {
        LocalTime endTime = end.toLocalTime();
        if (endTime.isBefore(NIGHT_END) || endTime.equals(NIGHT_START) || endTime.isBefore(LocalTime.of(18, 0))) {
            return end.toLocalDate().minusDays(1);
        }
        return end.toLocalDate();
    }

    private Set<LocalDate> getNightDates(SleepingSession session) {
        LocalDate startDate = session.getSleepStart().toLocalDate();
        LocalDate endDate = session.getSleepEnd().toLocalDate();
        LocalTime startTime = session.getSleepStart().toLocalTime();

        if (startDate.equals(endDate)) {
            return startTime.isBefore(NIGHT_END) ? Set.of(startDate.minusDays(1)) : Set.of(startDate);
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return LongStream.range(0, daysBetween)
                .mapToObj(startDate::plusDays)
                .collect(Collectors.toSet());
    }

    private boolean isNightSleep(SleepingSession session) {
        LocalDate startDate = session.getSleepStart().toLocalDate();
        LocalDate endDate = session.getSleepEnd().toLocalDate();
        LocalTime startTime = session.getSleepStart().toLocalTime();
        LocalTime endTime = session.getSleepEnd().toLocalTime();
        // если одно условие верно то сон ночной
        return !startDate.equals(endDate)
                || startTime.isBefore(NIGHT_END)
                || endTime.isBefore(NIGHT_END)
                || endTime.equals(NIGHT_START);
    }
}
