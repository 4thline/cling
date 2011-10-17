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

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.seamless.util.Exceptions;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class EventedValueURI extends EventedValue<URI> {

    final private static Logger log = Logger.getLogger(EventedValueURI.class.getName());

    public EventedValueURI(URI value) {
        super(value);
    }

    public EventedValueURI(Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected URI valueOf(String s) throws InvalidValueException {
        try {
            // These URIs are really defined as 'string' datatype in AVTransport1.0.pdf, but we can try
            // to parse whatever devices give us, like the Roku which sends "unknown url".
            return super.valueOf(s);
        } catch (InvalidValueException ex) {
            log.info("Ignoring invalid URI in evented value '" + s +"': " + Exceptions.unwrap(ex));
            return null;
        }
    }

    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.URI.getDatatype();
    }
}
