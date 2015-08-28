package com.exmertec.yaz.query;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class ComparableCompareQuery<T extends Comparable<? super T>> extends ComplexQueryBase<T> {
    private ExpressionEvaluator evaluator;

    public ComparableCompareQuery(String fieldName, T comparable, ExpressionEvaluator evaluator) {
        super(fieldName, comparable);
        this.evaluator = evaluator;
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, From entity, String field,
                                         Iterable<Expression<T>> expressions) {
        Expression<T> expression = expressions.iterator().next();
        return Arrays.asList(evaluator.evaluate(criteriaBuilder, entity.<T>get(field), expression));
    }

    @FunctionalInterface
    public interface ExpressionEvaluator {
        <T extends Comparable<? super T>> Predicate evaluate(CriteriaBuilder criteriaBuilder,
                                                             Path<T> path, Expression<T> expression);
    }
}
