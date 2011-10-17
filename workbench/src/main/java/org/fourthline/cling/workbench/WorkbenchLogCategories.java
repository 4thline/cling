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

package org.fourthline.cling.workbench;

import org.fourthline.cling.support.shared.CoreLogCategories;
import org.fourthline.cling.support.shared.log.LogView;
import org.seamless.swing.logging.LogCategory;

import java.util.logging.Level;

public class WorkbenchLogCategories extends CoreLogCategories implements LogView.LogCategories {

    public WorkbenchLogCategories() {
        super();

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

        // TODO: Externalize to SPI
        add(new LogCategory("Plugins", new LogCategory.Group[]{

                new LogCategory.Group(
                        "Binary Light",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.workbench.plugins.binarylight", Level.FINER),
                        }
                ),

                new LogCategory.Group(
                        "Content Directory Browser",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.workbench.plugins.contentdirectory", Level.FINER),
                                new LogCategory.LoggerLevel("org.fourthline.cling.support.contentdirectory", Level.FINER),
                                new LogCategory.LoggerLevel("org.seamless.statemachine", Level.FINER),
                        }
                ),
                new LogCategory.Group(
                        "Audio/Video Transport Control Point",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.workbench.plugins.avtransport", Level.FINER),
                                new LogCategory.LoggerLevel("org.fourthline.cling.support.avtransport", Level.FINER),
                                new LogCategory.LoggerLevel("org.seamless.statemachine", Level.FINER),
                        }
                ),
                new LogCategory.Group(
                        "Media Rendering Control",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.workbench.plugins.renderingcontrol", Level.FINER),
                                new LogCategory.LoggerLevel("org.fourthline.cling.support.renderingcontrol", Level.FINER),
                        }
                ),
        }));

    }
}
