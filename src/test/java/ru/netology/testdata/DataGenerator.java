package ru.netology.testdata;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class DataGenerator {

    private static final Faker FAKER_RU = new Faker(new Locale("ru"));
    private static final Faker FAKER_EN = new Faker(new Locale("en"));
    private static final Random RANDOM = new Random();

    private DataGenerator() {
    }

    public static String generateDate(int shift) {
        LocalDate date = LocalDate.now();
        return date.plusDays(shift).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String generateValidCity() {
        List<String> cities = new ArrayList<>(List.of("Москва",
                "Казань",
                "Санкт-Петербург",
                "Новосибирск",
                "Нижний Новгород",
                "Петрозаводск",
                "Тверь",
                "Ярославль",
                "Самара",
                "Уфа"
        ));

        return cities.get(RANDOM.nextInt(cities.size()));
    }

    public static String generateInvalidCity() {
        return FAKER_EN.country().capital();
    }

    public static String generateValidName() {
        return FAKER_RU.name().lastName() + " " + FAKER_RU.name().firstName();
    }

    public static String generateInvalidName() {
        return FAKER_EN.name().lastName() + " " + FAKER_EN.name().firstName();
    }

    public static String generateValidPhone() {
        // так как в поле маска на + и количество символов, валидный номер
        // состоит из 7ки вначале и последующих 10ти рандомных цифр
        return "7" + RANDOM.ints(10,0,10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public static String generateInvalidPhone() {
        // тут просто 6 рандомных цифр, это невалидный номер
        return RANDOM.ints(6,0,10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }
}
