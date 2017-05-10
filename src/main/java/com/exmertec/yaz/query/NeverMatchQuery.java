package com.exmertec.yaz.query;

import com.exmertec.yaz.core.Query;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class NeverMatchQuery implements Query {
    @Override
    public List<Predicate> toRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> abstractQuery, Path<?> path) {
        return Arrays.asList(criteriaBuilder.notEqual(criteriaBuilder.literal(1), 1));
    }
}
