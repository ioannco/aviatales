package ru.ioannco.aviasales.model.dao;

import ru.ioannco.aviasales.model.entity.Client;

import java.util.List;

public interface ClientDAO {
    void save(Client client);
    void update(Client client);
    void delete(Client client);
    List<Client> getAll();
    List<Client> getByFilter(String firstName, String lastName, String middleName, String phoneNumber, String email, String address);
}
