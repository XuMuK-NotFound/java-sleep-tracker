package ru.yandex.practicum.sleeptracker.enums;

public enum ChronoTypeOfSleep { // Название класса с большой буквы — это стандарт
    LARK("Жаворонок"),
    OWL("Сова"),
    DOVE("Голубь");

    private final String translateToRussian;

    // Конструктор Enum всегда приватный по умолчанию
    ChronoTypeOfSleep(String translateToRussian) {
        this.translateToRussian = translateToRussian;
    }

    @Override
    public String toString() {
        return translateToRussian;
    }
}
