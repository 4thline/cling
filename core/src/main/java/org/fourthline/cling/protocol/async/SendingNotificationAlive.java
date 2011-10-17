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

package org.fourthline.cling.protocol.async;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.types.NotificationSubtype;

import java.util.logging.Logger;

/**
 * Sending <em>ALIVE</em> notification messages for a registered local device.
 *
 * @author Christian Bauer
 */
public class SendingNotificationAlive extends SendingNotification {

    final private static Logger log = Logger.getLogger(SendingNotification.class.getName());

    public SendingNotificationAlive(UpnpService upnpService, LocalDevice device) {
        super(upnpService, device);
    }

    @Override
    protected void execute() {
        log.fine("Sending alive messages ("+getBulkRepeat()+" times) for: " + getDevice());
        super.execute();
    }

    protected NotificationSubtype getNotificationSubtype() {
        return NotificationSubtype.ALIVE;
    }

}
