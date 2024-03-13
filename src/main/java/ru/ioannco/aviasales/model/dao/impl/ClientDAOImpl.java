package ru.ioannco.aviasales.model.dao.impl;

import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
import org.springframework.stereotype.Repository;
import ru.ioannco.aviasales.ReflectionTools;
import ru.ioannco.aviasales.model.dao.ClientDAO;
import ru.ioannco.aviasales.model.entity.Client;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ClientDAOImpl extends BaseDAOImpl<Client> implements ClientDAO {
    @Override
    public List<Client> getByFilter(Filter filter) {
        CriteriaQuery<Client> criteriaQuery = createAllCriteriaQuery(filter);
        return getSession().createQuery(criteriaQuery).getResultList();
    }


    private List<Predicate> constructLikePredicates(CriteriaBuilder builder, Root<Client> root, Filter filter) {
        return Arrays.stream(filter.getClass().getDeclaredFields())
                .map(field -> ReflectionTools.filterFieldToLikePredicate(builder, root, field, filter))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<Client> getByFilterPaginated(Filter filter, int pageSize, int pageNumber) {
        CriteriaQuery<Client> criteriaQuery = createAllCriteriaQuery(filter);
        return getSession().createQuery(criteriaQuery)
                .setMaxResults(pageSize)
                .setFirstResult(pageSize * pageNumber)
                .getResultList();
    }

    private CriteriaQuery<Client> createAllCriteriaQuery(Filter filter) {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = builder.createQuery(Client.class);
        Root<Client> root = criteriaQuery.from(Client.class);
        List<Predicate> predicates = constructLikePredicates(builder, root, filter);
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        return criteriaQuery;
    }

    @Override
    public Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }
}
