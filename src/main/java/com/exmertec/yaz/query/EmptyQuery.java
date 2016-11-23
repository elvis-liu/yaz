package com.exmertec.yaz.query;

import com.exmertec.yaz.core.Query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public class EmptyQuery implements Query {
    @Override
    public List<Predicate> toRestrictions(CriteriaBuilder criteriaBuilder, CommonAbstractCriteria criteria) {
        return new ArrayList<>();
    }
}
