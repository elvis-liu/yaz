package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.JoinQueryBuilder;
import com.exmertec.yaz.core.Query;

import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public class CoreJoinQueryBuilder implements JoinQueryBuilder {
    private String joinByField;
    private final FieldQueryBuilder fieldQueryBuilder;

    public CoreJoinQueryBuilder(String joinByField, String fieldName) {
        this.joinByField = joinByField;
        fieldQueryBuilder = new FieldQueryBuilder(fieldName);
    }

    @Override
    public Query eq(Object value) {
        return wrap(fieldQueryBuilder.eq(value));
    }

    @Override
    public Query ne(Object value) {
        return wrap(fieldQueryBuilder.ne(value));
    }

    @Override
    public Query like(String value) {
        return wrap(fieldQueryBuilder.like(value));
    }

    @Override
    public Query in(Object... values) {
        return wrap(fieldQueryBuilder.in(values));
    }

    @Override
    public <T> Query in(Collection<T> values) {
        return wrap(fieldQueryBuilder.in(values));
    }

    @Override
    public Query nin(Object... values) {
        return wrap(fieldQueryBuilder.nin(values));
    }

    @Override
    public <T> Query nin(Collection<T> values) {
        return wrap(fieldQueryBuilder.nin(values));
    }

    @Override
    public <T extends Comparable<? super T>> Query between(T value1, T value2) {
        return wrap(fieldQueryBuilder.between(value1, value2));
    }

    @Override
    public <T extends Comparable<? super T>> Query gtComparable(T value) {
        return wrap(fieldQueryBuilder.gtComparable(value));
    }

    @Override
    public <T extends Number> Query gt(T number) {
        return wrap(fieldQueryBuilder.gt(number));
    }

    @Override
    public <T extends Comparable<? super T>> Query ltComparable(T value) {
        return wrap(fieldQueryBuilder.ltComparable(value));
    }

    @Override
    public <T extends Number> Query lt(T number) {
        return wrap(fieldQueryBuilder.lt(number));
    }

    @Override
    public <T extends Comparable<? super T>> Query gteComparable(T value) {
        return wrap(fieldQueryBuilder.gteComparable(value));
    }

    @Override
    public <T extends Number> Query gte(T number) {
        return wrap(fieldQueryBuilder.gte(number));
    }

    @Override
    public <T extends Comparable<? super T>> Query lteComparable(T value) {
        return wrap(fieldQueryBuilder.lteComparable(value));
    }

    @Override
    public <T extends Number> Query lte(T number) {
        return wrap(fieldQueryBuilder.lte(number));
    }

    private Query wrap(Query query) {
        return new QueryWithJoinPoint(joinByField, query);
    }

    private static final class QueryWithJoinPoint implements Query {
        private final String joinByField;
        private final Query query;

        public QueryWithJoinPoint(String joinByField, Query query) {
            this.joinByField = joinByField;
            this.query = query;
        }

        @Override
        public List<Predicate> toRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> abstractQuery,
                                              From entity) {
            return query.toRestrictions(criteriaBuilder, abstractQuery, entity.join(joinByField));
        }
    }
}
