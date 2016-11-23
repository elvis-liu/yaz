package com.exmertec.yaz.query;

import com.exmertec.yaz.core.Query;
import com.exmertec.yaz.expression.ExpressionGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class ComplexQueryBase<T> implements Query {
    private final String field;
    private final List<T> values;

    protected ComplexQueryBase(String field, T... values) {
        this.field = field;
        this.values = Arrays.asList(values);
    }

    public final List<Predicate> toRestrictions(final CriteriaBuilder criteriaBuilder,
                                                final AbstractQuery<?> abstractQuery) {
        if (values.isEmpty()) {
            return new ArrayList<>();
        }

        List<Expression<T>> expressions = values.stream()
            .map(input -> toExpression(criteriaBuilder, abstractQuery, input))
            .collect(Collectors.toList());

        Root<?> entity = abstractQuery.getRoots().iterator().next();
        return doGenerate(criteriaBuilder, entity, field, expressions);
    }

    private Expression<T> toExpression(CriteriaBuilder criteriaBuilder, CommonAbstractCriteria criteria, T value) {
        if (value != null && ExpressionGenerator.class.isInstance(value)) {
            ExpressionGenerator expressionGenerator = ExpressionGenerator.class.cast(value);
            return expressionGenerator.generate(criteriaBuilder, criteria);
        } else {
            return valueToExpress(criteriaBuilder, value);
        }
    }

    protected Expression<T> valueToExpress(CriteriaBuilder criteriaBuilder, T input) {
        return criteriaBuilder.literal(input);
    }

    protected abstract List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field,
                                                  Iterable<Expression<T>> expressions);

    @Override
    public String toString() {
        return "ComplexRestrictionGeneratorBase{"
            + "field='" + field + '\''
            + ", values=" + values
            + '}';
    }
}
