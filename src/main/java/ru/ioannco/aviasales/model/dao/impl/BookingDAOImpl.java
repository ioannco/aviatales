package ru.ioannco.aviasales.model.dao.impl;

import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.ioannco.aviasales.model.dao.BookingDAO;
import ru.ioannco.aviasales.model.entity.Booking;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class BookingDAOImpl extends BaseDAOImpl<Booking> implements BookingDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Booking> getByFilter(Filter filter, int pageSize, int pageNumber, SortOrder sortOrder) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Booking> criteriaQuery = builder.createQuery(Booking.class);
        Root<Booking> root = criteriaQuery.from(Booking.class);
        List<Predicate> predicates = new ArrayList<>();

        // Добавление предикатов фильтрации
        if (filter.getClient() != null) {
            predicates.add(builder.equal(root.get("client"), filter.getClient()));
        }

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));

        // Добавление сортировки
        if (sortOrder != null) {
            criteriaQuery.orderBy(sortOrder.isAscending() ? builder.asc(root.get(sortOrder.getField())) : builder.desc(root.get(sortOrder.getField())));
        }

        return session.createQuery(criteriaQuery)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }
}