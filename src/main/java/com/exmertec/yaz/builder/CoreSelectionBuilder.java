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
                                                           root -> getCriteriaBuilder().countDistinct(
                                                               root.get(fieldName)));
    }

    @Override
    public Double avg() {
        return criteriaQueryGenerator.doQuerySingleForType(Double.class,
                                                           root -> getCriteriaBuilder().avg(
                                                               root.get(fieldName)));
    }

    @Override
    public <T extends Number> T min(Class<T> targetType) {
        return criteriaQueryGenerator.doQuerySingleForType(targetType,
                                                           root -> getCriteriaBuilder().min(
                                                               root.get(fieldName)));
    }

    @Override
    public <T extends Number> T max(Class<T> targetType) {
        return criteriaQueryGenerator.doQuerySingleForType(targetType,
                                                           root -> getCriteriaBuilder().max(
                                                               root.get(fieldName)));
    }

    @Override
    public <T extends Number> T sum(Class<T> targetType) {
        return criteriaQueryGenerator.doQuerySingleForType(targetType,
                                                           root -> getCriteriaBuilder().sum(
                                                               root.get(fieldName)));
    }

    private CriteriaBuilder getCriteriaBuilder() {
        return criteriaQueryGenerator.getEntityManager().getCriteriaBuilder();
    }
}
