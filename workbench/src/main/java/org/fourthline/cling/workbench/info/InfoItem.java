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
