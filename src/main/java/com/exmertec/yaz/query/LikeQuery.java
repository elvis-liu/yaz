package com.exmertec.yaz.query;

import com.exmertec.yaz.util.ReflectionUtils;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;


public class LikeQuery extends ComplexQueryBase<String> {

    public static final Boolean LITERALLY = true;
    public static final Boolean NOT_LITERALLY = false;

    private boolean isLiterally;

    public LikeQuery(String field, String value, Boolean isLiterally) {
        super(field, value);
        this.isLiterally = isLiterally;
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Path<?> path, String field,
                                         Iterable<Expression<String>> values) {
        Expression<String> expression = values.iterator().next();
        Class<?> targetFieldType = ReflectionUtils.getFieldType(path.getJavaType(), field);
        if (String.class.equals(targetFieldType)) {
            return Arrays.asList(criteriaBuilder.like(path.get(field), expression));
        } else {
            return Arrays.asList(criteriaBuilder.like(path.get(field).as(String.class), expression));
        }
    }

    @Override
    protected Expression<String> valueToExpress(CriteriaBuilder criteriaBuilder, String input) {
        if (isLiterally) {
            return super.valueToExpress(criteriaBuilder, input);
        } else {
            return super.valueToExpress(criteriaBuilder, "%" + input + "%");
        }
    }
}
