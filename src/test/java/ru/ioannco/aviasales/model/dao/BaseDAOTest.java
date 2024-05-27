package ru.ioannco.aviasales.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.ioannco.aviasales.model.entity.TestEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class BaseDAOTest {

    @Autowired
    private BaseDAO<TestEntity> testEntityDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private TestEntity entity1;
    private TestEntity entity2;

    @BeforeEach
    void setUp() {
        entity1 = new TestEntity("Test1");
        entity2 = new TestEntity("Test2");

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(entity1);
            session.save(entity2);
            session.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE test_entity RESTART IDENTITY CASCADE", Object.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void testSave() {
        TestEntity entity = new TestEntity("Test3");
        testEntityDAO.save(entity);

        Optional<TestEntity> retrievedEntity = testEntityDAO.getByID(entity.getId());
        assertTrue(retrievedEntity.isPresent());
        assertEquals("Test3", retrievedEntity.get().getSampleData());
    }

    @Test
    void testSaveAll() {
        TestEntity entity3 = new TestEntity("Test3");
        TestEntity entity4 = new TestEntity("Test4");
        testEntityDAO.saveAll(List.of(entity3, entity4));

        List<TestEntity> entities = testEntityDAO.getAll();
        assertEquals(4, entities.size());
    }

    @Test
    void testUpdate() {
        entity1.setSampleData("UpdatedTest1");
        testEntityDAO.update(entity1);

        Optional<TestEntity> retrievedEntity = testEntityDAO.getByID(entity1.getId());
        assertTrue(retrievedEntity.isPresent());
        assertEquals("UpdatedTest1", retrievedEntity.get().getSampleData());
    }

    @Test
    void testDelete() {
        testEntityDAO.delete(entity1);

        Optional<TestEntity> retrievedEntity = testEntityDAO.getByID(entity1.getId());
        assertFalse(retrievedEntity.isPresent());
    }

    @Test
    void testGetAll() {
        List<TestEntity> entities = testEntityDAO.getAll();
        assertEquals(2, entities.size());
    }

    @Test
    void testGetByID() {
        Optional<TestEntity> retrievedEntity = testEntityDAO.getByID(entity1.getId());
        assertTrue(retrievedEntity.isPresent());
        assertEquals("Test1", retrievedEntity.get().getSampleData());
    }

    @Test
    void testCount() {
        long count = testEntityDAO.count();
        assertEquals(2, count);
    }
}