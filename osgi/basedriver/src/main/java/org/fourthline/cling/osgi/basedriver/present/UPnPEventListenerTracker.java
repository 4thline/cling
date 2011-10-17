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
