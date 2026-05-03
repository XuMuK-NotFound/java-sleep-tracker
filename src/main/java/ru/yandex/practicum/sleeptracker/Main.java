package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.Data.Logger;
import ru.yandex.practicum.sleeptracker.Data.SleepDataLoader;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String filePath = "src/main/resources/sleep_log.txt";

        Logger logger = new Logger(new File("commonLog.txt"));
        SleepTrackerApp app = new SleepTrackerApp();

        try {
            List<SleepingSession> sessions = SleepDataLoader.loadFromFile(filePath);

            System.out.println("### АНАЛИЗ СНА ###\n");

            app.analysisFunctions.stream()
                    .map(function -> function.apply(sessions))
                    .forEach(System.out::println);

        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
            logger.saveLogError(e);
        }
    }

}
