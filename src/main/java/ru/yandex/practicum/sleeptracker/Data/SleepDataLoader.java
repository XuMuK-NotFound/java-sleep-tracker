package ru.yandex.practicum.sleeptracker.Data;

import ru.yandex.practicum.sleeptracker.enums.QualitySleepTypes;
import ru.yandex.practicum.sleeptracker.sessions.SleepingSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SleepDataLoader {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static List<SleepingSession> loadFromFile(String filePath) throws IOException {
        // Простая проверка пути
        String path = Optional.ofNullable(filePath)
                .filter(p -> !p.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Путь к файлу не указан"));

        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            List<SleepingSession> sessions = lines
                    .filter(line -> line != null && !line.trim().isEmpty())
                    .map(SleepDataLoader::parseSession)
                    .collect(Collectors.toList());

            if (sessions.isEmpty()) {
                throw new IOException("Найден пустой файл " + filePath);
            }

            return sessions;
        } catch (IOException e) {
            throw new IOException("Ошибка при работе с файлом: " + path, e);
        }
    }

    private static SleepingSession parseSession(String line) {
        String[] parts = line.split(";");

        // Берем данные как есть
        LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        QualitySleepTypes quality = QualitySleepTypes.valueOf(parts[2].trim());

        return new SleepingSession(start, end, quality);
    }
}
