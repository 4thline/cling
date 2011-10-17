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

package org.fourthline.cling.support.shared;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Christian Bauer
 */
public class NewPlatformApple {

    public static void setup(final ShutdownHandler shutdownHandler, String appName) throws Exception {

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
        Object listener = AppListenerProxy.newInstance(adapterClass.newInstance(), shutdownHandler);
        addAppListmethod.invoke(application, listener);
    }

    static class AppListenerProxy implements InvocationHandler {

        private ShutdownHandler shutdownHandler;
        private Object object;

        public static Object newInstance(Object obj, ShutdownHandler shutdownHandler) {
            return Proxy.newProxyInstance(
                    obj.getClass().getClassLoader(),
                    obj.getClass().getInterfaces(),
                    new AppListenerProxy(obj, shutdownHandler)
            );
        }

        private AppListenerProxy(Object obj, ShutdownHandler shutdownHandler) {
            this.object = obj;
            this.shutdownHandler = shutdownHandler;
        }

        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            Object result = null;
            try {
                if ("handleQuit".equals(m.getName())) {
                    if (shutdownHandler != null) {
                        shutdownHandler.shutdown();
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
