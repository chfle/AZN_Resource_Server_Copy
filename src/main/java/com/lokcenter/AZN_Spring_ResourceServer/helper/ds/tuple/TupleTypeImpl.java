package com.lokcenter.AZN_Spring_ResourceServer.helper.ds.tuple;

/**
 * From https://stackoverflow.com/questions/3642452/java-n-tuple-implementation
 */
class TupleTypeImpl implements TupleType {
    final Class<?>[] types;

    TupleTypeImpl(Class<?>[] types) {
        this.types = (types != null ? types : new Class<?>[0]);
    }

    /**
     * Get types size
     * @return
     */
    public int size() {
        return types.length;
    }

    /**
     * public <T> Class<T> getNthType(int i)
     * @param i index
     *
     * @return index value
     */
    public Class<?> getNthType(int i) {
        return types[i];
    }

    /**
     * Create new tuple with nth values
     * @param values values
     *
     * @return new nth tuple
     */
    public Tuple createTuple(Object... values) {
        if ((values == null && types.length == 0) ||
                (values != null && values.length != types.length)) {
            throw new IllegalArgumentException(
                    "Expected "+types.length+" values, not "+
                            (values == null ? "(null)" : values.length) + " values");
        }

        if (values != null) {
            for (int i = 0; i < types.length; i++) {
                final Class<?> nthType = types[i];
                final Object nthValue = values[i];
                if (nthValue != null && ! nthType.isAssignableFrom(nthValue.getClass())) {
                    throw new IllegalArgumentException(
                            "Expected value #"+i+" ('"+
                                    nthValue+"') of new Tuple to be "+
                                    nthType+", not " +
                                    nthValue.getClass());
                }
            }
        }

        return new TupleImpl(this, values);
    }
}
