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

import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.model.state.StateVariableAccessor;

import java.util.logging.Logger;

/**
 * @author Bruce Green
 */
class UPnPStateVariableAccessor extends StateVariableAccessor {

    final private static Logger log = Logger.getLogger(UPnPStateVariableAccessor.class.getName());

    private UPnPStateVariable variable;

    public UPnPStateVariableAccessor(UPnPStateVariable variable) {
        this.variable = variable;
    }

    @Override
    public Class<?> getReturnType() {
        log.entering(this.getClass().getName(), "getReturnType", new Object[]{});
        return variable.getJavaDataType();
    }

    @Override
    public Object read(Object serviceImpl) throws Exception {
        log.entering(this.getClass().getName(), "read", new Object[]{serviceImpl});
        return null;
    }
}
