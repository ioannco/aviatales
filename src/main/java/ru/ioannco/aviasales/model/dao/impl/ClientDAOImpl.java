package ru.ioannco.aviasales.model.dao.impl;

import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.ioannco.aviasales.model.dao.ClientDAO;
import ru.ioannco.aviasales.model.entity.Client;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ClientDAOImpl implements ClientDAO {
    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Client client) {
        getSession().persist(client);
    }

    @Override
    public void update(Client client) {
        getSession().merge(client);
    }

    @Override
    public void delete(Client client) {
        getSession().remove(client);
    }

    @Override
    public List<Client> getAll() {
        CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = criteriaBuilder.createQuery(Client.class);
        Root<Client> root = criteriaQuery.from(Client.class);
        criteriaQuery.select(root);
        return getSession().createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<Client> getByFilter(String firstName, String middleName, String lastName, String phoneNumber, String email, String address) {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = builder.createQuery(Client.class);
        Root<Client> root = criteriaQuery.from(Client.class);

        List<Predicate> predicates = new ArrayList<>();

        addLikePredicate(predicates, builder, root.get("firstName"), firstName);
        addLikePredicate(predicates, builder, root.get("middleName"), middleName);
        addLikePredicate(predicates, builder, root.get("lastName"), lastName);
        addLikePredicate(predicates, builder, root.get("phoneNumber"), phoneNumber);
        addLikePredicate(predicates, builder, root.get("email"), email);
        addLikePredicate(predicates, builder, root.get("address"), address);

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));

        return getSession().createQuery(criteriaQuery).getResultList();
    }

        private void addLikePredicate(List<Predicate> predicates, CriteriaBuilder builder, Expression<String> fieldPath, String field) {
        if (field != null && !field.isEmpty()) {
            predicates.add(builder.like(builder.lower(fieldPath), String.format("%%%s%%", field.toLowerCase())));
        }
    }
}
