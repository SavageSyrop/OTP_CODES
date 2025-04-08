package ru.otp.entities;

import java.lang.reflect.ParameterizedType;

public interface Indexable<T> {
    void setIndex(T index);

    T getIndex();

    default Class<?> getIndexClass() {
        ParameterizedType paramType;
        paramType = (ParameterizedType) this.getClass().getGenericInterfaces()[0];
        return paramType.getActualTypeArguments()[0].getClass();
    }
}
