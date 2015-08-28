package com.exmertec.yaz.query;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class NumberCompareQuery<T extends Number> extends ComplexQueryBase<T> {
    private ExpressionEvaluator evaluator;

    public NumberCompareQuery(String fieldName, T number, ExpressionEvaluator evaluator) {
        super(fieldName, number);
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
        Predicate evaluate(CriteriaBuilder criteriaBuilder,
                           Path<? extends Number> path, Expression<? extends Number> expression);
    }
}
