package com.exmertec.yaz.core;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public interface Selection {
    Expression toExpression(Root root, CriteriaBuilder criteriaBuilder);
}
