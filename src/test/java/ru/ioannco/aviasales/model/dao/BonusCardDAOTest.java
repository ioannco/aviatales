package ru.ioannco.aviasales.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.ioannco.aviasales.model.entity.Airline;
import ru.ioannco.aviasales.model.entity.BonusCard;
import ru.ioannco.aviasales.model.entity.Client;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class BonusCardDAOTest {

    @Autowired
    private BonusCardDAO bonusCardDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Client client1;
    private Client client2;
    private Airline airline1;
    private Airline airline2;
    private BonusCard bonusCard1;
    private BonusCard bonusCard2;

    @BeforeEach
    void setUp() {
        client1 = new Client("Иван", "Иванович", "Иванов", "+79991234567", "ivanov@example.com", "Адрес 1");
        client2 = new Client("Петр", "Петрович", "Петров", "+79997654321", "petrov@example.com", "Адрес 2");
        airline1 = new Airline("Аэрофлот");
        airline2 = new Airline("Россия");

        bonusCard1 = new BonusCard(client1, airline1, 100);
        bonusCard2 = new BonusCard(client2, airline2, 200);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(client1);
            session.save(client2);
            session.save(airline1);
            session.save(airline2);
            session.save(bonusCard1);
            session.save(bonusCard2);
            session.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE bonus_card RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.createNativeQuery("TRUNCATE client RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.createNativeQuery("TRUNCATE airline RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void testGetByFilter() {
        BonusCardDAO.Filter filter = bonusCardDAO.getFilterBuilder()
                .client(client1)
                .build();

        List<BonusCard> bonusCards = bonusCardDAO.getByFilter(filter);

        assertEquals(1, bonusCards.size());
        assertEquals(client1, bonusCards.get(0).getClient());
    }

    @Test
    void testGetByFilterWithPagination() {
        BonusCardDAO.Filter filter = bonusCardDAO.getFilterBuilder()
                .client(client2)
                .build();

        List<BonusCard> bonusCards = bonusCardDAO.getByFilter(filter);

        assertEquals(1, bonusCards.size());
        assertEquals(client2, bonusCards.get(0).getClient());
    }
}