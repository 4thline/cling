/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.seamless.xml.XmlPullParserUtils;

import javax.enterprise.inject.Alternative;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Implementation based on the <em>Xml Pull Parser</em> XML processing API.
 * <p>
 * This processor extends {@link PullGENAEventProcessorImpl}, it will always
 * first try to read messages regularly with the superclass' methods before
 * trying to recover from a failure.
 * </p>
 * <p>
 * When the superclass can't read the message, this processor will try to
 * recover from broken XML by for example, detecting wrongly encoded XML entities,
 * and working around other vendor-specific bugs caused by incompatible UPnP
 * stacks in the wild.
 * </p>
 * <p>
 * This processor will also return partial results, if at least a single
 * state variable value could be recovered from the (broken) event XML.
 * </p>
 *
 * @author Michael Pujos
 */
@Alternative
public class RecoveringGENAEventProcessorImpl extends PullGENAEventProcessorImpl {

    private static Logger log = Logger.getLogger(GENAEventProcessor.class.getName());

    public void readBody(IncomingEventRequestMessage requestMessage) throws UnsupportedDataException {
        try {
            super.readBody(requestMessage);
        } catch (UnsupportedDataException ex) {

            // Can't recover from this
            if (!requestMessage.isBodyNonEmptyString())
                throw ex;

            log.warning("Trying to recover from invalid GENA XML event: " + ex);

            // Some properties may have been read at this point, so reset the list
            requestMessage.getStateVariableValues().clear();

            String body = getMessageBody(requestMessage);

            String fixedBody = fixXMLEncodedLastChange(
                XmlPullParserUtils.fixXMLEntities(body)
            );

            try {
                // Try again, if this fails, we are done...
                requestMessage.setBody(fixedBody);
                super.readBody(requestMessage);
            } catch (UnsupportedDataException ex2) {
                // Check if some properties were read
                if (requestMessage.getStateVariableValues().isEmpty()) {
                    // Throw the initial exception containing unmodified XML
                    throw ex;
                }
                log.warning("Partial read of GENA event properties (probably due to truncated XML)");
            }
        }
    }

    protected String fixXMLEncodedLastChange(String xml) {
        Pattern pattern = Pattern.compile("<LastChange>(.*)</LastChange>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);

        if (matcher.find() && matcher.groupCount() == 1) {

            String xmlEncodedLastChange = matcher.group(1);

            // TODO: UPNP VIOLATION: Philips NP2900 inserts garbage HTML, try to fix it
            String fixedXmlEncodedLastChange = xmlEncodedLastChange.replaceAll("<>", "");

            if (fixedXmlEncodedLastChange.equals(xmlEncodedLastChange)) {
                return xml;
            }

            log.warning("Deleted invalid characters in LastChange event");

            return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<e:propertyset xmlns:e=\"urn:schemas-upnp-org:event-1-0\">" +
                "<e:property>" +
                "<LastChange>" +
                fixedXmlEncodedLastChange +
                "</LastChange>" +
                "</e:property>" +
                "</e:propertyset>";
        }
        return xml;
    }
}
