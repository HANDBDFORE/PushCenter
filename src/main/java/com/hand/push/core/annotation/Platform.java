package com.hand.push.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 3:04 PM
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Platform {
    public String platformName();

}
