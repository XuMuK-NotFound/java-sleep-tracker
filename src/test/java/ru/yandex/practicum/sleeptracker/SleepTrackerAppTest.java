package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.DisplayName;
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
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 0),
                LocalDateTime.of(2025, 10, 2, 7, 0),
                QualitySleepTypes.GOOD
        ));
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 3, 12, 0),
                LocalDateTime.of(2025, 10, 3, 12, 50),
                QualitySleepTypes.GOOD
        ));
        return sessions;
    }

    @Test
    @DisplayName("Общее количество сессий сна")
    public void testTotalSessions() {
        TotalSessionsFunction function = new TotalSessionsFunction();
        assertEquals(2L, function.apply(createTestSessions()).getValue(), "Должно быть 2 сессии");
        assertEquals(0L, function.apply(null).getValue(), "Для null должно быть 0");
    }

    @Test
    @DisplayName("Минимальный сон")
    public void testMinSleep() {
        MinSleepFunction function = new MinSleepFunction();
        assertEquals(50L, function.apply(createTestSessions()).getValue(), "Минимум 50 мин");
        assertEquals("нет данных", function.apply(new ArrayList<>()).getValue(), "Пустой список - нет данных");
    }

    @Test
    @DisplayName("Максимальный сон")
    public void testMaxSleep() {
        MaxSleepFunction function = new MaxSleepFunction();
        assertEquals(540L, function.apply(createTestSessions()).getValue(), "Максимум 540 мин");
        assertEquals("нет данных", function.apply(null).getValue(), "Для null - нет данных");
    }

    // ### ТЕСТЫ ДЛЯ СРЕДНЕЙ ДЛИТЕЛЬНОСТИ (AverageSleepFunction) ###

    @Test
    @DisplayName("Счет в минутах")
    public void testAverageSleep_Calculation() {
        List<SleepingSession> sessions = new ArrayList<>();
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 20, 0),
                LocalDateTime.of(2025, 10, 2, 6, 0),
                QualitySleepTypes.GOOD
        ));
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 2, 22, 0),
                LocalDateTime.of(2025, 10, 3, 4, 0),
                QualitySleepTypes.GOOD
        ));

        AverageSleepFunction function = new AverageSleepFunction();
        SleepAnalysisResult result = function.apply(sessions);

        assertTrue(result.getValue().toString().contains("480"),
                "Среднее значение должно быть 480");
    }

    @Test
    @DisplayName("Проверка счета на пустоту")
    public void testAverageSleep_Empty() {
        AverageSleepFunction function = new AverageSleepFunction();
        assertEquals("Данные отсутствуют.", function.apply(null).getValue(),
                "Для null должно писать, что данных нет");
    }

    // ### ТЕСТЫ ДЛЯ ПЛОХОГО КАЧЕСТВА (BadQualityFunction) ###

    @Test
    @DisplayName("Счетчик плохого сна")
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
    @DisplayName("Пустота в списке плохого сна")
    public void testBadQuality_NullList() {
        BadQualityFunction function = new BadQualityFunction();
        assertEquals(0L, function.apply(null).getValue(), "Для null списка результат должен быть 0");
    }

    // ### ТЕСТЫ ДЛЯ ХРОНОТИПА (ChronoTypeFunction) ###

    @Test
    @DisplayName("Вывод хронотипа")
    public void testChronoType_Owl() {
        List<SleepingSession> sessions = new ArrayList<>();
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
    @DisplayName("Голубь при равенстве дней Совы и Жаворонка")
    public void testChronoType_DoveEquality() {
        List<SleepingSession> sessions = new ArrayList<>();

        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 30),
                LocalDateTime.of(2025, 10, 2, 9, 30),
                QualitySleepTypes.GOOD
        ));

        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 2, 21, 0),
                LocalDateTime.of(2025, 10, 3, 5, 0),
                QualitySleepTypes.GOOD
        ));

        ChronoTypeFunction function = new ChronoTypeFunction();
        SleepAnalysisResult result = function.apply(sessions);

        assertEquals(ChronoTypeOfSleep.DOVE, result.getValue(),
                "Совы = Жаворонкам, должен получиться Голубь");
    }

    @Test
    @DisplayName("Вывод голубя по умолчанию")
    public void testChronoType_Empty() {
        ChronoTypeFunction function = new ChronoTypeFunction();
        assertEquals(ChronoTypeOfSleep.DOVE, function.apply(null).getValue());
    }

    // ### ТЕСТЫ ДЛЯ БЕССОННЫХ НОЧЕЙ (SleeplessNightsFunction) ###

    @Test
    @DisplayName("Счетчик плохого сна")
    public void testSleeplessNights_Calculation() {
        List<SleepingSession> sessions = new ArrayList<>();
        sessions.add(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 0),
                LocalDateTime.of(2025, 10, 2, 6, 0),
                QualitySleepTypes.GOOD
        ));
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
    @DisplayName("Вывод нуля если нет бессонных ночей")
    public void testSleeplessNights_NoData() {
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        assertEquals(0L, function.apply(new ArrayList<>()).getValue(),
                "Нет сессий — нет бессонных ночей (0)");
    }


}
