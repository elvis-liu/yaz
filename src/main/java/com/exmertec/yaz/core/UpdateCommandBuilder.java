package com.exmertec.yaz.core;

public interface UpdateCommandBuilder<T> {
    UpdateCommandBuilder<T> update(String fieldName, Object value);

    int applyWithoutClearCache();

    int apply(boolean cleanCache);
}
