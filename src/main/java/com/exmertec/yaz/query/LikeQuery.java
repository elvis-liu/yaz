package com.exmertec.yaz.query;

import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;



public class LikeQuery extends ComplexQueryBase<String> {

    private boolean isFullFuzzy ;

    public LikeQuery(String field, String value, Boolean isFullFuzzy) {
        super(field, value);
        this.isFullFuzzy = isFullFuzzy;
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field,
                                         Iterable<Expression<String>> values) {
        Expression<String> expression = values.iterator().next();
        try {
            if (String.class.equals(entity.getJavaType().getDeclaredField(field).getType())) {
                return Arrays.asList(criteriaBuilder.like(entity.get(field), expression));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return Arrays.asList(criteriaBuilder.like(entity.get(field).as(String.class), expression));
    }

    @Override
    protected Expression<String> valueToExpress(CriteriaBuilder criteriaBuilder, String input) {
        if (isFullFuzzy) {
            return super.valueToExpress(criteriaBuilder, "%" + input + "%");
        } else {
            return super.valueToExpress(criteriaBuilder, input);
        }
    }
}
