package ru.ioannco.aviasales;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.ioannco.aviasales.model.dao.ClientDAO;
import ru.ioannco.aviasales.model.entity.Client;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class ReflectionTools {
    public static <T> Optional<Predicate> filterFieldToLikePredicate(CriteriaBuilder builder, Root<Client> root, Field filterField, T filter) {
        try {
            if (!filterField.getType().isAssignableFrom(String.class))
                return Optional.empty();
            filterField.setAccessible(true);
            String field = (String) filterField.get(filter);
            String fieldName = filterField.getName();
            if (field != null && !field.isEmpty())
                return Optional.of(builder.like(builder.lower(root.get(fieldName)), String.format("%%%s%%", field.toLowerCase())));
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getGeneric(Class<?> clazz, int paramNum) {
        Type param = ((ParameterizedType) clazz.getGenericSuperclass())
                .getActualTypeArguments()[paramNum];

        if (param instanceof ParameterizedType)
            return (Class<T>) ((ParameterizedType) param).getRawType();
        else if (param instanceof Class<?>)
            return (Class<T>) param;
        else
            throw new IllegalArgumentException("Generic superclass of clazz does not declare type arguments");
    }
}
