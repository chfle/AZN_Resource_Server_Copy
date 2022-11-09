package com.lokcenter.AZN_Spring_ResourceServer.helper.ds;

import lombok.*;

import java.io.Serializable;

/**
 * Java Pair Data Structure
 *
 * @version 1.0
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Pair<K, V> implements Serializable {
    /**
     *  Key of pair
     */
    @Setter
    @Getter
    private K key;

    /**
     * Value of pair
     */
    @Setter
    @Getter
    private V value;

    @Override
    public int hashCode() {
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
            if (value != null ? !value.equals(pair.value) : pair.value != null) return false;
            return true;
        }
        return false;
    }
}
