package ru.netology.apptest;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.testdata.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryTest {

    @BeforeAll
    public static void setupBrowser() {
        System.setProperty("selenide.holdBrowserOpen", "true");
        System.setProperty("selenide.browser", "chrome");
        System.setProperty("selenide.headless", "false");
    }

    @BeforeEach
    void setupHost() {
        open("http://localhost:9999");
    }

    // REGRESS - повторно тестируем на соотв. предыдущим требованиям

    @Test
    public void shouldSendFormWithCorrectData() {
        $("[data-test-id='city'] input").setValue(DataGenerator.generateValidCity());

        String validDate = DataGenerator.generateDate(6);
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(validDate);

        $("[data-test-id='name'] input").setValue(DataGenerator.generateValidName());
        $("[data-test-id='phone'] input").setValue(DataGenerator.generateValidPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Запланировать"))).click();

        String expectedText = "Встреча успешно запланирована на " + validDate;

        $("[data-test-id='success-notification'] .notification__content")
                .should(Condition.appear, Duration.ofSeconds(15))
                .shouldHave(Condition.exactText(expectedText));
    }

    @Test
    public void shouldNotSendFormWithIncorrectCity() {
        $("[data-test-id='city'] input").setValue(DataGenerator.generateInvalidCity());

        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(DataGenerator.generateDate(5));

        $("[data-test-id='name'] input").setValue(DataGenerator.generateValidName());
        $("[data-test-id='phone'] input").setValue(DataGenerator.generateValidPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Запланировать"))).click();

        String expectedText = "Доставка в выбранный город недоступна";
        $("[data-test-id='city'].input_invalid .input__sub")
                .shouldHave(Condition.exactText(expectedText));

        $("[data-test-id='success-notification']")
                .shouldNot(Condition.appear);
    }

    @Test
    public void shouldNotSendFormWithIncorrectDate() {
        $("[data-test-id='city'] input").setValue(DataGenerator.generateValidCity());

        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(DataGenerator.generateDate(-6));

        $("[data-test-id='name'] input").setValue(DataGenerator.generateValidName());
        $("[data-test-id='phone'] input").setValue(DataGenerator.generateValidPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Запланировать"))).click();

        String expectedText = "Заказ на выбранную дату невозможен";
        $("[data-test-id='date'] .input_invalid .input__sub")
                .shouldHave(Condition.exactText(expectedText));

        $("[data-test-id='success-notification']")
                .shouldNot(Condition.appear);
    }

    @Test
    public void shouldNotSendFormWithIncorrectName() {
        $("[data-test-id='city'] input").setValue(DataGenerator.generateValidCity());

        String validDate = DataGenerator.generateDate(6);
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(validDate);

        $("[data-test-id='name'] input").setValue(DataGenerator.generateInvalidName());
        $("[data-test-id='phone'] input").setValue(DataGenerator.generateValidPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Запланировать"))).click();

        String expectedText = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.";
        $("[data-test-id='name'].input_invalid .input__sub")
                .shouldHave(Condition.exactText(expectedText));

        $("[data-test-id='success-notification']")
                .shouldNot(Condition.appear);
    }

    @Test
    public void shouldNotSendFormWithIncorrectPhone() {
        $("[data-test-id='city'] input").setValue(DataGenerator.generateValidCity());

        String validDate = DataGenerator.generateDate(4);
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(validDate);

        $("[data-test-id='name'] input").setValue(DataGenerator.generateValidName());
        $("[data-test-id='phone'] input").setValue(DataGenerator.generateInvalidPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Запланировать"))).click();

        String expectedText = "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.";
        $("[data-test-id='phone'].input_invalid .input__sub")
                .shouldHave(Condition.exactText(expectedText));

        $("[data-test-id='success-notification']")
                .shouldNot(Condition.appear);
    }

    @Test
    public void shouldNotSendFormWithoutAgreementCheckbox() {
        $("[data-test-id='city'] input").setValue(DataGenerator.generateValidCity());

        String validDate = DataGenerator.generateDate(7);
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(validDate);

        $("[data-test-id='name'] input").setValue(DataGenerator.generateValidName());
        $("[data-test-id='phone'] input").setValue(DataGenerator.generateValidPhone());
        $$("button").find((Condition.exactText("Запланировать"))).click();

        $("[data-test-id='agreement']")
                .shouldHave(Condition.cssClass("input_invalid"));
        $("[data-test-id='success-notification']")
                .shouldNot(Condition.appear);
    }

    // НОВЫЙ ФУНКЦИОНАЛ - повторная отправка формы
    @Test
    public void shouldReplanDeliveryWithCorrectData() {
        String validCity = DataGenerator.generateValidCity();
        String firstDate = DataGenerator.generateDate(4);
        String secondDate = DataGenerator.generateDate(8);
        String validName = DataGenerator.generateValidName();
        String validPhone = DataGenerator.generateValidPhone();

        // отправляем форму 1й раз
        $("[data-test-id='city'] input").setValue(validCity);
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(firstDate);
        $("[data-test-id='name'] input").setValue(validName);
        $("[data-test-id='phone'] input").setValue(validPhone);
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Запланировать"))).click();

        // очищаем поля и отправляем повторно
        $("[data-test-id='city'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(validCity);
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(secondDate);
        $("[data-test-id='name'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(validName);
        $("[data-test-id='phone'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(validPhone);
        $$("button").find((Condition.exactText("Запланировать"))).click();

        // уведомление о перепланировании
        $("[data-test-id='replan-notification'] .notification__content")
                .should(Condition.appear);
        $$("button").find((Condition.exactText("Перепланировать"))).click();

        // ожидаемый результат
        String expectedText = "Встреча успешно запланирована на " + secondDate;
        $("[data-test-id='success-notification'] .notification__content")
                .should(Condition.appear)
                .shouldHave(Condition.exactText(expectedText));
    }
}