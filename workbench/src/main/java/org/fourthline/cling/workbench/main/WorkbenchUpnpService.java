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

package org.fourthline.cling.workbench.main;

import org.fourthline.cling.ManagedUpnpService;
import org.fourthline.cling.workbench.bridge.backend.Bridge;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Specializes;

/**
 * Weld is buggy: https://issues.jboss.org/browse/WELD-904
 *
 * @author Christian Bauer
 */
@Alternative
@Specializes
public class WorkbenchUpnpService extends ManagedUpnpService {

    protected Bridge bridge;

    @Override
    public void start(Start start) {
        super.start(start);
        bridge = new Bridge(this);
    }


    @Override
    public void shutdown(Shutdown shutdown) {
        if (bridge != null)
            bridge.stop(true);
        super.shutdown(shutdown);
    }


}
