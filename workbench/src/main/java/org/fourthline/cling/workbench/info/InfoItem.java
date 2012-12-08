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

package org.fourthline.cling.workbench.info;

/**
 * @author Christian Bauer
 */
public class InfoItem {

    private String info;
    private boolean url;
    private Object data;

    public InfoItem(String info) {
        this.info = info;
    }

    public InfoItem(String info, boolean url) {
        this.info = info;
        this.url = url;
    }

    public InfoItem(String prefix, Object data) {
        this(prefix, data, null);
    }

    public InfoItem(String prefix, Object data, Object owner) {
        if (data != null && data instanceof String) {
            String stringData = (String)data;
            if (stringData.length() == 0) {
                this.info = prefix + "<EMPTY>";
            } else {
                this.info = prefix + stringData;
            }
        } else {
            this.info = prefix + data;
        }

        this.data = data;
    }

    public InfoItem(String prefix, Object data, boolean url) {
        this(prefix, data, null, url);
    }

    public InfoItem(String prefix, Object data, Object owner, boolean url) {
        this.info = prefix + data;
        this.url = url;
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public boolean isUrl() {
        return url;
    }

    public InfoItem setData(Object data) {
        this.data = data;
        return this;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return info;
    }
}
