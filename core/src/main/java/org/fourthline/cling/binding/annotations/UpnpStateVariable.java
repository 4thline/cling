/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.fourthline.cling.binding.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UpnpStateVariable {

    String name() default "";
    String datatype() default "";

    String defaultValue() default "";

    // String types
    String[] allowedValues() default {};
    Class allowedValuesEnum() default void.class;

    // Numeric types
    long allowedValueMinimum() default 0;
    long allowedValueMaximum() default 0;
    long allowedValueStep() default 1;

    // Dynamic
    Class allowedValueProvider() default void.class;
    Class allowedValueRangeProvider() default void.class;

    boolean sendEvents() default true;
    int eventMaximumRateMilliseconds() default 0;
    int eventMinimumDelta() default 0;

}
