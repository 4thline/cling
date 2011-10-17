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

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.types.InvalidValueException;

import java.util.ArrayList;

/**
 * @author Christian Bauer
 */
public class ProtocolInfos extends ArrayList<ProtocolInfo> {

    public ProtocolInfos(ProtocolInfo... info) {
        for (ProtocolInfo protocolInfo : info) {
            add(protocolInfo);
        }
    }

    public ProtocolInfos(String s) throws InvalidValueException {
        String[] infos = ModelUtil.fromCommaSeparatedList(s);
        if (infos != null)
            for (String info : infos)
                add(new ProtocolInfo(info));
    }

    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(toArray(new ProtocolInfo[size()]));
    }
}
