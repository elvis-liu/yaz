package com.exmertec.yaz.query;

import com.exmertec.yaz.core.Query;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public class BooleanQuery implements Query {
    private final List<Query> queries;
    private Function<Predicate[], Predicate> booleanOperator;

    public BooleanQuery(Function<Predicate[], Predicate> booleanOperator, Query... queries) {
        this.booleanOperator = booleanOperator;
        this.queries = Arrays.asList(queries);
    }

    @Override
    public List<Predicate> toRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> abstractQuery) {
        Predicate[] predicates = queries.stream()
            .map(query -> Optional.of(query.toRestrictions(criteriaBuilder, abstractQuery)).get())
            .flatMap(Collection::stream)
            .toArray(Predicate[]::new);

        return Arrays.asList(booleanOperator.apply(predicates));
    }
}
