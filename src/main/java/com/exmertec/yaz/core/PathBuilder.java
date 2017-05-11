package com.exmertec.yaz.core;

public interface PathBuilder {
    QueryBuilder field(String field);

    PathBuilder withEmbedded(String embeddedField);
}
