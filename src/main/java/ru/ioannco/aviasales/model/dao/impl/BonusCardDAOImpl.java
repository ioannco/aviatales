package ru.ioannco.aviasales.model.dao.impl;

import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.ioannco.aviasales.model.dao.BonusCardDAO;
import ru.ioannco.aviasales.model.entity.BonusCard;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class BonusCardDAOImpl extends BaseDAOImpl<BonusCard> implements BonusCardDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<BonusCard> getByFilter(Filter filter) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<BonusCard> criteriaQuery = builder.createQuery(BonusCard.class);
        Root<BonusCard> root = criteriaQuery.from(BonusCard.class);
        List<Predicate> predicates = new ArrayList<>();

        // Добавление предикатов фильтрации
        if (filter.getClient() != null) {
            predicates.add(builder.equal(root.get("client"), filter.getClient()));
        }

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }
}