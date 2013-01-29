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

package org.fourthline.cling.workbench;

import org.fourthline.cling.support.shared.CoreLogCategories;
import org.fourthline.cling.support.shared.log.LogView;
import org.fourthline.cling.workbench.plugins.avtransport.AVTransportControlPoint;
import org.fourthline.cling.workbench.plugins.binarylight.controlpoint.SwitchPowerControlPoint;
import org.fourthline.cling.workbench.plugins.contentdirectory.ContentDirectoryControlPoint;
import org.fourthline.cling.workbench.plugins.renderingcontrol.RenderingControlPoint;
import org.seamless.swing.logging.LogCategory;

import java.util.logging.Level;

public class WorkbenchLogCategories extends CoreLogCategories implements LogView.LogCategories {

    public WorkbenchLogCategories() {
        super();

        /* TODO: Move to bridge module
        add(new LogCategory("Bridge", new LogCategory.Group[]{

                new LogCategory.Group(
                        "All",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.bridge", Level.FINEST),
                                new LogCategory.LoggerLevel("org.jboss.resteasy", Level.FINEST),
                                new LogCategory.LoggerLevel("org.eclipse.jetty", Level.FINEST),
                        }
                ),
        }));
        */

        // TODO: Externalize to SPI
        add(new LogCategory("Plugins", new LogCategory.Group[]{

            new LogCategory.Group(
                "Binary Light",
                new LogCategory.LoggerLevel[]{
                    new LogCategory.LoggerLevel(SwitchPowerControlPoint.LOGGER.getName(), Level.FINE),
                    new LogCategory.LoggerLevel("org.fourthline.cling.workbench.plugins.binarylight", Level.FINE),
                }
            ),

            new LogCategory.Group(
                ContentDirectoryControlPoint.LOGGER.getName(),
                new LogCategory.LoggerLevel[]{
                    new LogCategory.LoggerLevel(ContentDirectoryControlPoint.LOGGER.getName(), Level.FINE),
                    new LogCategory.LoggerLevel("org.fourthline.cling.support.contentdirectory", Level.FINER),
                    new LogCategory.LoggerLevel("org.seamless.statemachine", Level.FINER),
                }
            ),
            new LogCategory.Group(
                AVTransportControlPoint.LOGGER.getName(),
                new LogCategory.LoggerLevel[]{
                    new LogCategory.LoggerLevel(AVTransportControlPoint.LOGGER.getName(), Level.FINE),
                    new LogCategory.LoggerLevel("org.fourthline.cling.support.avtransport", Level.FINER),
                    new LogCategory.LoggerLevel("org.fourthline.cling.support.lastchange", Level.FINE),
                    new LogCategory.LoggerLevel("org.seamless.statemachine", Level.FINER),
                }
            ),
            new LogCategory.Group(
                RenderingControlPoint.LOGGER.getName(),
                new LogCategory.LoggerLevel[]{
                    new LogCategory.LoggerLevel(RenderingControlPoint.LOGGER.getName(), Level.FINE),
                    new LogCategory.LoggerLevel("org.fourthline.cling.support.renderingcontrol", Level.FINER),
                    new LogCategory.LoggerLevel("org.fourthline.cling.support.lastchange", Level.FINE),
                }
            ),
        }));

    }
}
