package ru.ioannco.aviasales.model.dao.impl;

import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.ioannco.aviasales.model.dao.FlightDAO;
import ru.ioannco.aviasales.model.entity.Airline;
import ru.ioannco.aviasales.model.entity.Flight;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class FlightDAOImpl extends BaseDAOImpl<Flight> implements FlightDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Flight> getByFilter(Filter filter, int pageSize, int pageNumber, SortOrder sortOrder) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Flight> criteriaQuery = builder.createQuery(Flight.class);
        Root<Flight> root = criteriaQuery.from(Flight.class);
        List<Predicate> predicates = buildPredicates(filter, builder, root);

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
    public long countByFilter(Filter filter) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<Flight> root = criteriaQuery.from(Flight.class);
        List<Predicate> predicates = buildPredicates(filter, builder, root);

        criteriaQuery.select(builder.count(root)).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }

    private List<Predicate> buildPredicates(Filter filter, CriteriaBuilder builder, Root<Flight> root) {
        List<Predicate> predicates = new ArrayList<>();

        // Join для авиалинии
        if (filter.getAirline() != null) {
            Join<Flight, Airline> airlineJoin = root.join("airline");
            predicates.add(builder.like(builder.lower(airlineJoin.get("verbose")), "%" + filter.getAirline().toLowerCase() + "%"));
        }

        // Добавление предикатов фильтрации
        if (filter.getDepartureDate() != null) {
            Date startOfDay = getStartOfDay(filter.getDepartureDate());
            Date endOfDay = getEndOfDay(filter.getDepartureDate());
            predicates.add(builder.between(root.get("departureDateTime"), startOfDay, endOfDay));
        }
        if (filter.getArrivalDate() != null) {
            Date startOfDay = getStartOfDay(filter.getArrivalDate());
            Date endOfDay = getEndOfDay(filter.getArrivalDate());
            predicates.add(builder.between(root.get("arrivalDateTime"), startOfDay, endOfDay));
        }
        if (filter.getDepartureCity() != null) {
            predicates.add(builder.like(builder.lower(root.get("departureAirport").get("city").get("verbose")), "%" + filter.getDepartureCity().toLowerCase() + "%"));
        }
        if (filter.getArrivalCity() != null) {
            predicates.add(builder.like(builder.lower(root.get("arrivalAirport").get("city").get("verbose")), "%" + filter.getArrivalCity().toLowerCase() + "%"));
        }
        if (filter.getDepartureAirport() != null) {
            predicates.add(builder.like(builder.lower(root.get("departureAirport").get("verbose")), "%" + filter.getDepartureAirport().toLowerCase() + "%"));
        }
        if (filter.getArrivalAirport() != null) {
            predicates.add(builder.like(builder.lower(root.get("arrivalAirport").get("verbose")), "%" + filter.getArrivalAirport().toLowerCase() + "%"));
        }
        if (filter.getFlightNumber() != null) {
            predicates.add(builder.like(builder.lower(root.get("number")), "%" + filter.getFlightNumber().toLowerCase() + "%"));
        }
        if (filter.getPurchasable() != null) {
            if (filter.getPurchasable()) {
                predicates.add(builder.and(
                        builder.greaterThan(root.get("passengerLimit"), root.get("passengerCount")),
                        builder.greaterThan(root.get("departureDateTime"), new Date())
                ));
            }
        }
        if (filter.getAvailableSeats() != null) {
            if (filter.getAvailableSeats()) {
                predicates.add(builder.greaterThan(root.get("passengerLimit"), root.get("passengerCount")));
            }
        }
        if (filter.getMinPrice() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
        }

        return predicates;
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}
