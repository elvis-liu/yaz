package com.exmertec.yaz.core;

import com.exmertec.yaz.query.EmptyQuery;

import java.util.List;

import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public interface Query {
    List<Predicate> toRestrictions(CriteriaBuilder criteriaBuilder, CommonAbstractCriteria criteria);

    default Query when(boolean condition) {
        if (condition) {
            return this;
        } else {
            return new EmptyQuery();
        }
    }
}
