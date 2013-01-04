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

package org.fourthline.cling.support.shared;

import org.seamless.swing.Controller;

import javax.swing.JFrame;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Christian Bauer
 */
public class PlatformApple {

    public static void setup(final Controller<JFrame> appController, String appName) throws Exception {

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        System.setProperty("apple.awt.showGrowBox", "true");

        // Use reflection to avoid compile-time dependency
        Class appClass = Class.forName("com.apple.eawt.Application");
        Object application = appClass.newInstance();
        Class listenerClass = Class.forName("com.apple.eawt.ApplicationListener");
        Method addAppListmethod = appClass.getDeclaredMethod("addApplicationListener", listenerClass);

        // creating and adding a custom adapter/listener to the Application
        Class adapterClass = Class.forName("com.apple.eawt.ApplicationAdapter");
        Object listener = AppListenerProxy.newInstance(adapterClass.newInstance(), appController);
        addAppListmethod.invoke(application, listener);
    }

    static class AppListenerProxy implements InvocationHandler {

        private Controller<JFrame> appController;
        private Object object;

        public static Object newInstance(Object obj, Controller<JFrame> appController) {
            return Proxy.newProxyInstance(
                    obj.getClass().getClassLoader(),
                    obj.getClass().getInterfaces(),
                    new AppListenerProxy(obj, appController)
            );
        }

        private AppListenerProxy(Object obj, Controller<JFrame> appController) {
            this.object = obj;
            this.appController = appController;
        }

        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            Object result = null;
            try {
                if ("handleQuit".equals(m.getName())) {
                    if (appController != null) {
                        appController.dispose();
                        appController.getView().dispose();
                    }
                } else {
                    result = m.invoke(object, args);
                }
            } catch (Exception e) {
                // Ignore
            }
            return result;
        }

    }

}
