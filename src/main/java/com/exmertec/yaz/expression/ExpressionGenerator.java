package com.exmertec.yaz.expression;

import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

public interface ExpressionGenerator<T> {
    Expression<T> generate(CriteriaBuilder criteriaBuilder, CommonAbstractCriteria criteria);
}
