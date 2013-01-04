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
package org.fourthline.cling.mediarenderer;

import org.fourthline.cling.support.shared.AWTExceptionHandler;
import org.fourthline.cling.support.shared.PlatformApple;
import org.seamless.util.OS;

import javax.swing.SwingUtilities;

public class MediaRenderer {

    public static final String APPNAME = "Cling MediaRenderer";
    public static final MediaRendererController APP;

    static {
        MediaRendererController app = null;
        try {
            app = new MediaRendererController();
        } catch (Throwable t) {
            new AWTExceptionHandler().handle(t);
        }
        APP = app;
    }

    public static void main(final String[] args) throws Exception {

        if (OS.checkForMac())
            PlatformApple.setup(APP, APPNAME);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                APP.getView().setVisible(true);
                APP.onViewReady(args);
            }
        });

    }

}
