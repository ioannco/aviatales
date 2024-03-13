package ru.ioannco.aviasales.model.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.ioannco.aviasales.ReflectionTools;
import ru.ioannco.aviasales.model.dao.BaseDAO;
import ru.ioannco.aviasales.model.entity.BaseEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
abstract class BaseDAOImpl<T extends BaseEntity> implements BaseDAO<T> {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    private final Class<T> typeT = ReflectionTools.getGeneric(getClass(), 0);

    @Override
    public void save(T entity) {
        getSession().persist(entity);
    }

    @Override
    public void saveAll(List<T> entities) {
        entities.forEach(getSession()::persist);
    }

    @Override
    public void update(T entity) {
        getSession().merge(entity);
    }

    @Override
    public void delete(T entity) {
        getSession().remove(entity);
    }

    @Override
    public List<T> getAll() {
        CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(typeT);
        Root<T> root = criteriaQuery.from(typeT);
        criteriaQuery.select(root);
        return getSession().createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Optional<T> getByID(long ID) {
        return Optional.ofNullable(getSession().get(typeT, ID));
    }

    @Override
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<T> root = criteriaQuery.from(typeT);
            criteriaQuery.select(builder.count(root));
            return session.createQuery(criteriaQuery).getResultList().getFirst();
        }
    }
}
