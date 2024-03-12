package ru.ioannco.aviasales.model.dao;

import org.hibernate.Session;
import ru.ioannco.aviasales.model.entity.BaseEntity;
import ru.ioannco.aviasales.model.entity.Client;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BaseDAO <T extends BaseEntity> {
    Session getSession();
    void save(T entity);
    void saveAll(List<T> entities);
    void update(T entity);
    void delete(T entity);
    List<T> getAll();
    Optional<T> getByID(long ID);
    long count();
}
