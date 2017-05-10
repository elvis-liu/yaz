package com.exmertec.yaz.query;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class NotEqualQuery extends ComplexQueryBase<Object> {
    public NotEqualQuery(String field, Object value) {
        super(field, value);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Path<?> path, String field,
                                         Iterable<Expression<Object>> expressions) {
        Expression<?> expression = expressions.iterator().next();
        Predicate restriction;
        if (expression == null) {
            restriction = criteriaBuilder.isNotNull(path.get(field));
        } else {
            restriction = criteriaBuilder.notEqual(path.get(field), expression);
        }

        return Arrays.asList(restriction);
    }

    @Override
    protected Expression<Object> valueToExpress(CriteriaBuilder criteriaBuilder, Object input) {
        if (input == null) {
            return null;
        } else {
            return super.valueToExpress(criteriaBuilder, input);
        }
    }
}
