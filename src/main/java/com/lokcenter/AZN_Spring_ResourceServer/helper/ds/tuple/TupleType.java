package com.lokcenter.AZN_Spring_ResourceServer.helper.ds.tuple;

/**
 * From https://stackoverflow.com/questions/3642452/java-n-tuple-implementation
 */
public interface TupleType {

    public int size();

    public Class<?> getNthType(int i);

    /**
     * Tuple are immutable objects.  Tuples should contain only immutable objects or
     * objects that won't be modified while part of a tuple.
     *
     * @return Tuple with the given values
     */
    public Tuple createTuple(Object... values);

    public class DefaultFactory {
        public static TupleType create(final Class<?>... types) {
            return new TupleTypeImpl(types);
        }
    }

}
