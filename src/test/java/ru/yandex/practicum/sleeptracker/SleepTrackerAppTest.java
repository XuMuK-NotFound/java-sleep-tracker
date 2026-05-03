package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.enums.ChronoTypeOfSleep;
import ru.yandex.practicum.sleeptracker.enums.QualitySleepTypes;
import ru.yandex.practicum.sleeptracker.functions.*;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;
import ru.yandex.practicum.sleeptracker.reports.SleepAnalysisResult;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SleepTrackerAppTest {

    private List<SleepingSession> createTestSessions() {
        List<SleepingSession> sessions = new ArrayList<>();
        // 9 часов (540 мин)
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 0),
                LocalDateTime.of(2025, 10, 2, 7, 0),
                QualitySleepTypes.GOOD
        ));
        // 50 минут (короткий сон)
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 3, 12, 0),
                LocalDateTime.of(2025, 10, 3, 12, 50),
                QualitySleepTypes.GOOD
        ));
        return sessions;
    }

    @Test
    public void testTotalSessions() {
        TotalSessionsFunction function = new TotalSessionsFunction();
        // Проверка на данных
        assertEquals(2L, function.apply(createTestSessions()).getValue(), "Должно быть 2 сессии");
        // Проверка на null
        assertEquals(0L, function.apply(null).getValue(), "Для null должно быть 0");
    }

    @Test
    public void testMinSleep() {
        MinSleepFunction function = new MinSleepFunction();
        assertEquals(50L, function.apply(createTestSessions()).getValue(), "Минимум 50 мин");
        assertEquals("нет данных", function.apply(new ArrayList<>()).getValue(), "Пустой список - нет данных");
    }

    @Test
    public void testMaxSleep() {
        MaxSleepFunction function = new MaxSleepFunction();
        assertEquals(540L, function.apply(createTestSessions()).getValue(), "Максимум 540 мин");
        assertEquals("нет данных", function.apply(null).getValue(), "Для null - нет данных");
    }

    // ### ТЕСТЫ ДЛЯ СРЕДНЕЙ ДЛИТЕЛЬНОСТИ (AverageSleepFunction) ###

    @Test
    public void testAverageSleep_Calculation() {
        List<SleepingSession> sessions = new ArrayList<>();
        // 10 часов = 600 мин
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 20, 0),
                LocalDateTime.of(2025, 10, 2, 6, 0),
                QualitySleepTypes.GOOD
        ));
        // 6 часов = 360 мин
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 2, 22, 0),
                LocalDateTime.of(2025, 10, 3, 4, 0),
                QualitySleepTypes.GOOD
        ));
        // Среднее: (600 + 360) / 2 = 480

        AverageSleepFunction function = new AverageSleepFunction();
        SleepAnalysisResult result = function.apply(sessions);

        assertTrue(result.getValue().toString().contains("480"),
                "Среднее значение должно быть 480");
    }

    @Test
    public void testAverageSleep_Empty() {
        AverageSleepFunction function = new AverageSleepFunction();
        assertEquals("Данные отсутствуют.", function.apply(null).getValue(),
                "Для null должно писать, что данных нет");
    }

    // ### ТЕСТЫ ДЛЯ ПЛОХОГО КАЧЕСТВА (BadQualityFunction) ###

    @Test
    public void testBadQuality_Count() {
        List<SleepingSession> sessions = new ArrayList<>();
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 0),
                LocalDateTime.of(2025, 10, 2, 6, 0),
                QualitySleepTypes.BAD // +1
        ));
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 2, 22, 0),
                LocalDateTime.of(2025, 10, 3, 6, 0),
                QualitySleepTypes.GOOD // Пропускаем
        ));

        BadQualityFunction function = new BadQualityFunction();
        assertEquals(1L, function.apply(sessions).getValue(), "Должна быть 1 плохая сессия");
    }

    @Test
    public void testBadQuality_NullList() {
        BadQualityFunction function = new BadQualityFunction();
        assertEquals(0L, function.apply(null).getValue(), "Для null списка результат должен быть 0");
    }

    // ### ТЕСТЫ ДЛЯ ХРОНОТИПА (ChronoTypeFunction) ###

    @Test
    public void testChronoType_Owl() {
        List<SleepingSession> sessions = new ArrayList<>();
        // Ложимся поздно (23:30), встаем поздно (09:30) -> Типичная сова
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 30),
                LocalDateTime.of(2025, 10, 2, 9, 30),
                QualitySleepTypes.GOOD
        ));

        ChronoTypeFunction function = new ChronoTypeFunction();
        SleepAnalysisResult result = function.apply(sessions);

        assertEquals(ChronoTypeOfSleep.OWL, result.getValue(), "Должна определиться Сова");
    }

    @Test
    public void testChronoType_Empty() {
        ChronoTypeFunction function = new ChronoTypeFunction();
        // Если данных нет, по умолчанию возвращаем Голубя
        assertEquals(ChronoTypeOfSleep.DOVE, function.apply(null).getValue());
    }

    // ### ТЕСТЫ ДЛЯ БЕССОННЫХ НОЧЕЙ (SleeplessNightsFunction) ###

    @Test
    public void testSleeplessNights_Calculation() {
        List<SleepingSession> sessions = new ArrayList<>();
        // Ночь №1: поспали
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 0),
                LocalDateTime.of(2025, 10, 2, 6, 0),
                QualitySleepTypes.GOOD
        ));
        // Ночь 2: пропустили
        // Ночь 3: поспали
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 3, 22, 0),
                LocalDateTime.of(2025, 10, 4, 6, 0),
                QualitySleepTypes.GOOD
        ));

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult result = function.apply(sessions);

        assertEquals(1L, result.getValue(), "Должна быть 1 бессонная ночь (со 2 на 3 октября)");
    }

    @Test
    public void testSleeplessNights_NoData() {
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        assertEquals(0L, function.apply(new ArrayList<>()).getValue(),
                "Нет сессий — нет бессонных ночей (0)");
    }


}
