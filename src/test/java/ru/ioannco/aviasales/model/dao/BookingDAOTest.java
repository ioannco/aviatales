package ru.ioannco.aviasales.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.ioannco.aviasales.model.entity.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
class BookingDAOTest {

    @Autowired
    BookingDAO bookingDAO;

    @Autowired
    SessionFactory sessionFactory;

    List<Booking> createdBookings;
    Client client1, client2;
    Airline airline1, airline2;
    Flight flight1, flight2;

    @BeforeEach
    void setUp() {
        List<Booking> bookings = new ArrayList<>();

        // Creating clients
        client1 = new Client("Иван", "Иванович", "Иванов", "+79991234567", "ivanov@example.com", "Адрес 1");
        client2 = new Client("Петр", "Петрович", "Петров", "+79997654321", "petrov@example.com", "Адрес 2");

        // Creating airlines
        airline1 = new Airline("Аэрофлот");
        airline2 = new Airline("Россия");

        // Creating cities
        City city1 = new City("Москва");
        City city2 = new City("Санкт-Петербург");

        // Creating airports
        Airport airport1 = new Airport("SVO", "Шереметьево", city1);
        Airport airport2 = new Airport("LED", "Пулково", city2);

        // Creating flights
        flight1 = new Flight("FL001", airline1, airport1, airport2, new Date(System.currentTimeMillis() + 86400000), new Date(System.currentTimeMillis() + 90000000), 100.0f, 200, 150, new Date());
        flight2 = new Flight("FL002", airline2, airport2, airport1, new Date(System.currentTimeMillis() + 172800000), new Date(System.currentTimeMillis() + 178800000), 120.0f, 250, 200, new Date());

        // Creating bookings
        bookings.add(new Booking(client1, airline1, flight1, new Date(), true, 100.0f, 0));
        bookings.add(new Booking(client1, airline2, flight2, new Date(), true, 120.0f, 0));
        bookings.add(new Booking(client2, airline1, flight1, new Date(), false, 100.0f, 0));
        bookings.add(new Booking(client2, airline2, flight2, new Date(), false, 120.0f, 0));

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(client1);
            session.save(client2);
            session.save(airline1);
            session.save(airline2);
            session.save(city1);
            session.save(city2);
            session.save(airport1);
            session.save(airport2);
            session.save(flight1);
            session.save(flight2);

            bookings.forEach(session::save);

            session.getTransaction().commit();
        }

        createdBookings = bookings;
    }

    @Test
    void getByFilterWithSorting() {
        BookingDAO.Filter filter = bookingDAO.getFilterBuilder()
                .client(client2)
                .build();

        List<Booking> bookings = bookingDAO.getByFilter(filter, BookingDAO.SortOrder.PRICE_DESC);

        assertEquals(2, bookings.size());
        assertEquals(120.0f, bookings.get(0).getPrice());
        assertEquals(100.0f, bookings.get(1).getPrice());
    }

    @AfterEach
    void tearDown() {
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
}