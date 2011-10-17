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
 * Sending <em>BYEBYE</em> notification messages for a registered local device.
 *
 * @author Christian Bauer
 */
public class SendingNotificationByebye extends SendingNotification {

    final private static Logger log = Logger.getLogger(SendingNotification.class.getName());

    public SendingNotificationByebye(UpnpService upnpService, LocalDevice device) {
        super(upnpService, device);
    }

    // The UDA 1.0 spec says "a message corresponding to /each/ of the ssd:alive messages" but
    // it's not clear if that means the "required" messages according to the tables only or if
    // it includes the triple (or whatever) repeated messages that have been sent to protect
    // against networking problems. It also says, a little later, that "each of the messages should
    // be send more than once". So we are also sending them three times - hell, why not pollute the
    // network with useless stuff, that is going to make this more reliable for sure...

    // In other words: The superclass method is fine even for byebye.

    @Override
    protected void execute() {
        log.fine("Sending byebye messages ("+getBulkRepeat()+" times) for: " + getDevice());
        super.execute();
    }

    protected NotificationSubtype getNotificationSubtype() {
        return NotificationSubtype.BYEBYE;
    }

}