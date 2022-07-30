package com.lokcenter.AZN_Spring_ResourceServer.helper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Make all classes that need a user
 */
@Target(ElementType.TYPE)
public @interface UserDepending {

}
