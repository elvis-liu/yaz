package com.exmertec.yaz.query;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ComparableBetweenQuery<T extends Comparable<? super T>> extends ComplexQueryBase<T> {
    public ComparableBetweenQuery(String field, T value1, T value2) {
        super(field, value1, value2);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field,
                                         Iterable<Expression<T>> expressions) {
        Iterator<Expression<T>> iterator = expressions.iterator();
        Expression<T> expression1 = iterator.next();
        Expression<T> expression2 = iterator.next();
        return Arrays.asList(criteriaBuilder.between(entity.<T>get(field), expression1, expression2));
    }
}
