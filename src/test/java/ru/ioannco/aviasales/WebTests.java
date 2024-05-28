package ru.ioannco.aviasales;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import ru.ioannco.aviasales.model.dao.ClientDAO;
import ru.ioannco.aviasales.model.dao.FlightDAO;
import ru.ioannco.aviasales.model.entity.*;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
public class WebTests {
    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private FlightDAO flightDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Client testClient;
    private Flight testFlight;
    private Airline airline1;
    private Airport airport1;
    private Airport airport2;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(1024, 768));

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            City city1 = new City("Москва");
            City city2 = new City("Нью-Йорк");

            airport1 = new Airport("SVO", "Шереметьево", city1);
            airport2 = new Airport("JFK", "Джон Ф. Кеннеди", city2);

            airline1 = new Airline("Аэрофлот");

            session.save(city1);
            session.save(city2);
            session.save(airport1);
            session.save(airport2);
            session.save(airline1);

            session.getTransaction().commit();
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE booking RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.createNativeQuery("TRUNCATE flight RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.createNativeQuery("TRUNCATE client RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.createNativeQuery("TRUNCATE airline RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.createNativeQuery("TRUNCATE airport RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.createNativeQuery("TRUNCATE city RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    public void index() {
        driver.get("http://localhost:" + port + "/");
        assertEquals("Главная страница", driver.getTitle());
    }

    @Test
    public void testAddAndVerifyClient() {
        driver.get("http://localhost:" + port + "/clients");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("add-client-button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstName"))).sendKeys("Иван");
        driver.findElement(By.id("middleName")).sendKeys("Иванович");
        driver.findElement(By.id("lastName")).sendKeys("Иванов");
        driver.findElement(By.id("phoneNumber")).sendKeys("1234567890");
        driver.findElement(By.id("email")).sendKeys("ivanov@example.com");
        driver.findElement(By.id("address")).sendKeys("Москва, ул. Пример");

        driver.findElement(By.id("save-button")).click();

        driver.get("http://localhost:" + port + "/clients");

        List<WebElement> clients = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("client-row")));
        boolean clientFound = false;
        for (WebElement client : clients) {
            if (client.getText().contains("Иван") &&
                    client.getText().contains("Иванович") &&
                    client.getText().contains("Иванов")) {
                clientFound = true;
                break;
            }
        }

        assertTrue(clientFound, "Новый клиент должен быть в списке клиентов.");
    }

    @Test
    public void testDeleteClient() {
        testClient = new Client("Иван", "Иванович", "Иванов", "1234567890", "ivanov@example.com", "Москва, ул. Пример");
        clientDAO.save(testClient);

        driver.get("http://localhost:" + port + "/clients");

        List<WebElement> clients = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("client-row")));
        boolean clientFound = false;
        WebElement detailsButton = null;
        for (WebElement client : clients) {
            if (client.getText().contains("Иван") &&
                    client.getText().contains("Иванович") &&
                    client.getText().contains("Иванов")) {
                clientFound = true;
                detailsButton = client.findElement(By.id("details-button"));
                break;
            }
        }
        assertTrue(clientFound, "Тестовый клиент должен быть в списке клиентов.");

        if (detailsButton != null) {
            detailsButton.click();
        }

        wait.until(ExpectedConditions.elementToBeClickable(By.id("delete-client-button"))).click();
        driver.switchTo().alert().accept();

        driver.get("http://localhost:" + port + "/clients");
        clients = driver.findElements(By.id("client-row"));
        clientFound = false;
        for (WebElement client : clients) {
            if (client.getText().contains("Иван") &&
                    client.getText().contains("Иванович") &&
                    client.getText().contains("Иванов")) {
                clientFound = true;
                break;
            }
        }
        assertTrue(!clientFound, "Тестовый клиент должен быть удалён из списка клиентов.");
    }

    @Test
    public void testAddAndVerifyFlight() {
        driver.get("http://localhost:" + port + "/flights");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("add-flight-button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("number"))).sendKeys("SU1234");
        driver.findElement(By.id("airline")).sendKeys("Аэрофлот");
        driver.findElement(By.id("departureAirport")).sendKeys("Шереметьево");
        driver.findElement(By.id("arrivalAirport")).sendKeys("Джон Ф. Кеннеди");
        driver.findElement(By.id("departureDateTime")).sendKeys("2023-12-01T10:00");
        driver.findElement(By.id("arrivalDateTime")).sendKeys("2023-12-01T14:00");
        driver.findElement(By.id("price")).sendKeys("15000");
        driver.findElement(By.id("passengerLimit")).sendKeys("200");
        driver.findElement(By.id("passengerCount")).sendKeys("0");

        driver.findElement(By.id("save-button")).click();

        driver.get("http://localhost:" + port + "/flights");

        List<WebElement> flights = driver.findElements(By.id("flight-row"));
        boolean flightFound = false;
        for (WebElement flight : flights) {
            if (flight.getText().contains("Шереметьево") &&
                    flight.getText().contains("Джон Ф. Кеннеди")) {
                flightFound = true;
                break;
            }
        }

        assertTrue(flightFound, "Новый рейс должен быть в списке рейсов.");
    }

    @Test
    public void testDeleteFlight() {
        testFlight = new Flight("SU1234", airline1, airport1, airport2, new Date(), new Date(), 15000.0f, 200, 0, new Date());
        flightDAO.save(testFlight);

        driver.get("http://localhost:" + port + "/flights");

        List<WebElement> flights = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("flight-row")));
        boolean flightFound = false;
        WebElement detailsButton = null;
        for (WebElement flight : flights) {
            if (flight.getText().contains("Шереметьево") &&
                    flight.getText().contains("Джон Ф. Кеннеди")) {
                flightFound = true;
                detailsButton = flight.findElement(By.id("details-button"));
                break;
            }
        }
        assertTrue(flightFound, "Тестовый рейс должен быть в списке рейсов.");

        if (detailsButton != null) {
            detailsButton.click();
        }

        wait.until(ExpectedConditions.elementToBeClickable(By.id("delete-button"))).click();
        driver.switchTo().alert().accept();

        driver.get("http://localhost:" + port + "/flights");
        flights = driver.findElements(By.id("flight-row"));
        flightFound = false;
        for (WebElement flight : flights) {
            if (flight.getText().contains("Шереметьево") &&
                    flight.getText().contains("Джон Ф. Кеннеди")) {
                flightFound = true;
                break;
            }
        }
        assertTrue(!flightFound, "Тестовый рейс должен быть удалён из списка рейсов.");
    }

    @Test
    public void testNavigationBar() {
        driver.get("http://localhost:" + port + "/");

        WebElement homeLink = driver.findElement(By.id("nav-home"));

        homeLink.click();
        assertEquals("Главная страница", driver.getTitle());

        WebElement flightsLink = driver.findElement(By.id("nav-flights"));
        flightsLink.click();
        assertEquals("Авиарейсы", driver.getTitle());

        WebElement clientsLink = driver.findElement(By.id("nav-clients"));
        clientsLink.click();
        assertEquals("Список клиентов", driver.getTitle());
    }

    @Test
    public void testEditClient() {
        // Создание тестового клиента через DAO
        testClient = new Client("Иван", "Иванович", "Иванов", "1234567890", "ivanov@example.com", "Москва, ул. Пример");
        clientDAO.save(testClient);

        driver.get("http://localhost:" + port + "/clients");

        // Поиск тестового клиента в списке и переход на страницу деталей
        List<WebElement> clients = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("client-row")));
        boolean clientFound = false;
        WebElement detailsButton = null;
        for (WebElement client : clients) {
            if (client.getText().contains("Иван") &&
                    client.getText().contains("Иванович") &&
                    client.getText().contains("Иванов")) {
                clientFound = true;
                detailsButton = client.findElement(By.id("details-button"));
                break;
            }
        }
        assertTrue(clientFound, "Тестовый клиент должен быть в списке клиентов.");

        if (detailsButton != null) {
            detailsButton.click();
        }

        // Переход на страницу редактирования клиента
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("edit-client-button")));
        editButton.click();

        // Редактирование данных клиента
        WebElement firstNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstName")));
        firstNameField.clear();
        firstNameField.sendKeys("Пётр");

        WebElement lastNameField = driver.findElement(By.id("lastName"));
        lastNameField.clear();
        lastNameField.sendKeys("Петров");

        driver.findElement(By.id("save-button")).click();

        // Проверка изменений в списке клиентов
        driver.get("http://localhost:" + port + "/clients");
        clients = driver.findElements(By.id("client-row"));
        boolean editedClientFound = false;
        for (WebElement client : clients) {
            if (client.getText().contains("Пётр") &&
                    client.getText().contains("Петров")) {
                editedClientFound = true;
                break;
            }
        }
        assertTrue(editedClientFound, "Изменённый клиент должен быть в списке клиентов.");
    }
    @Test
    public void testAddBonusCardAndBookFlight() {
        // Создание тестового клиента через DAO
        testClient = new Client("Иван", "Иванович", "Иванов", "1234567890", "ivanov@example.com", "Москва, ул. Пример");
        clientDAO.save(testClient);

        // Создание тестового рейса через DAO с добавлением нескольких дней к дате вылета и прилета
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 3);
        Date departureDate = calendar.getTime();
        calendar.add(Calendar.HOUR, 4);
        Date arrivalDate = calendar.getTime();
        testFlight = new Flight("SU1234", airline1, airport1, airport2, departureDate, arrivalDate, 15000.0f, 200, 0, new Date());
        flightDAO.save(testFlight);

        driver.get("http://localhost:" + port + "/clients");

        // Поиск тестового клиента в списке и переход на страницу деталей
        List<WebElement> clients = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("client-row")));
        boolean clientFound = false;
        WebElement detailsButton = null;
        for (WebElement client : clients) {
            if (client.getText().contains("Иван") &&
                    client.getText().contains("Иванович") &&
                    client.getText().contains("Иванов")) {
                clientFound = true;
                detailsButton = client.findElement(By.id("details-button"));
                break;
            }
        }
        assertTrue(clientFound, "Тестовый клиент должен быть в списке клиентов.");

        if (detailsButton != null) {
            detailsButton.click();
        }

        // Добавление бонусной карты клиенту
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bonusAmount"))).sendKeys("1000");
        driver.findElement(By.id("add-bonus-card-button")).click();

        // Проверка наличия бонусной карты у клиента
        List<WebElement> bonusCards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("bonus-card-row")));
        assertTrue(bonusCards.size() > 0, "У клиента должна быть хотя бы одна бонусная карта.");

        // Переход на страницу бронирования рейса
        driver.get("http://localhost:" + port + "/flights");
        List<WebElement> flights = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("flight-row")));
        WebElement bookButton = null;
        for (WebElement flight : flights) {
            if (flight.getText().contains("Шереметьево") &&
                    flight.getText().contains("Джон Ф. Кеннеди")) {
                bookButton = flight.findElement(By.id("book-button"));
                break;
            }
        }
        assertNotNull(bookButton, "Кнопка бронирования должна быть доступна для тестового рейса.");

        if (bookButton != null) {
            bookButton.click();
        }

        // Оформление заказа
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("ivanov@example.com");
        driver.findElement(By.id("submit-button")).click();

        // Оплата заказа
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cardNumber"))).sendKeys("4111111111111111");
        driver.findElement(By.id("pay-button")).click();

        // Проверка успешной оплаты
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-message")));
        assertTrue(successMessage.isDisplayed(), "Сообщение об успешной оплате должно быть отображено.");

        // Проверка наличия бронирования у клиента
        driver.get("http://localhost:" + port + "/clients");
        clients = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("client-row")));
        for (WebElement client : clients) {
            if (client.getText().contains("Иван") &&
                    client.getText().contains("Иванович") &&
                    client.getText().contains("Иванов")) {
                detailsButton = client.findElement(By.id("details-button"));
                break;
            }
        }

        if (detailsButton != null) {
            detailsButton.click();
        }

        WebElement bookingsButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("all-bookings-button")));
        bookingsButton.click();

        List<WebElement> bookings = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("booking-row")));
        boolean bookingFound = false;
        for (WebElement booking : bookings) {
            if (booking.getText().contains("SU1234")) {
                bookingFound = true;
                break;
            }
        }
        assertTrue(bookingFound, "Бронирование рейса должно быть в списке бронирований клиента.");
    }

    @Test
    public void testEditFlight() {
        // Создание тестового рейса через DAO с добавлением нескольких дней к дате вылета и прилета
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 3);
        Date departureDate = calendar.getTime();
        calendar.add(Calendar.HOUR, 4);
        Date arrivalDate = calendar.getTime();
        testFlight = new Flight("SU1234", airline1, airport1, airport2, departureDate, arrivalDate, 15000.0f, 200, 0, new Date());
        flightDAO.save(testFlight);

        driver.get("http://localhost:" + port + "/flights");

        // Поиск тестового рейса в списке и переход на страницу деталей
        List<WebElement> flights = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("flight-row")));
        boolean flightFound = false;
        WebElement detailsButton = null;
        for (WebElement flight : flights) {
            if (flight.getText().contains("Шереметьево") &&
                    flight.getText().contains("Джон Ф. Кеннеди")) {
                flightFound = true;
                detailsButton = flight.findElement(By.id("details-button"));
                break;
            }
        }
        assertTrue(flightFound, "Тестовый рейс должен быть в списке рейсов.");

        if (detailsButton != null) {
            detailsButton.click();
        }

        // Переход на страницу редактирования рейса
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("edit-button")));
        editButton.click();

        // Редактирование данных рейса
        WebElement flightNumberField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("number")));
        flightNumberField.clear();
        flightNumberField.sendKeys("SU5678");

        WebElement priceField = driver.findElement(By.id("price"));
        priceField.clear();
        priceField.sendKeys("20000");

        driver.findElement(By.id("save-button")).click();

        // Проверка изменений в списке рейсов
        driver.get("http://localhost:" + port + "/flights");
        flights = driver.findElements(By.id("flight-row"));
        flightFound = false;
        for (WebElement flight : flights) {
            if (flight.getText().contains("Шереметьево") &&
                    flight.getText().contains("Москва")) {
                flightFound = true;
                detailsButton = flight.findElement(By.id("details-button"));
                break;
            }
        }

        if (flightFound)
            detailsButton.click();

        WebElement flightNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flight-number")));

        assertTrue(flightNumber.getText().contains("SU5678"), "Flight number must contain new value.");
    }


}
