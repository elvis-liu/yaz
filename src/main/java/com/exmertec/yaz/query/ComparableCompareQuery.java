package com.exmertec.yaz.query;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ComparableCompareQuery<T extends Comparable<? super T>> extends ComplexQueryBase<T> {
    private ExpressionEvaluator expressionEvaluator;

    public ComparableCompareQuery(String fieldName, T comparable, ExpressionEvaluator expressionEvaluator) {
        super(fieldName, comparable);
        this.expressionEvaluator = expressionEvaluator;
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field,
                                         Iterable<Expression<T>> expressions) {
        Expression<T> expression = expressions.iterator().next();
        return Arrays.asList(expressionEvaluator.evaluate(entity.<T>get(field), expression));
    }

    @FunctionalInterface
    public interface ExpressionEvaluator {
        <T extends Comparable<? super T>> Predicate evaluate(Path<T> path, Expression<T> expression);
    }
}
