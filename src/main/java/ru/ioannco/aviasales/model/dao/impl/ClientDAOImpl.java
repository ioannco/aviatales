package ru.ioannco.aviasales.model.dao.impl;

import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.ioannco.aviasales.model.dao.ClientDAO;
import ru.ioannco.aviasales.model.entity.Booking;
import ru.ioannco.aviasales.model.entity.Client;
import ru.ioannco.aviasales.model.entity.Flight;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ClientDAOImpl extends BaseDAOImpl<Client> implements ClientDAO {
    @Override
    public List<Client> getByFilter(Filter filter) {
        CriteriaQuery<Client> criteriaQuery = createFilterCriteriaQuery(filter);
        return getSession().createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<Client> getByFilterPaginated(Filter filter, int pageSize, int pageNumber) {
        CriteriaQuery<Client> criteriaQuery = createFilterCriteriaQuery(filter);
        return getSession().createQuery(criteriaQuery)
                .setMaxResults(pageSize)
                .setFirstResult(pageSize * pageNumber)
                .getResultList();
    }

    private CriteriaQuery<Client> createFilterCriteriaQuery(Filter filter) {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = builder.createQuery(Client.class);
        Root<Client> root = criteriaQuery.from(Client.class);
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getFirstName() != null) {
            predicates.add(getLike(builder, root, "firstName", filter.getFirstName()));
        }
        if (filter.getMiddleName() != null) {
            predicates.add(getLike(builder, root, "middleName", filter.getMiddleName()));
        }
        if (filter.getLastName() != null) {
            predicates.add(getLike(builder, root, "lastName", filter.getLastName()));
        }
        if (filter.getPhoneNumber() != null) {
            predicates.add(getLike(builder, root, "phoneNumber", filter.getPhoneNumber()));
        }
        if (filter.getEmail() != null) {
            predicates.add(getLike(builder, root, "email", filter.getEmail()));
        }
        if (filter.getAddress() != null) {
            predicates.add(getLike(builder, root, "address", filter.getAddress()));
        }

        // Если используются join'ы, добавляем предикаты для них
        if (filter.getFlightNo() != null || filter.getFlightPayed() != null) {
            Root<Booking> bookingRoot = criteriaQuery.from(Booking.class);
            Join<Booking, Client> clientJoin = bookingRoot.join("client");
            Join<Booking, Flight> flightJoin = bookingRoot.join("flight");

            if (filter.getFlightNo() != null) {
                predicates.add(builder.like(builder.lower(flightJoin.get("number")), getPattern(filter.getFlightNo().toLowerCase())));
            }
            if (filter.getFlightPayed() != null) {
                predicates.add(builder.equal(bookingRoot.get("isPaid"), filter.getFlightPayed()));
            }

            criteriaQuery.select(clientJoin).distinct(true).where(predicates.toArray(new Predicate[0]));
        } else {
            criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        }

        return criteriaQuery;
    }


    @Override
    public Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }

    private static String getPattern(String name) {
        return "%" + name + "%";
    }

    private static Predicate getLike(CriteriaBuilder builder, Root<Client> root, String fieldName, String field) {
        return builder.like(builder.lower(root.get(fieldName)), String.format("%%%s%%", field.toLowerCase()));
    }
}
