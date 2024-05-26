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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
class ClientDAOTest {
    @Autowired
    ClientDAO clientDAO;

    @Autowired
    SessionFactory sessionFactory;

    List<Client> createdClients;

    @BeforeEach
    void setUp() {
        List<Client> clients = new ArrayList<>();

        clients.add(new Client("Иван", "Сергеевич", "Черемисенов", "+79969763732", "email@email.com", "Кремль дом 1"));
        clients.add(new Client("Федор", "Сергеевич", "Черемисенов", "+812312341234", "another@email.com", "Кремль дом 2"));
        clients.add(new Client("Ивандар", "Сергеевич", "Черемисенов", "+79969763735", "email2@email.com", "Кремль дом 1"));

        for (int i = 1; i <= 20; ++i) {
            clients.add(new Client("dummy " + i, "", "", "", "" ,""));
        }

        clientDAO.saveAll(clients);
        createdClients = clients;

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
        Flight flight1 = new Flight("FL001", airport1, airport2, new Date(), new Date(), 100.0f, 200, 150, new Date());
        Flight flight2 = new Flight("FL002", airport2, airport1, new Date(), new Date(), 120.0f, 250, 200, new Date());

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(city1);
            session.save(city2);
            session.save(airport1);
            session.save(airport2);
            session.save(airline1);
            session.save(airline2);
            session.save(flight1);
            session.save(flight2);

            // Creating bookings
            Booking booking1 = new Booking(clients.get(0), airline1, flight1, new Date(), true, 100.0f, 0);
            Booking booking2 = new Booking(clients.get(1), airline1, flight1, new Date(), false, 100.0f, 0);
            Booking booking3 = new Booking(clients.get(2), airline2, flight2, new Date(), true, 120.0f, 0);

            session.save(booking1);
            session.save(booking2);
            session.save(booking3);

            session.getTransaction().commit();
        }
    }

    @Test
    void getByEmptyFilter() {
        List<Client> clients = clientDAO.getByFilter(ClientDAO.Filter.builder().build());

        assertEquals(clients.stream().map(Client::getFirstName).collect(Collectors.toSet()),
                createdClients.stream().map(Client::getFirstName).collect(Collectors.toSet()));
    }

    @Test
    void getByFirstNameFilter() {
        ClientDAO.Filter criteria = clientDAO.getFilterBuilder()
                .firstName("Иван")
                .build();

        List<Client> clients = clientDAO.getByFilter(criteria);

        Set<String> expectedNames = new HashSet<>();
        expectedNames.add("Иван");
        expectedNames.add("Ивандар");

        assertEquals(clients.stream().map(Client::getFirstName).collect(Collectors.toSet()),
                expectedNames);
    }

    @Test
    void getByFullNameFilter() {
        ClientDAO.Filter criteria = clientDAO.getFilterBuilder()
                .firstName("Федор")
                .middleName("Сергеевич")
                .lastName("Черемисенов")
                .build();

        List<Client> clients = clientDAO.getByFilter(criteria);

        assertEquals(1, clients.size());
        assertEquals("Федор", clients.getFirst().getFirstName());
    }

    @Test
    void getByPhoneNumberFilter() {
        ClientDAO.Filter criteria = clientDAO.getFilterBuilder()
                .phoneNumber("+79969763732")
                .build();

        List<Client> clients = clientDAO.getByFilter(criteria);

        assertEquals(1, clients.size());
        assertEquals("+79969763732", clients.getFirst().getPhoneNumber());
    }

    @Test
    void getByEmailFilter() {
        ClientDAO.Filter criteria = clientDAO.getFilterBuilder()
                .email("email@email.com")
                .build();

        List<Client> clients = clientDAO.getByFilter(criteria);

        assertEquals(1, clients.size());
        assertEquals("email@email.com", clients.get(0).getEmail());
    }

    @Test
    void getByAddressFilter() {
        ClientDAO.Filter criteria = clientDAO.getFilterBuilder()
                .address("Кремль дом 1")
                .build();

        List<Client> clients = clientDAO.getByFilter(criteria);

        assertEquals(2, clients.size());
        assertTrue(clients.stream().allMatch(client -> client.getAddress().equals("Кремль дом 1")));
    }

    @Test
    void getByFlightNoFilter() {
        ClientDAO.Filter criteria = clientDAO.getFilterBuilder()
                .flightNo("FL001")
                .build();

        List<Client> clients = clientDAO.getByFilter(criteria);

        assertEquals(2, clients.size());
        assertTrue(clients.stream().map(Client::getFirstName).collect(Collectors.toSet()).containsAll(Set.of("Иван", "Федор")));
    }

    @Test
    void getByFlightPayedFilter() {
        ClientDAO.Filter criteria = clientDAO.getFilterBuilder()
                .flightPayed(true)
                .build();

        List<Client> clients = clientDAO.getByFilter(criteria);

        assertEquals(2, clients.size());
        assertTrue(clients.stream().map(Client::getFirstName).collect(Collectors.toSet()).containsAll(Set.of("Иван", "Ивандар")));
    }

    @Test
    void getByFilterPaginated() {
        ClientDAO.Filter empty = clientDAO.getFilterBuilder().build();

        List<Client> page = clientDAO.getByFilterPaginated(empty, 5, 0);
        assertEquals(5, page.size());

        Set<Client> expectedPage = createdClients.stream().limit(5).collect(Collectors.toSet());
        assertEquals(expectedPage.stream().map(Client::getFirstName).collect(Collectors.toSet()),
                page.stream().map(Client::getFirstName).collect(Collectors.toSet()));

        page = clientDAO.getByFilterPaginated(empty, 10, 1);
        expectedPage = createdClients.stream().skip(10).limit(10).collect(Collectors.toSet());

        assertEquals(10, page.size());
        assertEquals(expectedPage.stream().map(Client::getFirstName).collect(Collectors.toSet()),
                page.stream().map(Client::getFirstName).collect(Collectors.toSet()));

        page = clientDAO.getByFilterPaginated(empty, 10, 2);
        expectedPage = createdClients.stream().skip(20).limit(30).collect(Collectors.toSet());

        assertEquals(3, page.size());
        assertEquals(expectedPage.stream().map(Client::getFirstName).collect(Collectors.toSet()),
                page.stream().map(Client::getFirstName).collect(Collectors.toSet()));
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