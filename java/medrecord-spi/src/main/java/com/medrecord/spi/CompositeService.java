package com.medrecord.spi;

public interface CompositeService<T> {
    public void addDelegate(T delegate);
}
