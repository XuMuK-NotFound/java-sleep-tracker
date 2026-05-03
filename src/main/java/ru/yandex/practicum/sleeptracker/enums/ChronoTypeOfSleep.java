package ru.yandex.practicum.sleeptracker.enums;

public enum ChronoTypeOfSleep { // Название класса с большой буквы — это стандарт
    LARK("Жаворонок"),
    OWL("Сова"),
    DOVE("Голубь");

    private final String translateToRussian;

    ChronoTypeOfSleep(String translateToRussian) {
        this.translateToRussian = translateToRussian;
    }

    @Override
    public String toString() {
        return translateToRussian;
    }
}
