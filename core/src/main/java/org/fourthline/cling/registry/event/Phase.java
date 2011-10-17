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

package org.fourthline.cling.registry.event;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Christian Bauer
 */

public interface Phase {

    public static AnnotationLiteral<Alive> ALIVE = new AnnotationLiteral<Alive>() {
    };

    public static AnnotationLiteral<Complete> COMPLETE = new AnnotationLiteral<Complete>() {
    };

    public static AnnotationLiteral<Byebye> BYEBYE = new AnnotationLiteral<Byebye>() {
    };

    public static AnnotationLiteral<Updated> UPDATED = new AnnotationLiteral<Updated>() {
    };


    @Qualifier
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    public @interface Alive {

    }

    @Qualifier
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    public @interface Complete {

    }

    @Qualifier
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    public @interface Byebye {

    }

    @Qualifier
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    public @interface Updated {

    }

}
