package com.lokcenter.AZN_Spring_ResourceServer.helper;

import org.apache.commons.codec.binary.Base16;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;

import java.io.*;
import java.util.Base64;

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
