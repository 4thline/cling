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

package org.fourthline.cling.osgi.basedriver.present;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.logging.Logger;

/**
 * For debugging purposes, ServiceTracker is sufficient in a production environment.
 *
 * @author Bruce Green
 */
class UPnPEventListenerTracker extends ServiceTracker  {

    final private static Logger log = Logger.getLogger(UPnPEventListenerTracker.class.getName());
	
	public UPnPEventListenerTracker(BundleContext context, Filter filter, ServiceTrackerCustomizer customizer) {
		super(context, filter, null);
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		log.entering(this.getClass().getName(), "addingService", new Object[] { reference });

        return super.addingService(reference);
	}
}
