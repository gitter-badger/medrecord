package com.medvision360.medrecord.spi;

public interface SoftDeletable
{
    public boolean isDeleted();
    public void setDeleted(boolean deleted);
}
