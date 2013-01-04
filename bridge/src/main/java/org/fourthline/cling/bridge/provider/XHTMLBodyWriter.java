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

package org.fourthline.cling.bridge.provider;

import org.seamless.util.io.IO;
import org.seamless.xhtml.XHTML;
import org.seamless.xhtml.XHTMLParser;
import org.seamless.xml.ParserException;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Christian Bauer
 */
@Provider
@Produces("application/xhtml+xml")
public class XHTMLBodyWriter extends BridgeProvider implements MessageBodyWriter<XHTML> {

    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return XHTML.class.isAssignableFrom(aClass);
    }

    public long getSize(XHTML xhtml, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(XHTML xhtml, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> headers, OutputStream outputStream) throws IOException, WebApplicationException {

        XHTMLParser parserXHTML = new XHTMLParser();
        try {
            String result = parserXHTML.print(xhtml, 4, true);
            IO.writeUTF8(outputStream, result);
        } catch (ParserException ex) {
            throw new IOException(ex);
        }
    }
}
