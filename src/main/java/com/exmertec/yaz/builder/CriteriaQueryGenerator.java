package com.exmertec.yaz.builder;

import static java.util.stream.Collectors.toList;

import com.exmertec.yaz.core.Query;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

interface CriteriaQueryGenerator<T> {
    Class<T> getProtoType();

    LockModeType getLockMode();

    List<OrderByRule> getOrderByRules();

    List<Query> getQueries();

    EntityManager getEntityManager();

    default Predicate[] generateRestrictions(AbstractQuery<?> abstractQuery, List<Query> queries, Path<T> path) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        return queries.stream()
            .map(query -> query.toRestrictions(criteriaBuilder, abstractQuery, path))
            .flatMap(Collection::stream)
            .toArray(Predicate[]::new);
    }

    default void addQueryOptions(TypedQuery<?> query) {
        if (getLockMode() != null) {
            query.setLockMode(getLockMode());
        }
    }

    default void appendOrderBy(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteria) {
        final Path<?> entity = criteria.getRoots().iterator().next();

        if (!getOrderByRules().isEmpty()) {
            criteria.orderBy(getOrderByRules().stream()
                    .map(rule -> rule.getOrder(criteriaBuilder, entity))
                    .collect(toList()));
        }
    }

    default <R> R doQuerySingleForType(Class<R> resultType, Function<Root<T>, Selection<R>> selectionFunction) {
        CriteriaQuery<R> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(resultType);
        Root<T> root = criteriaQuery.from(getProtoType());

        criteriaQuery.select(selectionFunction.apply(root));

        Predicate[] generatedRestrictions = generateRestrictions(criteriaQuery, getQueries(), root);

        criteriaQuery.where(generatedRestrictions);
        TypedQuery<R> query = getEntityManager().createQuery(criteriaQuery);
        addQueryOptions(query);

        return query.getSingleResult();
    }

    default <R> List<R> doQueryListWithSelect(String fieldName, Class<R> resultType,
                                              Consumer<TypedQuery<R>> queryManipulator, boolean isDistinct) {
        CriteriaQuery<R> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(resultType);
        Root<T> root = criteriaQuery.from(getProtoType());

        criteriaQuery.select(root.get(fieldName)).distinct(isDistinct);

        appendOrderBy(getEntityManager().getCriteriaBuilder(), criteriaQuery);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaQuery, getQueries(), root);

        criteriaQuery.where(generatedRestrictions);

        TypedQuery<R> query = getEntityManager().createQuery(criteriaQuery);
        if (queryManipulator != null) {
            queryManipulator.accept(query);
        }
        addQueryOptions(query);

        return query.getResultList();
    }

    default List<T> doQueryList(Consumer<TypedQuery<T>> queryManipulator) {
        CriteriaQuery<T> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(getProtoType());
        Root<T> root = criteriaQuery.from(getProtoType());

        criteriaQuery.select(root);

        appendOrderBy(getEntityManager().getCriteriaBuilder(), criteriaQuery);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaQuery, getQueries(), root);

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

        public Order getOrder(CriteriaBuilder criteriaBuilder, Path<?> entity) {
            if (isAscending) {
                return criteriaBuilder.asc(entity.get(fieldName));
            } else {
                return criteriaBuilder.desc(entity.get(fieldName));
            }
        }
    }
}
