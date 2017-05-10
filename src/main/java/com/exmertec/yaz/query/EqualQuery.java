package com.exmertec.yaz.query;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class EqualQuery extends ComplexQueryBase<Object> {
    public EqualQuery(String field, Object value) {
        super(field, value);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Path<?> path, String field,
                                         Iterable<Expression<Object>> expressions) {
        Expression<?> expression = expressions.iterator().next();
        Predicate restriction;
        if (expression == null) {
            restriction = criteriaBuilder.isNull(path.get(field));
        } else {
            restriction = criteriaBuilder.equal(path.get(field), expression);
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
