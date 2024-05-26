package ru.ioannco.aviasales;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.ioannco.aviasales.model.entity.Client;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class ReflectionTools {
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
