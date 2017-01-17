package com.exmertec.yaz.builder;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

interface CriteriaQueryGenerator<T> extends CriteriaGenerator<T> {
    List<OrderByRule> getOrderByRules();

    default Root<T> getRoot(CriteriaQuery<?> criteriaQuery) {
        Set<Root<?>> roots = criteriaQuery.getRoots();
        if (roots.size() != 1) {
            throw new IllegalStateException("Query contains more than one root entity!");
        }

        return (Root<T>) roots.iterator().next();
    }

    default <R> CriteriaQuery<R> createQuery(Class<R> resultType) {
        CriteriaQuery<R> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(resultType);
        criteriaQuery.from(getProtoType());

        return criteriaQuery;
    }

    default void appendOrderBy(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteria) {
        final Root<?> entity = criteria.getRoots().iterator().next();

        if (!getOrderByRules().isEmpty()) {
            criteria.orderBy(getOrderByRules().stream()
                    .map(rule -> rule.getOrder(criteriaBuilder, entity))
                    .collect(toList()));
        }
    }

    default <R> R doQuerySingleForType(Class<R> resultType, Function<Root<T>, Selection<R>> selectionFunction) {
        CriteriaQuery<R> criteriaQuery = createQuery(resultType);

        criteriaQuery.select(selectionFunction.apply(getRoot(criteriaQuery)));

        Predicate[] generatedRestrictions = generateRestrictions(criteriaQuery, getQueries());

        criteriaQuery.where(generatedRestrictions);
        TypedQuery<R> query = getEntityManager().createQuery(criteriaQuery);
        addQueryOptions(query);

        return query.getSingleResult();
    }

    default <R> List<R> doQueryListWithSelect(String fieldName, Class<R> resultType,
                                              Consumer<TypedQuery<R>> queryManipulator, boolean isDistinct) {
        CriteriaQuery<R> criteriaQuery = createQuery(resultType);
        criteriaQuery.select(getRoot(criteriaQuery).get(fieldName)).distinct(isDistinct);

        appendOrderBy(getEntityManager().getCriteriaBuilder(), criteriaQuery);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaQuery, getQueries());

        criteriaQuery.where(generatedRestrictions);

        TypedQuery<R> query = getEntityManager().createQuery(criteriaQuery);
        if (queryManipulator != null) {
            queryManipulator.accept(query);
        }
        addQueryOptions(query);

        return query.getResultList();
    }

    default List<T> doQueryList(Consumer<TypedQuery<T>> queryManipulator) {
        CriteriaQuery<T> criteriaQuery = createQuery(getProtoType());
        criteriaQuery.select(getRoot(criteriaQuery));

        appendOrderBy(getEntityManager().getCriteriaBuilder(), criteriaQuery);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaQuery, getQueries());

        criteriaQuery.where(generatedRestrictions);

        TypedQuery<T> query = getEntityManager().createQuery(criteriaQuery);
        if (queryManipulator != null) {
            queryManipulator.accept(query);
        }
        addQueryOptions(query);

        return query.getResultList();
    }

    class OrderByRule {
        private boolean isAscending;
        private String fieldName;

        public OrderByRule(boolean isAscending, String fieldName) {
            this.isAscending = isAscending;
            this.fieldName = fieldName;
        }

        public Order getOrder(CriteriaBuilder criteriaBuilder, Root<?> entity) {
            if (isAscending) {
                return criteriaBuilder.asc(entity.get(fieldName));
            } else {
                return criteriaBuilder.desc(entity.get(fieldName));
            }
        }
    }
}
