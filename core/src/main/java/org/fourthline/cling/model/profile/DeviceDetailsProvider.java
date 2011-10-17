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

package org.fourthline.cling.model.profile;

import org.fourthline.cling.model.meta.DeviceDetails;

/**
 * Provides custom device details metadata based on control point profile.
 * <p>
 * Use this instead of {@link DeviceDetails} when you create a
 * {@link org.fourthline.cling.model.meta.LocalDevice} if dynamic metadata is
 * required - e.g. when your control points expect different DLNA capabilities
 * or if they are otherwise incompatible with the standard metadata of the
 * service you provide. You can then provide custom metadata for each
 * control point based on the detected control point information.
 * </p>
 * <p>
 * Don't forget to provide a default, that is, if none of your conditions match
 * you still have to provide a minimal {@link DeviceDetails} instance for
 * generic control points.
 * </p>
 *
 * @author Mario Franco
 * @author Christian Bauer
 */
public interface DeviceDetailsProvider {
    DeviceDetails provide(ControlPointInfo info);
}
