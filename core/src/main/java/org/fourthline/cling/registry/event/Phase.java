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

		/**
		 * 
		 */
		private static final long serialVersionUID = -831225876131056634L;
    };

    public static AnnotationLiteral<Complete> COMPLETE = new AnnotationLiteral<Complete>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6513651765612642029L;
    };

    public static AnnotationLiteral<Byebye> BYEBYE = new AnnotationLiteral<Byebye>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7945618889517154102L;
    };

    public static AnnotationLiteral<Updated> UPDATED = new AnnotationLiteral<Updated>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1484213289668518597L;
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
