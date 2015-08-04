package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.SelectionBuilder;

import javax.persistence.criteria.CriteriaBuilder;

public class CoreSelectionBuilder<T> implements SelectionBuilder {
    private final CriteriaQueryGenerator<T> criteriaQueryGenerator;
    private final String fieldName;

    public CoreSelectionBuilder(CriteriaQueryGenerator<T> criteriaQueryGenerator, String fieldName) {
        this.criteriaQueryGenerator = criteriaQueryGenerator;
        this.fieldName = fieldName;
    }

    @Override
    public Long distinctCount() {
        return criteriaQueryGenerator.doQuerySingleForType(Long.class,
                                                           root -> getCriteriaBuilder().countDistinct(root.get(fieldName)));
    }

    private CriteriaBuilder getCriteriaBuilder() {
        return criteriaQueryGenerator.getEntityManager().getCriteriaBuilder();
    }
}
