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

package org.fourthline.cling.mediarenderer;

import org.fourthline.cling.support.shared.CoreLogCategories;
import org.seamless.swing.logging.LogCategory;

import java.util.logging.Level;

public class MediaRendererLogCategories extends CoreLogCategories {

    public MediaRendererLogCategories() {
        super();

        add(new LogCategory("Cling MediaRenderer", new LogCategory.Group[]{

                new LogCategory.Group(
                        "MediaRenderer UPnP services",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.support.renderingcontrol", Level.FINER),
                                new LogCategory.LoggerLevel("org.fourthline.cling.support.avtransport", Level.FINER),
                                new LogCategory.LoggerLevel("org.fourthline.cling.support.connectionmanager", Level.FINER),
                                new LogCategory.LoggerLevel("org.fourthline.cling.support.lastchange", Level.FINER),
                                new LogCategory.LoggerLevel("org.seamless.statemachine", Level.FINER),
                        }
                ),

                new LogCategory.Group(
                        "GStreamer backend",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.mediarenderer.gstreamer", Level.FINER),
                        }
                ),

                new LogCategory.Group(
                        "Display",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.mediarenderer.display", Level.FINER),
                        }
                )
        }));

    }
}

