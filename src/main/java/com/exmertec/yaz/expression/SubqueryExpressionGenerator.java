package com.exmertec.yaz.expression;

import com.exmertec.yaz.core.Query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

public class SubqueryExpressionGenerator<F, T> implements ExpressionGenerator<T> {
    private final Class<F> fromType;
    private final String targetField;
    private final Class<T> targetFieldType;
    private final Iterable<Query> byRestrictions;

    private SubqueryExpressionGenerator(Class<F> fromType, String targetField, Class<T> targetFieldType,
                                        Iterable<Query> byRestrictions) {
        this.fromType = fromType;
        this.targetField = targetField;
        this.targetFieldType = targetFieldType;
        this.byRestrictions = byRestrictions;
    }

    @Override
    public Expression<T> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        Subquery<T> subquery = query.subquery(targetFieldType);
        Root<?> entity = subquery.from(fromType);
        Predicate[] restrictions = generateRestrictions(criteriaBuilder, subquery, byRestrictions);
        return subquery.select(entity.<T>get(targetField)).where(restrictions);
    }

    public static ExpressionGenerator subquery(Class<?> fromType, String targetField, Query... queries) {
        return new SubqueryExpressionGenerator(fromType, targetField, getField(fromType, targetField),
                                               Arrays.asList(queries));
    }

    private static Class<?> getField(Class<?> fromClass, String targetField) {
        for (Class<?> cls = fromClass; cls != null; cls = cls.getSuperclass()) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(targetField)) {
                    return field.getDeclaringClass();
                }
            }
        }

        throw new RuntimeException("No field " + targetField + " on class " + fromClass);
    }

    private static Predicate[] generateRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query,
                                                    Iterable<Query> generators) {
        return StreamSupport.stream(generators.spliterator(), false)
            .map(generator -> Optional.of(generator.toRestrictions(criteriaBuilder, query)).orElseGet(ArrayList::new))
            .flatMap(restrictions -> restrictions.stream())
            .toArray(Predicate[]::new);
    }
}
