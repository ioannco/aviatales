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
class FlightDAOTest {

    @Autowired
    FlightDAO flightDAO;

    @Autowired
    SessionFactory sessionFactory;

    List<Flight> createdFlights;

    @BeforeEach
    void setUp() {
        List<Flight> flights = new ArrayList<>();

        // Creating cities
        City city1 = new City("Москва");
        City city2 = new City("Санкт-Петербург");

        // Creating airports
        Airport airport1 = new Airport("SVO", "Шереметьево", city1);
        Airport airport2 = new Airport("LED", "Пулково", city2);

        // Creating airlines
        Airline airline1 = new Airline("Аэрофлот");
        Airline airline2 = new Airline("Россия");

        // Creating flights
        flights.add(new Flight("FL001", airline1, airport1, airport2, new Date(System.currentTimeMillis() + 86400000), new Date(System.currentTimeMillis() + 90000000), 100.0f, 200, 150, new Date()));
        flights.add(new Flight("FL002", airline2, airport2, airport1, new Date(System.currentTimeMillis() + 86400000), new Date(System.currentTimeMillis() + 90000000), 120.0f, 250, 200, new Date()));
        flights.add(new Flight("FL003", airline1, airport1, airport2, new Date(System.currentTimeMillis() + 172800000), new Date(System.currentTimeMillis() + 178800000), 110.0f, 200, 150, new Date()));
        flights.add(new Flight("FL004", airline2, airport2, airport1, new Date(System.currentTimeMillis() + 172800000), new Date(System.currentTimeMillis() + 178800000), 130.0f, 250, 200, new Date()));

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(city1);
            session.save(city2);
            session.save(airport1);
            session.save(airport2);
            session.save(airline1);
            session.save(airline2);

            flights.forEach(session::save);

            session.getTransaction().commit();
        }

        createdFlights = flights;
    }

    @Test
    void getByAirlineFilter() {
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .airline("Аэрофлот")
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.DEPARTURE_TIME_ASC);

        assertEquals(2, flights.size());
        assertTrue(flights.stream().allMatch(flight -> flight.getAirline().getVerbose().equals("Аэрофлот")));
    }

    @Test
    void getByDepartureDateFilter() {
        Date departureDate = new Date(System.currentTimeMillis() + 86400000);
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .departureDate(departureDate)
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.DEPARTURE_TIME_ASC);

        assertEquals(2, flights.size());
        assertTrue(flights.stream().allMatch(flight -> {
            Date flightDate = flight.getDepartureDateTime();
            return flightDate.after(getStartOfDay(departureDate)) && flightDate.before(getEndOfDay(departureDate));
        }));
    }

    @Test
    void getByArrivalDateFilter() {
        Date arrivalDate = new Date(System.currentTimeMillis() + 90000000);
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .arrivalDate(arrivalDate)
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10,

                0, FlightDAO.SortOrder.ARRIVAL_TIME_ASC);

        assertEquals(2, flights.size());
        assertTrue(flights.stream().allMatch(flight -> {
            Date flightDate = flight.getArrivalDateTime();
            return flightDate.after(getStartOfDay(arrivalDate)) && flightDate.before(getEndOfDay(arrivalDate));
        }));
    }

    @Test
    void getByDepartureCityFilter() {
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .departureCity("Москва")
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.DEPARTURE_TIME_ASC);

        assertEquals(2, flights.size());
        assertTrue(flights.stream().allMatch(flight -> flight.getDepartureAirport().getCity().getVerbose().equals("Москва")));
    }

    @Test
    void getByArrivalCityFilter() {
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .arrivalCity("Санкт-Петербург")
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.ARRIVAL_TIME_ASC);

        assertEquals(2, flights.size());
        assertTrue(flights.stream().allMatch(flight -> flight.getArrivalAirport().getCity().getVerbose().equals("Санкт-Петербург")));
    }

    @Test
    void getByDepartureAirportFilter() {
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .departureAirport("Шереметьево")
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.DEPARTURE_TIME_ASC);

        assertEquals(2, flights.size());
        assertTrue(flights.stream().allMatch(flight -> flight.getDepartureAirport().getVerbose().equals("Шереметьево")));
    }

    @Test
    void getByArrivalAirportFilter() {
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .arrivalAirport("Пулково")
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.ARRIVAL_TIME_ASC);

        assertEquals(2, flights.size());
        assertTrue(flights.stream().allMatch(flight -> flight.getArrivalAirport().getVerbose().equals("Пулково")));
    }

    @Test
    void getByFlightNumberFilter() {
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .flightNumber("FL001")
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.DEPARTURE_TIME_ASC);

        assertEquals(1, flights.size());
        assertEquals("FL001", flights.get(0).getNumber());
    }

    @Test
    void getByPurchasable() {
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .purchasable(true)
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.DEPARTURE_TIME_ASC);

        assertEquals(4, flights.size());
    }

    @Test
    void getByAvailableSeats() {
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .availableSeats(true)
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.DEPARTURE_TIME_ASC);

        assertEquals(4, flights.size());
    }

    @Test
    void getByPriceRange() {
        FlightDAO.Filter filter = flightDAO.getFilterBuilder()
                .minPrice(100.0f)
                .maxPrice(120.0f)
                .build();

        List<Flight> flights = flightDAO.getByFilter(filter, 10, 0, FlightDAO.SortOrder.PRICE_ASC);

        assertEquals(3, flights.size());
        assertTrue(flights.stream().allMatch(flight -> flight.getPrice() >= 100.0f && flight.getPrice() <= 120.0f));
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

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}