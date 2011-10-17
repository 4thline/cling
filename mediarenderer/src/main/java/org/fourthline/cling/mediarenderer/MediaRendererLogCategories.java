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

