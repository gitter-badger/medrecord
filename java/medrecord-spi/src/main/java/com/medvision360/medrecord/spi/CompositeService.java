package com.medvision360.medrecord.spi;

public interface CompositeService<T> {
    public void addDelegate(T delegate);
}
