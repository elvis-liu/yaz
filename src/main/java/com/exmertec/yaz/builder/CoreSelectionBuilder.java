package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.SelectionBuilder;

import javax.persistence.criteria.CriteriaBuilder;

public class CoreSelectionBuilder<T> implements SelectionBuilder {
    private CriteriaQueryGenerator<T> criteriaQueryGenerator;

    public CoreSelectionBuilder(CriteriaQueryGenerator<T> criteriaQueryGenerator) {
        this.criteriaQueryGenerator = criteriaQueryGenerator;
    }

    @Override
    public Long count(String fieldName) {
        CriteriaBuilder criteriaBuilder = criteriaQueryGenerator.getEntityManager().getCriteriaBuilder();
        return criteriaQueryGenerator.doQuerySingleForType(Long.class, criteriaBuilder::count);
    }
}
