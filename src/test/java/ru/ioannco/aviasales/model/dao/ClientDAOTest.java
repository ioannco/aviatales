package ru.ioannco.aviasales.model.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.w3c.dom.ranges.Range;
import ru.ioannco.aviasales.model.entity.Client;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
class ClientDAOTest {
    @Autowired
    ClientDAO clientDAO;

    @BeforeEach
    void setUp() {
        List<Client> clients = new ArrayList<>();

        clients.add(new Client("Иван", "Сергеевич", "Черемисенов", "+79969763732", "email@email.com", "Кремль дом 1"));
        for (int i = 0; i < 20; ++i) {
            clients.add(
                    new Client("Sample" + i, "","","","","")
            );
        }

        clientDAO.saveAll(clients);
    }

    @AfterEach
    void tearDown() {
    }
}