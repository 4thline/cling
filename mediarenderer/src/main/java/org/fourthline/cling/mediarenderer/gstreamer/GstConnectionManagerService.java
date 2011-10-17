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
