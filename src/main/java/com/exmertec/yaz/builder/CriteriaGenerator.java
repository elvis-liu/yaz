package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

interface CriteriaGenerator<T> {

    Class<T> getProtoType();

    EntityManager getEntityManager();

    List<Query> getQueries();

    LockModeType getLockMode();

    default void addQueryOptions(javax.persistence.Query query) {
        if (getLockMode() != null) {
            query.setLockMode(getLockMode());
        }
    }

    default Predicate[] generateRestrictions(CommonAbstractCriteria abstractCriteria, List<Query> queries) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        return queries.stream()
            .map(query -> Optional.of(query.toRestrictions(criteriaBuilder, abstractCriteria)).get())
            .flatMap(Collection::stream)
            .toArray(Predicate[]::new);
    }
}
