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

package org.fourthline.cling.mediarenderer.gstreamer;

import org.gstreamer.PluginFeature;
import org.gstreamer.Registry;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.seamless.util.MimeType;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class GstConnectionManagerService extends ConnectionManagerService {

    final private static Logger log = Logger.getLogger(GstConnectionManagerService.class.getName());

    public GstConnectionManagerService() {
        List<PluginFeature> types = Registry.getDefault().getPluginFeatureListByPlugin("typefindfunctions");
        for (PluginFeature type : types) {
            try {
                MimeType mt = MimeType.valueOf(type.getName());
                log.fine("Supported MIME type: " + mt);
                sinkProtocolInfo.add(new ProtocolInfo(mt));
            } catch (IllegalArgumentException ex) {
                log.finer("Ignoring invalid MIME type: " + type.getName());
            }
        }
        log.info("Supported MIME types: " + sinkProtocolInfo.size());
    }

}
