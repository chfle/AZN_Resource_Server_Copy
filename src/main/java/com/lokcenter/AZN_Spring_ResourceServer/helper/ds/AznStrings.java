package com.lokcenter.AZN_Spring_ResourceServer.helper.ds;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Strings
 *
 * Convert Serializable classes from and to Strings
 *
 * @version 1.3
 */
public class AznStrings {
    /**
     * Convert Serializable to MD2 represented String
     *
     * @param o Serializable Object
     * @return String
     */
    public static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject( o );

            System.out.println(DigestUtils.md2Hex(baos.toByteArray()));
            return DigestUtils.md2Hex(baos.toByteArray());
        }
    }

}
