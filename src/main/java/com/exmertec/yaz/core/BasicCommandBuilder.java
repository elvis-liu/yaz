package com.exmertec.yaz.core;

import javax.persistence.LockModeType;

public interface BasicCommandBuilder<T> {
    BasicCommandBuilder<T> lockBy(LockModeType lockModeType);

    T querySingle();
}
