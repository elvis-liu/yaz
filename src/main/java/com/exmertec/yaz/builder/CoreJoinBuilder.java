package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.JoinBuilder;
import com.exmertec.yaz.core.JoinQueryBuilder;

public class CoreJoinBuilder implements JoinBuilder {
    private String joinByField;

    public CoreJoinBuilder(String joinByField) {
        this.joinByField = joinByField;
    }

    @Override
    public JoinQueryBuilder ofRelationField(String fieldName) {
        return new CoreJoinQueryBuilder(joinByField, fieldName);
    }
}
