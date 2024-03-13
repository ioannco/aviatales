package ru.ioannco.aviasales.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.ioannco.aviasales.model.entity.TestEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseDAOTest {
    @Autowired
    TestEntityDAO testEntityDAO;

    @Autowired
    SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        List<TestEntity> entities = new ArrayList<>();

        for (int i = 1; i <= 10; ++i) {
            entities.add(
                    new TestEntity("data" + i)
            );
        }

        testEntityDAO.saveAll(entities);
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE test RESTART IDENTITY", Object.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void getByIdUpdate() {
       TestEntity entity = testEntityDAO.getAll().getFirst();
       long id = entity.getId();

       Optional<TestEntity> byIdOpt = testEntityDAO.getByID(id);
       assertTrue(byIdOpt.isPresent());

       TestEntity byId = byIdOpt.get();
       assertEquals(entity, byId);

       byId.setSampleData("updated");
       testEntityDAO.update(byId);

       Optional<TestEntity> updated = testEntityDAO.getByID(byId.getId());
       assertTrue(updated.isPresent());
       assertEquals(updated.get().getSampleData(), "updated");
    }

    @Test
    void delete() {
        TestEntity entity = testEntityDAO.getAll().getFirst();
        testEntityDAO.delete(entity);

        assertEquals(testEntityDAO.count(), 9);
        assertFalse(testEntityDAO.getByID(entity.getId()).isPresent());
    }

    @Test
    void getAll() {
        Set<String> expectedData = new HashSet<>();

        for (int i = 1; i <= 10; ++i) {
            expectedData.add("data" + i);
        }

        List<TestEntity> data = testEntityDAO.getAll();

        assertEquals(data.stream().map(TestEntity::getSampleData).collect(Collectors.toSet()), expectedData);
    }

    @Test
    void count() {
        long count = testEntityDAO.count();
        assertEquals(count, 10);
    }
}