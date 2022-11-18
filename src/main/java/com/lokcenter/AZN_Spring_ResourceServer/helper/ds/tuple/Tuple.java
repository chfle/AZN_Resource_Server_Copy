package com.lokcenter.AZN_Spring_ResourceServer.helper.ds.tuple;

/**
 * Tuple Data Structure
 *
 * @version 1.o
 */
public interface Tuple {
    public TupleType getType();
    public int size();
    public <T> T getNthValue(int i);
}
