package com.exmertec.yaz.query;

import com.exmertec.yaz.core.Query;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class BooleanQuery implements Query {
    private final List<Query> queries;
    private BooleanOperator booleanOperator;

    public BooleanQuery(BooleanOperator booleanOperator, Query... queries) {
        this.booleanOperator = booleanOperator;
        this.queries = Arrays.asList(queries);
    }

    @Override
    public List<Predicate> toRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> abstractQuery, Path<?> path) {
        Predicate[] predicates = queries.stream()
            .map(query -> query.toRestrictions(criteriaBuilder, abstractQuery, path))
            .flatMap(Collection::stream)
            .toArray(Predicate[]::new);

        return Arrays.asList(booleanOperator.operate(criteriaBuilder, predicates));
    }

    @FunctionalInterface
    public interface BooleanOperator {
        Predicate operate(CriteriaBuilder criteriaBuilder, Predicate[] predicates);
    }
}
