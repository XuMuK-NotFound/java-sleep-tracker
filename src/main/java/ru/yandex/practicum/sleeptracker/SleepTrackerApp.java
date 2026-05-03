package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.functions.*;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;
import ru.yandex.practicum.sleeptracker.reports.SleepAnalysisResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/*
 *  Апп просто выполняет задачи по сборке функций
 * */
public class SleepTrackerApp {

    // Сбор всех функций в одну кучу (Реестр)
    public final List<Function<List<SleepingSession>, SleepAnalysisResult>> analysisFunctions;

    public SleepTrackerApp() {
        analysisFunctions = new ArrayList<>();

        analysisFunctions.add(new TotalSessionsFunction());
        analysisFunctions.add(new MinSleepFunction());
        analysisFunctions.add(new MaxSleepFunction());
        analysisFunctions.add(new AverageSleepFunction());
        analysisFunctions.add(new BadQualityFunction());
        analysisFunctions.add(new SleeplessNightsFunction());
        analysisFunctions.add(new ChronoTypeFunction());
    }

}