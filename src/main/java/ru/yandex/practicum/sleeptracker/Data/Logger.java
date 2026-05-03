package ru.yandex.practicum.sleeptracker.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/*
 * Логгер сохранит все ошибки для следующих наработок
 * лучше использовать IOException если например программу трекера захотят адаптировать на часы
 * */
public class Logger {
    private final File commonLog;

    public Logger(File commonLog) {
        this.commonLog = commonLog;
    }

    public void saveLogError(Exception e) {
        String time = LocalDateTime.now().toString();
        try (FileWriter writer = new FileWriter(commonLog, true)) {
            writer.write(e.toString() + "\n");
            writer.write("Time: " + time + "\n");
            writer.write("=".repeat(20) + "\n");
        } catch (IOException ignored) {
            // Если даже логгер упал, ничего не поделаешь
        }
    }
}


