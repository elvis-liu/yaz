package com.exmertec.yaz.query;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class LikeQuery extends ComplexQueryBase<String> {
    public LikeQuery(String field, String value) {
        super(field, value);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field,
                                         Iterable<Expression<String>> values) {
        Expression<String> expression = values.iterator().next();
        return Arrays.asList(criteriaBuilder.like(entity.<String>get(field), expression));
    }

    @Override
    protected Expression<String> valueToExpress(CriteriaBuilder criteriaBuilder, String input) {
        return super.valueToExpress(criteriaBuilder, "%" + input + "%");
    }
}
