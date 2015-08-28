package com.exmertec.yaz.query;

import com.exmertec.yaz.core.Query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public class EmptyQuery implements Query {
    @Override
    public List<Predicate> toRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> abstractQuery,
                                          From entity) {
        return new ArrayList<>();
    }
}
