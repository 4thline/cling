/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
