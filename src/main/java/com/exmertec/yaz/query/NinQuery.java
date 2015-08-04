package com.exmertec.yaz.query;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class NinQuery extends ComplexQueryBase<Object> {
    public NinQuery(String field, Object... values) {
        super(field, values);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Object>> expressions) {
        Expression[] expressionArray = StreamSupport.stream(expressions.spliterator(), false).toArray(Expression[]::new);
        return Arrays.asList(criteriaBuilder.not(entity.get(field).in(expressionArray)));
    }
}
