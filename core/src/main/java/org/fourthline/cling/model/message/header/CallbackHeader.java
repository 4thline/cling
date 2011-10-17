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

package org.fourthline.cling.model.message.header;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class CallbackHeader extends UpnpHeader<List<URL>> {

    public CallbackHeader() {
        setValue(new ArrayList());
    }

    public CallbackHeader(List<URL> urls) {
        this();
        getValue().addAll(urls);
    }

    public CallbackHeader(URL url) {
        this();
        getValue().add(url);
    }

    public void setString(String s) throws InvalidHeaderException {

        if (s.length() == 0) {
            // Well, no callback URLs are not useful but we have to consider this state
            return;
        }

        if (!s.contains("<") || !s.contains(">")) {
            throw new InvalidHeaderException("URLs not in brackets: " + s);
        }

        s = s.replaceAll("<", "");
        String[] split = s.split(">");
        try {
            List<URL> urls = new ArrayList();
            for (String sp : split) {
                sp = sp.trim();
                if (!sp.startsWith("http://")) {
                    throw new InvalidHeaderException("Can't parse non-http callback URL: " + sp);
                }
                urls.add(new URL(sp));
            }
            setValue(urls);
        } catch (MalformedURLException ex) {
            throw new InvalidHeaderException("Can't parse callback URLs from '" + s + "': " + ex);
        }
    }

    public String getString() {
        StringBuilder s = new StringBuilder();
        for (URL url : getValue()) {
            s.append("<").append(url.toString()).append(">");
        }
        return s.toString();
    }
}
