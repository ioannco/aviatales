package ru.ioannco.aviasales.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.w3c.dom.ranges.Range;
import ru.ioannco.aviasales.model.entity.Client;

import java.util.ArrayList;
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
    }

    @Test
    void getByEmptyFilter() {
        List<Client> clients = clientDAO.getByFilter(ClientDAO.Filter.builder().build());

        assertEquals(clients.stream().map(Client::getFirstName).collect(Collectors.toSet()),
                createdClients.stream().map(Client::getFirstName).collect(Collectors.toSet()));
    }
    
    @Test
    void getByFilter() {
        ClientDAO.Filter criteria = clientDAO.getFilterBuilder()
                .firstName("Федор")
                .build();

        List<Client> clients = clientDAO.getByFilter(criteria);
        assertEquals(clients.size(), 1);
        assertEquals(clients.getFirst().getPhoneNumber(), "+812312341234");

        criteria = clientDAO.getFilterBuilder()
                .firstName("иван")
                .build();

        Set<String> expectedEmails = new HashSet<>();
        expectedEmails.add("email@email.com");
        expectedEmails.add("email2@email.com");

        clients = clientDAO.getByFilter(criteria);
        assertEquals(clients.stream().map(Client::getEmail).collect(Collectors.toSet()),
                expectedEmails);
    }

    @Test
    void getByFilterPaginated() {
        ClientDAO.Filter empty = clientDAO.getFilterBuilder().build();

        List<Client> page = clientDAO.getByFilterPaginated(empty, 5, 0);
        assertEquals(page.size(), 5);

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
            session.createNativeQuery("TRUNCATE client RESTART IDENTITY", Object.class).executeUpdate();
            session.getTransaction().commit();
        }
    }
}